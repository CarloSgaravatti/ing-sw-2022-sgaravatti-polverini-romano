package it.polimi.ingsw.client;

import it.polimi.ingsw.client.messageHandlers.DefaultMessageHandler;
import it.polimi.ingsw.client.messageHandlers.MessageHandler;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class ConnectionToServer handles the connection between client and server by reading messages from the server and
 * by asynchronously writing messages to the server.
 */
public class ConnectionToServer implements Runnable {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler firstMessageHandler;
    private ExecutorService messageHandlerExecutor = Executors.newSingleThreadExecutor();
    private String nickname;
    private boolean active = true;

    /**
     * Constructs a new instance of ConnectionToServer from the specified Socket and that is bounded to the specified
     * UserInterface
     *
     * @param socket the socket used for the connection with the server
     * @param view the user interface of the client
     */
    public ConnectionToServer(Socket socket, UserInterface view) {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            firstMessageHandler = new DefaultMessageHandler(this, view, null);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
        PlayerSetupHandler playerSetupHandler = new PlayerSetupHandler(this);
        view.addListener(playerSetupHandler, "Nickname");
        view.addListener(playerSetupHandler, "NewGame");
        view.addListener(playerSetupHandler, "GameToPlay");
        view.addListener(playerSetupHandler, "TowerChoice");
        view.addListener(playerSetupHandler, "WizardChoice");
        view.addListener(playerSetupHandler, "RefreshLobby");
        view.addListener(playerSetupHandler, "RestoreGame");
        view.addListener(playerSetupHandler, "DeleteSavedGame");
    }

    /**
     * Adds a new message handler in the first place of the message handler chain
     *
     * @param newHandler the new first message handler
     */
    public void addFirstMessageHandler(MessageHandler newHandler) {
        if (firstMessageHandler!= null) newHandler.setNextHandler(firstMessageHandler);
        firstMessageHandler = newHandler;
    }

    /**
     * Method continue reading a message from the socket input stream until the client is active. The message is passed
     * to the first message handler in the message handler chain
     */
    @Override
    public void run() {
        try {
            while (isActive()) { //while(isActive()) ?
                MessageFromServer message = (MessageFromServer) inputStream.readObject();
                //System.out.println("Received " + message.getServerMessageHeader().getMessageName());
                if (message.getServerMessageHeader().getMessageType() != ServerMessageType.PING_MESSAGE) {
                    //TODO: delete try catch after everything is ok
                    messageHandlerExecutor.submit(() -> {
                        try {
                            firstMessageHandler.handleMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    onPingMessage();
                }
            }
        }  catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (ClassCastException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            messageHandlerExecutor.shutdownNow();
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                //TODO
            }
        }
    }

    /**
     * Asynchronously write the specified message to the socket output stream
     *
     * @param message the message that has to be sent
     * @return the Thread that performs the write action
     */
    public synchronized Thread asyncWriteToServer(Object message) {
        Thread t = new Thread(() -> {
            try {
                outputStream.reset();
                outputStream.writeObject(message);
                outputStream.flush();
            } catch (IOException e) {
                //TODO: is this ok
                asyncWriteToServer(message);
            }
        });
        t.start();
        return t;
    }

    /**
     * Sends a message (by calling the asyncWriteToServer method) that will have the specified message payload and the
     * header will contain the specified message name and message type.
     *
     * @param payload the payload of the message
     * @param messageName the name of the message
     * @param messageType the type of the message
     */
    public void sendMessage(MessagePayload payload, String messageName, ClientMessageType messageType) {
        ClientMessageHeader header = new ClientMessageHeader(messageName, nickname, messageType);
        MessageFromClient message = new MessageFromClient(header, payload);
        //System.out.println("Sending " + messageName);
        asyncWriteToServer(message);
    }

    /**
     * Handles a ping message that have been arrived from the server by immediately sending back a PING_ACK message
     */
    public void onPingMessage() {
        ClientMessageHeader header = new ClientMessageHeader(null, null, ClientMessageType.PING_ACK);
        asyncWriteToServer(new MessageFromClient(header, null));
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Reset the message handler chain, the new one will have the specified message handler as the first message handler
     *
     * @param messageHandler the new first message handler
     */
    public void reset(MessageHandler messageHandler) {
        messageHandlerExecutor.shutdownNow();
        messageHandlerExecutor = Executors.newSingleThreadExecutor();
        firstMessageHandler = messageHandler;
    }
}
