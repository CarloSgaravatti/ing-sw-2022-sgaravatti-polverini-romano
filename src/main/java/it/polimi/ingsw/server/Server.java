package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.DuplicateNicknameException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.persistence.PersistenceGameInfo;
import it.polimi.ingsw.server.persistence.SaveGame;
import it.polimi.ingsw.utils.Triplet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server class is the main class of the server side. It accepts connections from clients and delegate them to a
 * ClientConnection instance for each client. The server is also responsible for the global lobby, where clients are
 * asked to choose a game to enter in, for creating GameLobby instances and for retrieving from the disk the participants
 * of the previous saved games (so when a clients connect, it is notified if he has a previous saved game that he
 * was participating). Each GameLobby (and so each game) is identified by an id, this permit to distinguish games that
 * are currently running and also games that are saved on disk.
 */
public class Server implements Runnable{
    private final ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<Integer, GameLobby> gamesMap;
    private final Map<String, ClientConnection> waitingPlayersWithNoGame;
    private final Map<Integer, Map<String, ClientConnection>> waitingPlayersPerGameMap;
    private final Map<String, ClientConnection> clientsConnected;
    private Map<Integer, String[]> gamesParticipantsToBeRestored = new HashMap<>();
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        gamesMap = new ConcurrentHashMap<>();
        waitingPlayersPerGameMap = new ConcurrentHashMap<>();
        waitingPlayersWithNoGame = new ConcurrentHashMap<>();
        clientsConnected = new ConcurrentHashMap<>();
    }

    /**
     * Continue looping accepting socket connections from clients and submitting them to a SocketClientConnection per
     * connection. Before this, the server retrieve from the disk the participants of each previously saved game
     */
    @Override
    public void run() {
        try {
            gamesParticipantsToBeRestored = SaveGame.getParticipants();
        } catch (IOException e) {
            System.out.println("Didn't find any previous saved game on disk");
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(new SocketClientConnection(socket, this));
            } catch (IOException e) {
                System.err.println("Error in connection");
            }
        }
    }

    /**
     * The server main, that creates the instance of the server after a correct port is inserted
     *
     * @param args the arguments of the program
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int port = 12345;
        boolean portCorrect = false;
        System.out.println("Insert the port: ");
        while (!portCorrect) {
            try {
                System.out.print("> ");
                port = Integer.parseInt(sc.next()); //did this because of InputMismatchException
                if (port < 1024 || port > 65535) System.out.println("Port must be between 1024 and 65535, retry");
                else portCorrect = true;
            } catch (NumberFormatException e) {
                System.out.println("Port must be a number, retry");
            }
        }
        try {
            Server server = new Server(port);
            System.out.println("Server started on port " + port);
            server.run();
        } catch (IOException e) {
            System.err.println("Couldn't start the server, the application will now close");
        }
    }

    /**
     * Delete the game from the games map and also from the disk, if the game was already saved
     *
     * @param gameId the id of the game to delete
     */
    protected synchronized void deleteGame(int gameId) {
        Map<String, ClientConnection> participants = gamesMap.get(gameId).getClients();
        gamesMap.remove(gameId);
        waitingPlayersPerGameMap.remove(gameId);
        gamesParticipantsToBeRestored.remove(gameId);
        for (String clientName : participants.keySet()) {
            participants.get(clientName).setSetupDone(false);
        }
        deleteFile(gameId);
    }

    /**
     * Delete a saved but not restored game from the disk if a participant decide to not resume the game. The id of the
     * saved game is obtained by searching in the participants of the saved games
     *
     * @param client the name of the client that decided to delete the saved game
     */
    protected synchronized void deleteSavedGame(String client) {
        Optional<PersistenceGameInfo> persistenceGameInfo = getPreviousGame(client);
        persistenceGameInfo.ifPresent(game -> {
            if (gamesMap.containsKey(game.getGameId())) {
                gamesMap.get(game.getGameId()).onOtherPlayerDeleteChoice(client);
            } else {
                gamesParticipantsToBeRestored.remove(game.getGameId());
                deleteFile(game.getGameId());
            }
        });
    }

    /**
     * Delete the file of a saved game (with the specified id) on the disk
     *
     * @param gameId the id of the game
     */
    private void deleteFile(int gameId) {
        SaveGame.deletePersistenceData(gameId);
        saveParticipants();
    }

    /**
     * Register a connection from a client that have the specified nickname
     *
     * @param clientName the nickname of the client
     * @param client the connection with the client
     * @throws DuplicateNicknameException if the nickname is already taken from someone else that is connected to the server
     */
    public synchronized void registerConnection(String clientName, ClientConnection client) throws DuplicateNicknameException {
        if (clientsConnected.containsKey(clientName)) {
            throw new DuplicateNicknameException();
        }
        clientsConnected.put(clientName, client);
        System.out.println("Registered " + clientName);
    }

    /**
     * Remove the connection with the client that have the specified nickname
     *
     * @param clientName the nickname of the client
     */
    public void deregisterConnection(String clientName) {
        clientsConnected.remove(clientName);
        waitingPlayersWithNoGame.remove(clientName);
        for(GameLobby lobby: gamesMap.values()) {
            if (lobby.getClients().containsKey(clientName)) {
                lobby.onDisconnection(clientName);
                return;
            }
        }
    }

    /**
     * Insert the client on the global lobby of server. The server wil send to the client a message that contains the
     * number of not started and not restored games and also details about them
     *
     * @param client the connection with the client
     * @param clientName the nickname of the client
     */
    public void globalLobby(ClientConnection client, String clientName) {
        waitingPlayersWithNoGame.putIfAbsent(clientName, client);
        if (checkPreviousGames(client, clientName)) return;
        //If there aren't games that are not started, server requests to insert the number of players
        //for a game and creates a new game.
        //If there are games that aren't started, server requests to client what game he wants to play or
        //if he wants to create a new game (and set the number of players)
        ServerMessageHeader header = new ServerMessageHeader("GlobalLobby", ServerMessageType.SERVER_MESSAGE);
        MessagePayload payload = new MessagePayload();
        if (waitingPlayersPerGameMap.isEmpty()) {
            payload.setAttribute("NotStartedGames", 0);
            payload.setAttribute("GamesInfo", new HashMap<>());
        } else {
            List<Integer> notRestoredGames = waitingPlayersPerGameMap.keySet().stream()
                    .filter(gameId -> !gamesMap.get(gameId).isGameRestored()).toList();
            payload.setAttribute("NotStartedGames", notRestoredGames.size());
            Map<Integer, Triplet<Integer, Boolean, String[]>> gamesToSendMap = new HashMap<>();
            for (Integer gameId: notRestoredGames) {
                Triplet<Integer, Boolean, String[]> gameInfo = new Triplet<>();
                gameInfo.setFirst(gamesMap.get(gameId).getNumPlayers());
                gameInfo.setSecond(gamesMap.get(gameId).isExpertGame());
                gameInfo.setThird(gamesMap.get(gameId).getGameParticipants());
                gamesToSendMap.put(gameId, gameInfo);
            }
            payload.setAttribute("GamesInfo", gamesToSendMap);
        }
        client.asyncSend(new MessageFromServer(header, payload));
    }

    /**
     * Returns true if the client was participating in a not finished game saved on disk, otherwise false
     *
     * @param client the connection with the client
     * @param clientName the  ickname of the client
     * @return true if the client was participating in a not finished game saved on disk, otherwise false
     */
    private boolean checkPreviousGames(ClientConnection client, String clientName) {
        Optional<PersistenceGameInfo> previousGame = getPreviousGame(clientName);
        if (previousGame.isPresent()) {
            System.out.println("Found game " + previousGame.get().getGameId() + " for player " + clientName);
            ServerMessageHeader header = new ServerMessageHeader("PreviousGameChoice", ServerMessageType.SERVER_MESSAGE);
            MessagePayload payload = new MessagePayload();
            Triplet<Integer, Boolean, String[]> previousGameInfo = previousGame.get().getGameInfo();
            payload.setAttribute("NumPlayers", previousGameInfo.getFirst());
            payload.setAttribute("Rules", previousGameInfo.getSecond());
            payload.setAttribute("Participants", previousGameInfo.getThird());
            client.asyncSend(new MessageFromServer(header, payload));
            return true;
        }
        return false;
    }

    /**
     * Returns the persistence data of the not finished game on the disk that the specified client was participating if
     * there is such a game, otherwise it returns an empty optional
     *
     * @param clientName the nickname of the client
     * @return the persistence data of the not finished game on the disk that the specified client was participating if
     *      there is such a game, otherwise an empty optional
     */
    private Optional<PersistenceGameInfo> getPreviousGame(String clientName) {
        for (Integer gameId: gamesParticipantsToBeRestored.keySet()) {
            if (Arrays.stream(gamesParticipantsToBeRestored.get(gameId)).toList().contains(clientName)) {
                try {
                    return Optional.ofNullable(SaveGame.getPersistenceData(gameId));
                } catch (IOException e) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Insert the specified client in the game lobby that have the specified id. If the lobby does not exist or if the
     * game with that id is already started, an error will be sent to the client
     *
     * @param gameId the id of the game
     * @param client the connection with the client
     * @param clientName the nickname of the client
     */
    public synchronized void gameLobby(int gameId, ClientConnection client, String clientName) {
        if (!gamesMap.containsKey(gameId)) {
            handleLobbyError(ErrorMessageType.INVALID_REQUEST_GAME_NOT_FOUND, client, clientName);
        } else if(gamesMap.get(gameId).isStarted()) {
            handleLobbyError(ErrorMessageType.INVALID_REQUEST_GAME_ALREADY_STARTED, client, clientName);
        } else {
            gamesMap.get(gameId).insertInLobby(clientName, client);
            client.setSetupDone(true);
            waitingPlayersWithNoGame.remove(clientName);
            if (gamesMap.get(gameId).isStarted()) gamesParticipantsToBeRestored.remove(gameId);
        }
    }

    /**
     * Handles a lobby error if the clients request to participate in a not found game or in a started game
     *
     * @param error the type of lobby error
     * @param client the connection with the client
     * @param clientName the nickname of the client
     */
    private void handleLobbyError(ErrorMessageType error,  ClientConnection client, String clientName) {
        client.sendError(error, "The game lobby requested was not found");
        globalLobby(client, clientName);
    }

    /**
     * Creates a new game with the specified number of players and rules and returns the id of the created game
     *
     * @param numPlayers the number of players of the game
     * @param isExpertGame the rules of the game
     * @return the id of the created game
     */
    public synchronized int createGame(int numPlayers, boolean isExpertGame) {
        //creates a new game of numPlayers number of players, assigns it a unique identifier, adds it to the games map
        //and to the waiting players per game map (it also creates the corresponding empty hash map) but not to the
        //games participants map
        int newGameId = getFirstFreeId();
        System.out.println("created a new game with id = " + newGameId);
        gamesMap.put(newGameId, new GameLobby(newGameId, numPlayers, isExpertGame, this, false));
        waitingPlayersPerGameMap.put(newGameId, new HashMap<>());
        return newGameId;
    }

    /**
     * Restore a previously saved game on disk where that contains the specified client in the participants and returns
     * the id of that game. There is at maximum one saved game for each nickname
     *
     * @param clientName the nickname of the client
     * @return the id of the restored game
     * @throws NoSuchElementException if there isn't such a game on disk
     */
    public synchronized int restoreGameOfClient(String clientName) throws NoSuchElementException {
        Optional<PersistenceGameInfo> previousGame = getPreviousGame(clientName);
        if (previousGame.isEmpty()) throw new NoSuchElementException();
        PersistenceGameInfo persistenceGameInfo = previousGame.get();
        int gameId = persistenceGameInfo.getGameId();
        Triplet<Integer, Boolean, String[]> gameInfo = persistenceGameInfo.getGameInfo();
        if (!gamesMap.containsKey(gameId)) {
            System.out.println("Restored game " + gameId);
            gamesMap.put(gameId, new GameLobby(gameId, gameInfo.getFirst(), gameInfo.getSecond(), this, true));
            waitingPlayersPerGameMap.put(gameId, new HashMap<>());
        }
        return gameId;
    }

    /**
     * Save the participants of all active games and saved but not restored games in a separated file on the disk in order
     * to retrieve the more quickly. This save is done asynchronously, and it is done everytime a new game starts and
     * everytime a started or saved game is deleted
     */
    public void saveParticipants() {
        new Thread(() -> {
            Map<Integer, String[]> gamesParticipants = new HashMap<>();
            for (Integer gameId : gamesParticipantsToBeRestored.keySet()) {
                gamesParticipants.put(gameId, gamesParticipantsToBeRestored.get(gameId));
            }
            for (Integer gameId: gamesMap.keySet()) {
                if (gamesMap.get(gameId).isStarted()) {
                    gamesParticipants.put(gameId, gamesMap.get(gameId).getGameParticipants());
                }
            }
            gamesParticipants.keySet().forEach(System.out::println);
            try {
                SaveGame.saveGameParticipants(gamesParticipants);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Returns the first free game id on which a new game can be created.
     *
     * @return the first free game id on which a new game can be created.
     */
    private int getFirstFreeId() {
        int currentMaxId = Math.max(gamesMap.keySet().stream().max(Comparator.comparingInt(id -> id)).orElse(0),
                gamesParticipantsToBeRestored.keySet().stream().max(Comparator.comparingInt(id -> id)).orElse(0));
        for (int i = 1; i < currentMaxId; i++) {
            if (!gamesMap.containsKey(i) && !gamesParticipantsToBeRestored.containsKey(i)) return i;
        }
        return currentMaxId + 1;
    }

    /**
     * Removes the game with the specified id from the not restored games map after the specified game war restarted
     *
     * @param gameId the id of the game restored game
     */
    protected void onGameRestored(int gameId) {
        gamesParticipantsToBeRestored.remove(gameId);
    }

    /**
     * Delete the game that the specified client has quit, if present, and puts the client in the global lobby
     *
     * @param connection the connection with the client
     * @param clientName the name of the client
     */
    protected void quitGameOfClient(ClientConnection connection, String clientName) {
        for (Integer gameId: gamesMap.keySet()) {
            if (Arrays.stream(gamesMap.get(gameId).getGameParticipants()).toList().contains(clientName)) {
                gamesMap.get(gameId).onDisconnection(clientName);
            }
        }
        globalLobby(connection, clientName);
    }
}
