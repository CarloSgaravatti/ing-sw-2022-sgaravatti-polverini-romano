package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class CLI implements Runnable, UserInterface {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private Scanner sc = new Scanner(System.in);
    private String nickname;

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
        ConnectionToServer connectionToServer = new ConnectionToServer(socket, this);
        new Thread(connectionToServer).start();
        //...
    }

    @Override
    public String askNickname() {
        String nickname;
        nickname = sc.next();
        while (nickname == null || nickname.isBlank()){
            nickname = sc.next();
        }
        this.nickname = nickname; //TODO: need to change
        return nickname;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void displayGameLobby(int numGames, Map<Integer, Pair<Integer, List<String>>> gamesInfo) {
        System.out.println("There are currently " + numGames + " games not started. These are those games: ");
        for (Integer i: gamesInfo.keySet()) {
            Pair<Integer, List<String>> gameInfo = gamesInfo.get(i);
            List<String> players = gameInfo.getSecond();
            System.out.println("- game id = " + i + " numPlayers = " + gameInfo.getFirst() + " players = " + players);
        }
    }

    @Override
    //This implementation is very bad I know (is just for test the server)
    public Pair<String, Integer> askGameToPlay() {
        System.out.println("Select what to do");
        return new Pair<>(sc.next(), sc.nextInt());
        //TODO: control input
    }

    @Override
    public void displayStringMessage(String message) {
        System.out.println(message);
    }

    public TowerType askTowerChoice(List<TowerType> freeTowers){
        return null;
    }

    public WizardType askWizardChoice(List<WizardType> freeWizards){
        return null;
    }

    public void displayLobbyInfo(){}

    public void askAction(){}
}
