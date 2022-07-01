package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.DuplicateNicknameException;
import it.polimi.ingsw.messages.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

/**
 * SocketClientConnection is a ClientConnection that handles the socket connection with the client by sending and
 * reading messages through the socket.
 *
 * @see it.polimi.ingsw.server.ClientConnection
 */
public class SocketClientConnection implements Runnable, ClientConnection {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Server server;
    private boolean active = false;
    private boolean setupDone = false; //If a player has a game, this is true
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private String nickname;
    private final ScheduledExecutorService pingManager = Executors.newScheduledThreadPool(1);
    private final ExecutorService messageExecutor = Executors.newSingleThreadExecutor();
    private boolean isPingAckReceived = true;

    /**
     * Constructs a SocketClientConnection that will use the specified socket to send and read messages and that will be
     * associated to the specified server instance
     *
     * @param socket the socket used for the connection
     * @param server the instance of the server
     */
    public SocketClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * Adds a listener that will listen to the specified property
     *
     * @param propertyName the name of the property
     * @param listener the listener that will listen the property
     */
    public void addListener(String propertyName, PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Initialize the client and then continue looping to read messages from the socket input stream
     */
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            do {
                initializeClient();
            } while (!isActive());
            enablePing();
            while(isActive()){
                readMessage();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException | IllegalStateException e) {
            System.err.println("Error!" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * Read a message from the socket input stream
     *
     * @throws IOException if there is an error in reading the message
     * @throws ClassNotFoundException if a not recognised message was read
     */
    public void readMessage() throws IOException, ClassNotFoundException {
        MessageFromClient message = (MessageFromClient) in.readObject();
        if (message.getClientMessageHeader().getMessageType() == ClientMessageType.PING_ACK) {
            setPingAckReceived(true);
        } else if (message.getClientMessageHeader().getMessageType() != ClientMessageType.GAME_SETUP) {
            System.out.println("Received from " + nickname + " : " +
                    "message name = " + message.getClientMessageHeader().getMessageName() + " " +
                    "message type = " + message.getClientMessageHeader().getMessageType());
            if (!setupDone) {
                sendError(ErrorMessageType.CLIENT_WITHOUT_GAME, "Before doing this you have to select a game to play");
            } else {
                //TODO: delete try catch when everything is ok
                messageExecutor.submit(() -> {
                    try {
                        listeners.firePropertyChange("RemoteView", null, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } else {
            messageExecutor.submit(() -> handleGameSetup(message));
        }
    }

    /**
     * Close the connection with the client
     */
    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
        server.deregisterConnection(nickname);
        System.out.println("Done!");
        pingManager.shutdownNow();
    }

    /**
     * Sends a message to inform the client that the connection will be closed and then close the connection
     */
    public synchronized void closeConnection() {
        ServerMessageHeader header = new ServerMessageHeader("ConnectionClosed", ServerMessageType.SERVER_MESSAGE);
        MessageFromServer message = new MessageFromServer(header, null);
        asyncSend(message);
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error when closing socket!");
        }
        active = false;
    }

    /**
     * Send the specified message in the socket output stream
     *
     * @param message the message that will be sent
     */
    private synchronized void send(Object message) {
        try {
            out.reset();
            out.writeObject(message);
            out.flush();
        } catch(IOException e) {
            System.err.println(e.getMessage());
            setActive(false);
        }
    }

    /**
     * Asynchronously send the specified message to the socket output stream
     *
     * @param message the message that will be sent
     */
    public synchronized void asyncSend(final Object message){
        new Thread(() -> send(message)).start();
    }

    /**
     * Returns true if the connection is active, otherwise false
     *
     * @return true if the connection is active, otherwise false
     */
    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Initialize the client by requesting from him the nickname
     *
     * @throws IOException if there is an error in the socket
     * @throws ClassNotFoundException
     * @throws ClassCastException if the arrived nickname message is not recognized as a MessageFromClient
     */
    public void initializeClient() throws IOException, ClassNotFoundException, ClassCastException {
        ServerMessageHeader header = new ServerMessageHeader("NicknameRequest", ServerMessageType.SERVER_MESSAGE);
        MessageFromServer message = new MessageFromServer(header, new MessagePayload());
        asyncSend(message);
        MessageFromClient answer = (MessageFromClient) in.readObject();
        String nickname = answer.getMessagePayload().getAttribute("Nickname").getAsString();
        try {
            server.registerConnection(nickname, this);
            setActive(true);
            server.globalLobby(this, nickname);
            this.nickname = nickname;
        } catch (DuplicateNicknameException e) {
            sendError(ErrorMessageType.DUPLICATE_NICKNAME, e.getMessage());
        }
    }

    /**
     * Handles a message from the client that have GAME_SETUP as the message type
     *
     * @param message the game setup message
     */
    public void handleGameSetup(MessageFromClient message) {
        if (message.getClientMessageHeader().getMessageName().equals("QuitGame")) {
            server.quitGameOfClient(this, nickname);
            return;
        } else if (isSetupDone()) {
            sendError(ErrorMessageType.SETUP_ALREADY_DONE, "You have already done your choices");
            return;
        }
        String messageName = message.getClientMessageHeader().getMessageName();
        int gameId;
        switch (messageName) {
            case "RefreshGlobalLobby" -> {
                server.globalLobby(this, nickname);
                return;
            }
            case "NewGame" -> {
                int numPlayers = message.getMessagePayload().getAttribute("NumPlayers").getAsInt();
                boolean isExpertGame = message.getMessagePayload().getAttribute("GameRules").getAsBoolean();
                gameId = server.createGame(numPlayers, isExpertGame);
            }
            case "GameToPlay" -> gameId = message.getMessagePayload().getAttribute("GameId").getAsInt();
            case "RestoreGame" -> {
                try {
                    gameId = server.restoreGameOfClient(nickname);
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    server.globalLobby(this, nickname);
                    return;
                }
            }
            case "DeleteSavedGame" -> {
                server.deleteSavedGame(nickname);
                server.globalLobby(this, nickname);
                return;
            }
            default -> {
                sendError(ErrorMessageType.UNRECOGNIZED_MESSAGE, "Your message was not recognized");
                return;
            }
        }
        server.gameLobby(gameId, this, nickname);
    }

    public synchronized boolean isSetupDone() {
        return setupDone;
    }

    public synchronized void setSetupDone(boolean setupDone) {
        this.setupDone = setupDone;
    }

    public synchronized boolean isPingAckReceived() {
        return isPingAckReceived;
    }

    public synchronized void setPingAckReceived(boolean pingAckReceived) {
        isPingAckReceived = pingAckReceived;
    }

    /**
     * Send the specified error to the client
     *
     * @param error the error type that will be sent to the client
     * @param description the description of the error
     */
    public void sendError(ErrorMessageType error, String description) {
        ServerMessageHeader header = new ServerMessageHeader("Error", ServerMessageType.SERVER_MESSAGE);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("ErrorType", error);
        payload.setAttribute("ErrorInfo", description);
        asyncSend(new MessageFromServer(header, payload));
    }

    /**
     * Enable ping messages to the client and start sending them at a fixed rate of 1 minute
     */
    public void enablePing() {
        MessageFromServer pingMessage = new MessageFromServer(
                new ServerMessageHeader(null, ServerMessageType.PING_MESSAGE), null);
        pingManager.scheduleAtFixedRate(() -> {
            if (!isPingAckReceived()) {
                setActive(false);
                return;
            }
            asyncSend(pingMessage);
            setPingAckReceived(false);
        }, 1, 1, TimeUnit.MINUTES);
    }
}
