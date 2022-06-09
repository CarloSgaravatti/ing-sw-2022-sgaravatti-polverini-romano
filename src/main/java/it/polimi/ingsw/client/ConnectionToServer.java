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

public class ConnectionToServer implements Runnable {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler firstMessageHandler;
    private ExecutorService messageHandlerExecutor = Executors.newSingleThreadExecutor();
    private String nickname;
    private boolean active = true;

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
    }

    public void addFirstMessageHandler(MessageHandler newHandler) {
        if (firstMessageHandler!= null) newHandler.setNextHandler(firstMessageHandler);
        firstMessageHandler = newHandler;
    }

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
        }  catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
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

    public void sendMessage(MessagePayload payload, String messageName, ClientMessageType messageType) {
        ClientMessageHeader header = new ClientMessageHeader(messageName, nickname, messageType);
        MessageFromClient message = new MessageFromClient(header, payload);
        //System.out.println("Sending " + messageName);
        asyncWriteToServer(message);
    }

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

    public void reset() {
        messageHandlerExecutor.shutdownNow();
        messageHandlerExecutor = Executors.newSingleThreadExecutor();
    }

    public void reset(MessageHandler messageHandler) {
        reset();
        firstMessageHandler = messageHandler;
    }
}
