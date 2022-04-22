package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Each game is identified by an integer
public class Server implements Runnable{
    private final ServerSocket serverSocket;
    private final ExecutorService executor = Executors.newFixedThreadPool(128);
    private final Map<Integer, List<ClientConnection>> gamesParticipantMap;
    private final Map<Integer, GameController> gamesMap;
    private final Map<String, ClientConnection> waitingPlayersWithNoGame;
    //when a game is started, the key is deleted from here
    private final Map<Integer, Map<String, ClientConnection>> waitingPlayersPerGameMap;
    private final List<String> nicknamesAlreadyTaken;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        gamesParticipantMap = new HashMap<>();
        gamesMap = new HashMap<>();
        waitingPlayersPerGameMap = new HashMap<>();
        waitingPlayersWithNoGame = new HashMap<>();
        nicknamesAlreadyTaken = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(new SocketClientConnection(socket));
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

    public GameController getGameById(int gameId) {
        return gamesMap.get(gameId);
    }

    public void globalLobby(ClientConnection client, String clientName) {
        waitingPlayersWithNoGame.put(clientName, client);
        //If there aren't games that are not started, server requests to insert the number of players
        //for a game and creates a new game.
        if (waitingPlayersPerGameMap.isEmpty()) {
            //...
            return;
        }

        //If there are games that aren't started, server requests to client what game he wants to play or
        //if he wants to create a new game (and set the number of players)

    }

    public void gameLobby(int gameId, ClientConnection client, String clientName) {
        //This is a particular game lobby, the game is always already created
        waitingPlayersPerGameMap.get(gameId).put(clientName, client);
        //Delete from waitingPlayersWithNoGame
        //other stuff (see trisMVC)
    }

    public void createGame(int numPlayers) {
        //creates a new game of numPlayers number of players, assigns it a unique identifier, adds it to the games map
        //and to the waiting players per game map (it also creates the corresponding empty hash map) but not to the
        //games participants map
        int id = assignNewGameId().orElse(1); //if option is empty it means that there are no games
    }

    //This implements a sort of auto-incremental key for gameId (like a db)
    private Optional<Integer> assignNewGameId() {
        return gamesMap.keySet().stream().max(Comparator.comparingInt(id -> id));
    }
}
