package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.DuplicateNicknameException;
import it.polimi.ingsw.messages.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.*;

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

    public SocketClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void addListener(String propertyName, PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            do {
                initializeClient();
            } while (!isActive());
            enablePing();
            while(isActive()){
                readMessage();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Error!" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void readMessage() throws IOException, ClassNotFoundException {
        MessageFromClient message = (MessageFromClient) in.readObject();
        if (message.getClientMessageHeader().getMessageType() == ClientMessageType.PING_ACK) {
            setPingAckReceived(true);
            System.out.println("PingAck received by " + nickname);
        } else if (message.getClientMessageHeader().getMessageType() != ClientMessageType.GAME_SETUP) {
            System.out.println("Received from " + nickname + " : " +
                    "message name = " + message.getClientMessageHeader().getMessageName() + " " +
                    "message type = " + message.getClientMessageHeader().getMessageType());
            if (!setupDone) {
                sendError(ErrorMessageType.CLIENT_WITHOUT_GAME);
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

    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
        server.deregisterConnection(nickname);
        System.out.println("Done!");
        pingManager.shutdownNow();
    }

    public synchronized void closeConnection() {
        ServerMessageHeader header = new ServerMessageHeader("ConnectionClosed", ServerMessageType.SERVER_MESSAGE);
        MessageFromServer message = new MessageFromServer(header, null);
        send(message);
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error when closing socket!");
        }
        active = false;
    }

    private synchronized void send(Object message) {
        try {
            out.reset();
            out.writeObject(message);
            out.flush();
        } catch(IOException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    //Sending a message is a slow operation, it needs to be asynchronous from the caller of the method
    public synchronized void asyncSend(final Object message){
        new Thread(() -> send(message)).start();
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    //TODO: there is a problem with pings when a client sends nickname after ping a ping ack
    public void initializeClient() throws IOException, ClassNotFoundException, ClassCastException {
        ServerMessageHeader header = new ServerMessageHeader("NicknameRequest", ServerMessageType.SERVER_MESSAGE);
        MessageFromServer message = new MessageFromServer(header, new MessagePayload());
        send(message);
        MessageFromClient answer = (MessageFromClient) in.readObject();
        String nickname = answer.getMessagePayload().getAttribute("Nickname").getAsString();
        try {
            server.registerConnection(nickname, this);
            setActive(true);
            server.globalLobby(this, nickname);
            this.nickname = nickname;
        } catch (DuplicateNicknameException e) {
            sendError(ErrorMessageType.DUPLICATE_NICKNAME);
        }
    }

    public void handleGameSetup(MessageFromClient message) {
        if (isSetupDone()) {
            sendError(ErrorMessageType.SETUP_ALREADY_DONE);
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
            default -> {
                sendError(ErrorMessageType.UNRECOGNIZED_MESSAGE);
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

    public void sendError(ErrorMessageType error) {
        ServerMessageHeader header = new ServerMessageHeader("Error", ServerMessageType.SERVER_MESSAGE);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("ErrorType", error);
        asyncSend(new MessageFromServer(header, payload));
    }

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
            System.out.println("Ping sent to " + nickname);
        }, 1, 1, TimeUnit.MINUTES);
    }
}
