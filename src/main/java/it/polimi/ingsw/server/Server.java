package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.DuplicateNicknameException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.resumeGame.PersistenceGameInfo;
import it.polimi.ingsw.server.resumeGame.SaveGame;
import it.polimi.ingsw.utils.Triplet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Each game is identified by an integer
public class Server implements Runnable{
    private final ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<Integer, GameLobby> gamesMap;
    private final Map<String, ClientConnection> waitingPlayersWithNoGame;
    private final Map<Integer, Map<String, ClientConnection>> waitingPlayersPerGameMap; //Decide if is useful
    private final Map<String, ClientConnection> clientsConnected;
    private int lastGameId = 0;
    private Map<Integer, String[]> gamesParticipantsToBeRestored = new HashMap<>();
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        gamesMap = new ConcurrentHashMap<>();
        waitingPlayersPerGameMap = new ConcurrentHashMap<>();
        waitingPlayersWithNoGame = new ConcurrentHashMap<>();
        clientsConnected = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        try {
            gamesParticipantsToBeRestored = SaveGame.getParticipants();
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
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

    protected synchronized void deleteGame(int gameId) {
        Map<String, ClientConnection> participants = gamesMap.get(gameId).getClients();
        gamesMap.remove(gameId);
        waitingPlayersPerGameMap.remove(gameId);
        for(String clientName: participants.keySet()) {
            //globalLobby(participants.get(clientName), clientName);
            participants.get(clientName).setSetupDone(false);
        }
        deleteFile(gameId);
    }

    protected synchronized void deleteSavedGame(String client) {
        Optional<PersistenceGameInfo> persistenceGameInfo = getPreviousGame(client);
        persistenceGameInfo.ifPresent(game -> {
            if (gamesMap.containsKey(game.getGameId())) {
                deleteGame(game.getGameId());
            } else {
                deleteFile(game.getGameId());
                gamesParticipantsToBeRestored.remove(game.getGameId());
            }
        });
    }

    private void deleteFile(int gameId) {
        SaveGame.deletePersistenceData(gameId);
        saveParticipants();
    }

    public GameLobby getGameById(int gameId) {
        return gamesMap.get(gameId);
    }

    public synchronized void registerConnection(String clientName, ClientConnection client) throws DuplicateNicknameException {
        if (clientsConnected.containsKey(clientName)) {
            throw new DuplicateNicknameException();
        }
        clientsConnected.put(clientName, client);
        System.out.println("Registered " + clientName);
    }

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
            payload.setAttribute("NotStartedGames", waitingPlayersPerGameMap.size());
            Map<Integer, Triplet<Integer, Boolean, String[]>> gamesToSendMap = new HashMap<>();
            for (Integer gameId: waitingPlayersPerGameMap.keySet().stream()
                    .filter(gameId -> !gamesMap.get(gameId).isGameRestored()).toList()) {
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

    private Optional<PersistenceGameInfo> getPreviousGame(String clientName) {
        for (Integer gameId: gamesParticipantsToBeRestored.keySet()) {
            if (Arrays.stream(gamesParticipantsToBeRestored.get(gameId)).toList().contains(clientName)) {
                try {
                    return Optional.ofNullable(SaveGame.getPersistenceData(gameId));
                } catch (FileNotFoundException e) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

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

    private void handleLobbyError(ErrorMessageType error,  ClientConnection client, String clientName) {
        client.sendError(error, "The game lobby requested was not found");
        globalLobby(client, clientName);
    }

    public synchronized int createGame(int numPlayers, boolean isExpertGame) {
        //creates a new game of numPlayers number of players, assigns it a unique identifier, adds it to the games map
        //and to the waiting players per game map (it also creates the corresponding empty hash map) but not to the
        //games participants map
        //int id = assignNewGameId().orElse(1); //if optional is empty it means that there are no games in the server
        lastGameId++;
        System.out.println("created a new game with id = " + lastGameId);
        gamesMap.put(lastGameId, new GameLobby(lastGameId, numPlayers, isExpertGame, this, false));
        waitingPlayersPerGameMap.put(lastGameId, new HashMap<>());
        return lastGameId;
    }

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

    public void saveParticipants() {
        new Thread(() -> {
            Map<Integer, String[]> gamesParticipants = new HashMap<>();
            for (Integer gameId : gamesParticipantsToBeRestored.keySet()) {
                gamesParticipants.put(gameId, gamesParticipantsToBeRestored.get(gameId));
            }
            for (Integer gameId: gamesMap.keySet()) {
                gamesParticipants.put(gameId, gamesMap.get(gameId).getGameParticipants());
            }
            gamesParticipants.keySet().forEach(System.out::println);
            try {
                SaveGame.saveGameParticipants(gamesParticipants);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    //This implements a sort of auto-incremental key for gameId (like in a db)
    private Optional<Integer> assignNewGameId() {
        return gamesMap.keySet().stream().max(Comparator.comparingInt(id -> id));
    }
}
