package it.polimi.ingsw.client;

import java.io.*;

@Deprecated
public class MessageReceiver implements Runnable{
    private ObjectInputStream inputStream;
    private MessageHandler messageHandler;

    public MessageReceiver(InputStream inputStream, ConnectionToServer connection, UserInterface view) throws IOException {
        this.inputStream = new ObjectInputStream(inputStream);
        this.messageHandler = new MessageHandler(connection, view); //Maybe we will need to change the MessageHandler constructor
    }

    @Override
    public void run() {
        /*try {
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
        }*/
    }
}
