package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class CLIStub implements Runnable{
    private final int serverPort;
    private final String serverAddress;
    private String nickname;

    public CLIStub(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String serverAddress;
        int serverPort;
        //Some visualization stuff ...
        System.out.println("Server ip address: ");
        serverAddress = sc.next();
        System.out.println("Server port: ");
        serverPort = sc.nextInt();
        new CLIStub(serverAddress, serverPort).run();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            Scanner sc = new Scanner(System.in);
            new Thread(() -> {
                try {
                    sendMessages(new ObjectOutputStream(socket.getOutputStream()), sc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    readMessages(new ObjectInputStream(socket.getInputStream()), sc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Error in connection with server");
        }
    }

    public void sendMessages(ObjectOutputStream outputStream, Scanner scanner) {
        while(true) {
            System.out.println("Message type");
            ClientMessageType messageType = ClientMessageType.valueOf(scanner.next());
            MessageFromClient message;
            if (messageType != ClientMessageType.PING_ACK) {
                System.out.println("Message name");
                String messageName = scanner.next();
                ClientMessageHeader header = new ClientMessageHeader(messageName, nickname, messageType);
                MessagePayload payload = new MessagePayload();
                boolean valid = true;
                switch (messageName) {
                    case "NicknameMessage" -> getNicknameMessagePayload(scanner, payload);
                    case "NewGame" -> getNumPlayersPayload(scanner, payload);
                    case "GameToPlay" -> getGameToPlayPayload(scanner, payload);
                    case "TowerChoice" -> getTowerChoicePayload(scanner, payload);
                    case "WizardChoice" -> getWizardChoicePayload(scanner, payload);
                    default -> {
                        System.err.println("Not valid name");
                        valid = false;
                    }
                }
                if (valid) {
                    message = new MessageFromClient(header, payload);
                } else {
                    message = null;
                }
            } else {
                ClientMessageHeader header = new ClientMessageHeader(null, nickname, messageType);
                message = new MessageFromClient(header, null);
            }
            if (message != null) {
                try {
                    outputStream.reset();
                    outputStream.writeObject(message);
                    outputStream.flush();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public void getNumPlayersPayload(Scanner scanner, MessagePayload payload) {
        System.out.println("Insert num players");
        int numPlayers = scanner.nextInt();
        payload.setAttribute("NumPlayers", numPlayers);
        System.out.println("Insert rules");
        boolean isExpert = scanner.next().equals("expert");
        payload.setAttribute("GameRules", isExpert);
    }

    public void getGameToPlayPayload(Scanner scanner, MessagePayload payload) {
        System.out.println("Insert game to play");
        int game = scanner.nextInt();
        payload.setAttribute("GameId", game);
    }

    public void getNicknameMessagePayload(Scanner scanner, MessagePayload payload) {
        System.out.println("Insert nickname");
        nickname = scanner.next();
        payload.setAttribute("Nickname", nickname);
    }

    public void getTowerChoicePayload(Scanner scanner, MessagePayload payload) {
        System.out.println("Insert tower type");
        payload.setAttribute("Tower", TowerType.valueOf(scanner.next()));
    }

    public void getWizardChoicePayload(Scanner scanner, MessagePayload payload) {
        System.out.println("Insert wizard type index");
        payload.setAttribute("Wizard", WizardType.values()[scanner.nextInt()]);
    }

    public void readMessages(ObjectInputStream inputStream, Scanner scanner) {
        while(true) {
            try {
                MessageFromServer message = (MessageFromServer) inputStream.readObject();
                String messageName = message.getServerMessageHeader().getMessageName();
                System.out.println("Message name: " + messageName);
                System.out.println("Message type: " + message.getServerMessageHeader().getMessageType());
                if (messageName.equals("Error")) {
                    System.out.println("ErrorType: "  + message.getMessagePayload().getAttribute("ErrorType").getAsObject());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("CLI stub has quit");
                return;
            }
        }
    }
}
