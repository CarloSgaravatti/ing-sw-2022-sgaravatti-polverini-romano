package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.DuplicateNicknameException;
import it.polimi.ingsw.messages.*;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClientConnection implements Runnable, ClientConnection {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Server server;
    private boolean active = false;
    private boolean setupDone = false; //If a player has a game, this is true
    private final EventListenerList listeners = new EventListenerList();
    private String nickname;

    public SocketClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void addListener(RemoteView listener) {
        listeners.add(RemoteView.class, listener);
    }

    protected void fireMessageEvent(MessageFromClient message) {
        for (RemoteView eventListener: listeners.getListeners(RemoteView.class)) {
            eventListener.eventPerformed(message);
        }
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            //Send setup nickname message
            do {
                initializeClient();
            } while (!isActive());
            while(isActive()){
                readMessage();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Error!" + e.getMessage());
        } finally{
            close();
        }
    }

    public synchronized void readMessage() throws IOException, ClassNotFoundException {
        MessageFromClient message = (MessageFromClient) in.readObject();
        if (message.getClientMessageHeader().getMessageType() != ClientMessageType.GAME_SETUP) {
            if (!setupDone) {
                //TODO: send error (player don't have a game)
            }else {
                fireMessageEvent(message);
            }
            return;
        }
        handleGameSetup(message);
    }

    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
        server.deregisterConnection(this);
        System.out.println("Done!");
    }

    public synchronized void closeConnection() {
        ServerMessageHeader header = new ServerMessageHeader("ConnectionClosed", ServerMessageType.SERVER_ANSWER);
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
        }
    }

    //Sending a message is a slow operation, it needs to be asynchronous from the caller of the method
    //This method doesn't work
    public void asyncSend(final Object message){
        new Thread(() -> send(message)).start();
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    public void initializeClient() throws IOException, ClassNotFoundException, ClassCastException {
        ServerMessageHeader header = new ServerMessageHeader("NicknameRequest", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        String messageInfo = "Welcome to Eriantys!\nInsert a username";
        payload.setAttribute("MessageInfo", messageInfo);
        MessageFromServer message = new MessageFromServer(header, payload);
        send(message);
        MessageFromClient answer = (MessageFromClient) in.readObject();
        String nickname = answer.getMessagePayload().getAttribute("Nickname").getAsString();
        try {
            server.registerConnection(nickname, this);
            setActive(true);
            server.globalLobby(this, nickname);
            this.nickname = nickname;
        } catch (DuplicateNicknameException e) {
            header = new ServerMessageHeader("Error", ServerMessageType.SERVER_ANSWER);
            payload = new MessagePayload();
            payload.setAttribute("ErrorType", ErrorMessageType.DUPLICATE_NICKNAME);
            message = new MessageFromServer(header, payload);
            send(message);
        }
    }

    public void handleGameSetup(MessageFromClient message) {
        if (isSetupDone()) {
            //TODO: send error (player already in a game)
            return;
        }
        String messageName = message.getClientMessageHeader().getMessageName();
        int gameId;
        switch (messageName) {
            case "NumPlayers" -> {
                int numPlayers = message.getMessagePayload().getAttribute("NumPlayers").getAsInt();
                gameId = server.createGame(numPlayers);
            }
            case "GameToPlay" -> gameId = message.getMessagePayload().getAttribute("GameId").getAsInt();
            default -> {
                ServerMessageHeader header = new ServerMessageHeader("Error", ServerMessageType.SERVER_ANSWER);
                MessagePayload payload = new MessagePayload();
                payload.setAttribute("ErrorType", ErrorMessageType.UNRECOGNIZE_MESSAGE);
                send(new MessageFromServer(header, payload));
                return;
            }
        }
        server.gameLobby(gameId, this, nickname);
        setSetupDone(true);
    }

    public synchronized boolean isSetupDone() {
        return setupDone;
    }

    public synchronized void setSetupDone(boolean setupDone) {
        this.setupDone = setupDone;
    }
}
