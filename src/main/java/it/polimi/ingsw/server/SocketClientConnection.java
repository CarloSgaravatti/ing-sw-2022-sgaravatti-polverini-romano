package it.polimi.ingsw.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClientConnection implements Runnable, ClientConnection {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Server server;
    private boolean active = true;

    public SocketClientConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String name;
        try{
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            //Send setup message
            while(isActive()){
                readMessage();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error!" + e.getMessage());
        }finally{
            close(); //Maybe it can be done, or not
        }
    }

    public void readMessage() throws IOException, ClassNotFoundException {

    }

    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
        //server.deregisterConnection(this);
        System.out.println("Done!");
    }

    public synchronized void closeConnection() {
        send("Connection closed!");
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
}
