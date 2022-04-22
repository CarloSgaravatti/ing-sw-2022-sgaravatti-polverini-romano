package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionToServer implements Runnable {
    private MessageReceiver messageReceiver;
    private ObjectOutputStream outputStream;

    public ConnectionToServer(Socket socket) {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            messageReceiver = new MessageReceiver(socket.getInputStream(), this);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //...
        new Thread(messageReceiver).start();
        //...
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
}
