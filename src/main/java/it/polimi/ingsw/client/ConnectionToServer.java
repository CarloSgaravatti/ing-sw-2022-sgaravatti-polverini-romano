package it.polimi.ingsw.client;

import it.polimi.ingsw.client.messageHandlers.DefaultMessageHandler;
import it.polimi.ingsw.client.messageHandlers.MessageHandler;
import it.polimi.ingsw.client.modelView.ModelView;
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
    private final ExecutorService messageHandlerExecutor = Executors.newSingleThreadExecutor();
    private String nickname;

    public ConnectionToServer(Socket socket, UserInterface view) {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            firstMessageHandler = new DefaultMessageHandler(this, view, null);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

    public void addFirstMessageHandler(MessageHandler newHandler) {
        newHandler.setNextHandler(firstMessageHandler);
        firstMessageHandler = newHandler;
    }

    @Override
    public void run() {
        try {
            while (true) { //while(isActive()) ?
                MessageFromServer message = (MessageFromServer) inputStream.readObject();
                if (message.getServerMessageHeader().getMessageType() != ServerMessageType.PING_MESSAGE) {
                    messageHandlerExecutor.submit(() -> firstMessageHandler.handleMessage(message));
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
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                //TODO
            }
        }
    }

    public Thread asyncWriteToServer(Object message) {
        Thread t = new Thread(() -> {
            try {
                outputStream.writeObject(message);
            } catch (IOException e) {
                //TODO
            }
        });
        t.start();
        return t;
    }

    public void sendMessage(MessagePayload payload, String messageName, ClientMessageType messageType) {
        ClientMessageHeader header = new ClientMessageHeader(messageName, nickname, messageType);
        MessageFromClient message = new MessageFromClient(header, payload);
        asyncWriteToServer(message);
    }

    public void onPingMessage() {
        ClientMessageHeader header = new ClientMessageHeader(null, null, ClientMessageType.PING_ACK);
        asyncWriteToServer(new MessageFromClient(header, null));
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
