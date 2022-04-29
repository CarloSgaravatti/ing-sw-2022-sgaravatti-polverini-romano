package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.MessageFromServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionToServer implements Runnable {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler messageHandler;

    public ConnectionToServer(Socket socket, UserInterface view) {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            messageHandler = new MessageHandler(this, view);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) { //while(isActive())
                MessageFromServer message = (MessageFromServer) inputStream.readObject();
                messageHandler.handleMessage(message);
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
            }
        }
    }

    public Thread asyncWriteToServer(Object message) {
        //Debug
        System.out.println("Writing message");
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
}
