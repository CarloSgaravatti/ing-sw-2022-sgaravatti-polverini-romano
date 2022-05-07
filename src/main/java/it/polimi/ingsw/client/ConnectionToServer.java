package it.polimi.ingsw.client;

import it.polimi.ingsw.client.messageHandlers.DefaultMessageHandler;
import it.polimi.ingsw.client.messageHandlers.MessageHandler;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionToServer implements Runnable {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler firstMessageHandler;
    private String nicknameClient;

    public ConnectionToServer(Socket socket, UserInterface view) {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            firstMessageHandler = new DefaultMessageHandler(this, view, new ModelView());
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
            while (true) { //while(isActive())
                MessageFromServer message = (MessageFromServer) inputStream.readObject();
                if (message.getServerMessageHeader().getMessageType() != ServerMessageType.PING_MESSAGE) {
                    firstMessageHandler.handleMessage(message);
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

    //TODO: pong
    public void onPingMessage() {
        ClientMessageHeader header = new ClientMessageHeader(null, nicknameClient, ClientMessageType.PING_ACK);
        asyncWriteToServer(new MessageFromClient(header, null));
    }
}
