package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.utils.PrintEntryWindow;
import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

//TODO: fire property change listeners might be useful (instead of using return types, because it isn't always the same
//  type that we want to return in some methods, i.e. askGameToPlay)

public class CLI implements Runnable, UserInterface {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private final Scanner sc = new Scanner(System.in);
    private String nickname;
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

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


    public void clearScreen(){
        try{
            String operatingSystem = System.getProperty("os.name");
            if(operatingSystem.contains("Windows")){
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            }
            else{
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            System.err.println("Error in connection with server");
        }
        System.out.println("Connection Established");
        clearScreen();
        PrintEntryWindow.printWelcome();
        //askNickname();
        //System.out.println(nickname);
        //clearScreen();
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
    public void displayGlobalLobby(int numGames, Map<Integer, Pair<Integer, List<String>>> gamesInfo) {
        System.out.println("There are currently " + numGames + " games not started.");
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

    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {
        System.out.print("You entered the lobby, there are currently " + waitingPlayers.length + "players waiting.\n"
                + "Their names are:");
        Arrays.stream(waitingPlayers).forEach(p -> System.out.print(" " + p));
        System.out.println("\nRemember: this game requires " + numPlayers + " players and the rules are "
                + ((rules) ? "simple" : "expert"));
    }

    public void askAction(List<String> actions) {
        actions.forEach(System.out::println);
        insertAction();
    }

    public void askAssistant() {

    }

    public void insertAction() {
        String action = sc.nextLine();
    }
}
