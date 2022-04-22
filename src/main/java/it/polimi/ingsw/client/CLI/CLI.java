package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.ConnectionToServer;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class CLI implements Runnable{
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String serverAddress;
        int serverPort;
        //Some visualization stuff ...
        System.out.println("Insert server ip address: ");
        System.out.print("> ");
        serverAddress = sc.next();
        System.out.println("Insert server port: ");
        System.out.print("> ");
        serverPort = sc.nextInt();
        new CLI(serverAddress, serverPort).run();
    }

    public CLI(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            System.err.println("Error in connection with server");
        }
        System.out.println("Connection Established");
        ConnectionToServer connectionToServer = new ConnectionToServer(socket);
        new Thread(connectionToServer).start();
        //...
    }
}
