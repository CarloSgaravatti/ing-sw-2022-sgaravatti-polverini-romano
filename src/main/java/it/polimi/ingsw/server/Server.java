package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.DuplicateNicknameException;
import it.polimi.ingsw.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.utils.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Each game is identified by an integer
public class Server implements Runnable{
    private final ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newFixedThreadPool(128);
    private final Map<Integer, GameLobby> gamesMap;
    private final Map<String, ClientConnection> waitingPlayersWithNoGame;
    //when a game is started, the key is deleted from here
    private final Map<Integer, Map<String, ClientConnection>> waitingPlayersPerGameMap; //Decide if is useful
    private final Map<String, ClientConnection> clientsConnected;
    //this list of games id is for games that are finished and are about to be canceled
    private final List<Integer> gamesFinished;

    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        gamesMap = new ConcurrentHashMap<>();
        waitingPlayersPerGameMap = new ConcurrentHashMap<>();
        waitingPlayersWithNoGame = new ConcurrentHashMap<>();
        clientsConnected = new ConcurrentHashMap<>();
        gamesFinished = new ArrayList<>();
        //new Thread(this::gameFinishedHandler).start();
    }

    @Override
    public void run() {
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
        try {
            Server server = new Server(12345);
            System.out.println("Server started");
            server.run();
        } catch (IOException e) {
            System.err.println("Couldn't start the server");
        }
    }

    //Schema produttore/consumatore (magari lo si può fare in una classe a parte)
    //TODO: produttore
    /*protected void gameFinishedHandler() {
        int gameId;
        while (true) {
            synchronized (gamesFinished) {
                while (gamesFinished.isEmpty()) {
                    try {
                        gamesFinished.wait(); //Decide what can be done with Interrupted exception
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                gameId = gamesFinished.remove(0);
            }
            String winner = gamesMap.get(gameId).getGameController().declareWinner();
            ServerMessageHeader header = new ServerMessageHeader("GameFinished", ServerMessageType.GAME_UPDATE);
            MessagePayload payload = new MessagePayload();
            payload.setAttribute("Winner", winner);
            MessageFromServer message = new MessageFromServer(header, payload);
            for (ClientConnection c: gamesParticipantMap.get(gameId).values()) {
                c.asyncSend(message);
                c.setSetupDone(false);
            }
            gamesMap.remove(gameId);
            gamesParticipantMap.remove(gameId);
        }
    }*/

    public GameLobby getGameById(int gameId) {
        return gamesMap.get(gameId);
    }

    public synchronized void registerConnection(String clientName, ClientConnection client) throws DuplicateNicknameException {
        if (clientsConnected.containsKey(clientName)) {
            throw new DuplicateNicknameException();
        }
        clientsConnected.put(clientName, client);
    }

    public void deregisterConnection(String clientName) {
        clientsConnected.remove(clientName);
        waitingPlayersWithNoGame.remove(clientName);
        //TODO: what happens at game lobby?
    }

    public void globalLobby(ClientConnection client, String clientName) {
        waitingPlayersWithNoGame.putIfAbsent(clientName, client);
        //If there aren't games that are not started, server requests to insert the number of players
        //for a game and creates a new game.
        //If there are games that aren't started, server requests to client what game he wants to play or
        //if he wants to create a new game (and set the number of players)
        ServerMessageHeader header = new ServerMessageHeader("GeneralLobby", ServerMessageType.SERVER_MESSAGE);
        MessagePayload payload = new MessagePayload();
        if (waitingPlayersPerGameMap.isEmpty()) {
            payload.setAttribute("NotStartedGames", 0);
            payload.setAttribute("GamesInfo", new HashMap<>());
        } else {
            payload.setAttribute("NotStartedGames", waitingPlayersPerGameMap.size());
            Map<Integer, Pair<Integer, List<String>>> gamesToSendMap = new HashMap<>();
            for (Integer gameId: waitingPlayersPerGameMap.keySet()) {
                Pair<Integer, List<String>> gameInfo = new Pair<>();
                gameInfo.setFirst(gamesMap.get(gameId).getNumPlayers());
                gameInfo.setSecond(new ArrayList<>(waitingPlayersPerGameMap.get(gameId).keySet()));
                gamesToSendMap.put(gameId, gameInfo);
            }
            payload.setAttribute("GamesInfo", gamesToSendMap);
        }
        client.asyncSend(new MessageFromServer(header, payload));
    }

    public void gameLobby(int gameId, ClientConnection client, String clientName) {
        if (!gamesMap.containsKey(gameId)) {
            handleLobbyError(ErrorMessageType.INVALID_REQUEST_GAME_NOT_FOUND, client, clientName);
        } else if(gamesMap.get(gameId).isStarted()) {
            handleLobbyError(ErrorMessageType.INVALID_REQUEST_GAME_ALREADY_STARTED, client, clientName);
        } else {
            //Try catch will be deleted after I find the error
            try {
                gamesMap.get(gameId).insertInLobby(clientName, client);
                client.setSetupDone(true);
            } catch (GameAlreadyStartedException e) {
                //game already started (when server sent global lobby message the game wasn't already started,
                //but since them someone as already entered the game and so the game has started)
                handleLobbyError(ErrorMessageType.INVALID_REQUEST_GAME_ALREADY_STARTED, client, clientName);
                return;
            } catch (NullPointerException e) {
                //get(gameId) returns null if there isn't a game with that id (it is not created), so insertInLobby
                //is called on a null object
                e.printStackTrace();
                handleLobbyError(ErrorMessageType.INVALID_REQUEST_GAME_NOT_FOUND, client, clientName);
                return;
            }
        }
        waitingPlayersWithNoGame.remove(clientName);
    }

    private void handleLobbyError(ErrorMessageType error,  ClientConnection client, String clientName) {
        client.sendError(error);
        globalLobby(client, clientName); //TODO: don'y know if this is necessary
    }

    public synchronized int createGame(int numPlayers, boolean isExpertGame) {
        //creates a new game of numPlayers number of players, assigns it a unique identifier, adds it to the games map
        //and to the waiting players per game map (it also creates the corresponding empty hash map) but not to the
        //games participants map
        int id = assignNewGameId().orElse(1); //if optional is empty it means that there are no games in the server
        gamesMap.put(id, new GameLobby(id, numPlayers, isExpertGame));
        waitingPlayersPerGameMap.put(id, new HashMap<>());
        return id;
    }

    //This implements a sort of auto-incremental key for gameId (like in a db)
    private Optional<Integer> assignNewGameId() {
        return gamesMap.keySet().stream().max(Comparator.comparingInt(id -> id));
    }
}
