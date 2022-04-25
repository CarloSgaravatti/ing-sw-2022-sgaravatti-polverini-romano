package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.DuplicateNicknameException;
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
    private final Map<Integer, Map<String, ClientConnection>> gamesParticipantMap;
    private final Map<Integer, GameController> gamesMap;
    private final Map<String, ClientConnection> waitingPlayersWithNoGame;
    //when a game is started, the key is deleted from here
    private final Map<Integer, Map<String, ClientConnection>> waitingPlayersPerGameMap;
    private final Map<String, ClientConnection> clientsConnected;
    //this list of games id is for games that are finished and are about to be canceled
    private final List<Integer> gamesFinished;

    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        gamesParticipantMap = new ConcurrentHashMap<>();
        gamesMap = new ConcurrentHashMap<>();
        waitingPlayersPerGameMap = new ConcurrentHashMap<>();
        waitingPlayersWithNoGame = new ConcurrentHashMap<>();
        clientsConnected = new ConcurrentHashMap<>();
        gamesFinished = new ArrayList<>();
        new Thread(this::gameFinishedHandler).start();
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
    protected void gameFinishedHandler() {
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
            String winner = gamesMap.get(gameId).declareWinner();
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
    }

    public GameController getGameById(int gameId) {
        return gamesMap.get(gameId);
    }

    public synchronized void registerConnection(String clientName, ClientConnection client) throws DuplicateNicknameException {
        if (clientsConnected.containsKey(clientName)) {
            throw new DuplicateNicknameException();
        }
        clientsConnected.put(clientName, client);
    }

    public void deregisterConnection(ClientConnection clientConnection) {
        //TODO
    }

    public void globalLobby(ClientConnection client, String clientName) {
        waitingPlayersWithNoGame.put(clientName, client);
        //If there aren't games that are not started, server requests to insert the number of players
        //for a game and creates a new game.
        //If there are games that aren't started, server requests to client what game he wants to play or
        //if he wants to create a new game (and set the number of players)
        ServerMessageHeader header = new ServerMessageHeader("GeneralLobby", ServerMessageType.SERVER_ANSWER);
        MessagePayload payload = new MessagePayload();
        if (waitingPlayersPerGameMap.isEmpty()) {
            payload.setAttribute("NotStartedGames", 0);
        } else {
            payload.setAttribute("NotStartedGames", waitingPlayersPerGameMap.size());
            Map<Integer, Pair<Integer, List<String>>> gamesToSendMap = new HashMap<>();
            for (Integer gameId: waitingPlayersPerGameMap.keySet()) {
                Pair<Integer, List<String>> gameInfo = new Pair<>();
                gameInfo.setFirst(gamesMap.get(gameId).getInitController().getNumPlayers());
                gameInfo.setSecond(new ArrayList<>(waitingPlayersPerGameMap.get(gameId).keySet()));
                gamesToSendMap.put(gameId, gameInfo);
            }
            payload.setAttribute("GamesInfo", gamesToSendMap);
        }
        client.asyncSend(new MessageFromServer(header, payload));
    }

    public void gameLobby(int gameId, ClientConnection client, String clientName) {
        //This is a particular game lobby, the game is always already created at this point
        MessagePayload payload = new MessagePayload();
        ServerMessageHeader header;
        try {
            waitingPlayersPerGameMap.get(gameId).put(clientName, client);
        } catch (NullPointerException e) {
            //game already started (when server sent global lobby message the game wasn't already started,
            //but since them someone as already entered the game and so the game has started)
            header = new ServerMessageHeader("Error", ServerMessageType.SERVER_ANSWER);
            payload.setAttribute("ErrorType", ErrorMessageType.INVALID_REQUEST_GAME_ALREADY_STARTED);
            client.asyncSend(new MessageFromServer(header, payload));
            return;
        }
        waitingPlayersWithNoGame.remove(clientName);
        int gameNumPlayers = gamesMap.get(gameId).getInitController().getNumPlayers();
        if (waitingPlayersPerGameMap.get(gameId).size() == gameNumPlayers) {
            //Start game (see TrisMVC), do setup things
            //TODO
            gamesParticipantMap.put(gameId, waitingPlayersPerGameMap.get(gameId));
            waitingPlayersPerGameMap.remove(gameId);
        } else {
            header = new ServerMessageHeader("GameLobby", ServerMessageType.SERVER_ANSWER);
            payload.setAttribute("GameNumPlayers", gameNumPlayers);
            payload.setAttribute("WaitingPlayers", waitingPlayersPerGameMap.get(gameId).size());
            client.asyncSend(new MessageFromServer(header, payload));
        }
    }

    public synchronized int createGame(int numPlayers) {
        //creates a new game of numPlayers number of players, assigns it a unique identifier, adds it to the games map
        //and to the waiting players per game map (it also creates the corresponding empty hash map) but not to the
        //games participants map
        int id = assignNewGameId().orElse(1); //if optional is empty it means that there are no games in the server
        gamesMap.put(id, new GameController(id));
        gamesMap.get(id).getInitController().setNumPlayers(numPlayers);
        waitingPlayersPerGameMap.put(id, new HashMap<>());
        return id;
    }

    //This implements a sort of auto-incremental key for gameId (like in a db)
    private Optional<Integer> assignNewGameId() {
        return gamesMap.keySet().stream().max(Comparator.comparingInt(id -> id));
    }
}
