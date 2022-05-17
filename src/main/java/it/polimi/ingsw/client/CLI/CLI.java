package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.utils.Colors;
import it.polimi.ingsw.client.CLI.utils.MapPrinter;
import it.polimi.ingsw.client.CLI.utils.PrintEntryWindow;
import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.PlayerSetupHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeListener;
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
    private final InputManager inputManager = new InputManager(sc);
    private ModelView modelView;

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
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public void addListener(PropertyChangeListener listener, String propertyName) {
        listeners.addPropertyChangeListener(propertyName, listener);
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
        ConnectionToServer connectionToServer = new ConnectionToServer(socket, this);
        PlayerSetupHandler playerSetupHandler = new PlayerSetupHandler(connectionToServer);
        addListener(playerSetupHandler, "Nickname");
        addListener(playerSetupHandler, "NewGame");
        addListener(playerSetupHandler, "GameToPlay");
        addListener(playerSetupHandler, "TowerChoice");
        addListener(playerSetupHandler, "WizardChoice");
        //TODO: maybe use an executor service
        Thread t = new Thread(connectionToServer);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            //TODO
            t.interrupt(); //is this ok?
        }
    }

    @Override
    public void askNickname() {
        String nickname;
        nickname = sc.next();
        while (nickname == null || nickname.isBlank()){
            nickname = sc.next();
        }
        this.nickname = nickname; //TODO: need to change
        listeners.firePropertyChange("Nickname", null, nickname);
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void displayGlobalLobby(int numGames, Map<Integer, Pair<Integer,String[]>> gamesInfo) {
        System.out.println("There are currently " + numGames + " games not started.");
        for (Integer i: gamesInfo.keySet()) {
            Pair<Integer, String[]> gameInfo = gamesInfo.get(i);
            String[] players = gameInfo.getSecond();
            System.out.println("- game id = " + i + " numPlayers = " + gameInfo.getFirst() + " players = " + Arrays.toString(players));
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
    public void askLobbyDecision() {
        String decision;
        do {
            System.out.println("Select what to do: [New/Game]");
            decision = sc.next();
        } while (!decision.equals("New") && !decision.equals("Game"));
        if (decision.equals("New")) {
            int numPlayers = 0;
            do {
                System.out.println("Insert number of players: [2/3]");
                try {
                    numPlayers = Integer.parseInt(sc.next());
                }
                catch (NumberFormatException e) {
                    System.out.println(Colors.RED + "You have to insert a number!" + Colors.RESET);
                }
            } while (numPlayers != 2 && numPlayers != 3);
            boolean rules;
            System.out.println("Insert rules types: to create an expert game type 'expert', otherwise a simple game will be created");
            rules = sc.next().equals("expert");
            listeners.firePropertyChange("NewGame", numPlayers, rules);
        } else {
            int gameId = 0;
            boolean inputOk = false;
            System.out.println("Insert game id");
            do {
                try {
                    gameId = Integer.parseInt(sc.next());
                    inputOk = true;
                }
                catch (NumberFormatException e) {
                    System.out.println(Colors.RED + "You have to insert a number!" + Colors.RESET);
                }
            } while(!inputOk);
            listeners.firePropertyChange("GameToPlay", null, gameId);
        }
    }

    @Override
    public void displayStringMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void askTowerChoice(TowerType[] freeTowers){
        System.out.println("You have to choose a tower, these are the options: " + Arrays.toString(freeTowers));
        boolean towerChosen = false;
        TowerType choice = null;
        while (!towerChosen) {
            try {
                choice = TowerType.valueOf(sc.next());
                towerChosen = true;
            } catch (IllegalArgumentException e) {
                System.out.println(Colors.RED + "Not a valid input, retry" + Colors.RESET);
            }
        }
        listeners.firePropertyChange("TowerChoice", null, choice);
    }

    @Override
    public void askWizardChoice(WizardType[] freeWizards){
        System.out.println("You have to choose a tower, these are the options: " + Arrays.toString(freeWizards));
        boolean wizardChosen = false;
        WizardType choice = null;
        while (!wizardChosen) {
            try {
                choice = WizardType.valueOf(sc.next());
                wizardChosen = true;
            } catch (IllegalArgumentException e) {
                System.out.println(Colors.RED + "Not a valid input, retry" + Colors.RESET);
            }
        }
        listeners.firePropertyChange("WizardChoice", null, choice);
    }

    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {
        System.out.print("You entered the lobby, there are currently " + waitingPlayers.length + " players waiting.\n"
                + "Their names are:");
        Arrays.stream(waitingPlayers).forEach(p -> System.out.print(" " + p));
        System.out.println("\nRemember: this game requires " + numPlayers + " players and the rules are "
                + ((rules) ? "expert" : "simple"));
    }

    public void askAction(List<String> actions) {
        actions.forEach(System.out::println);
        String actionName = sc.next();
        String actionArgument = sc.nextLine();
        while(!actions.contains(actionName)) { //can be modified (for example by adding abbreviations)
            System.out.println(Colors.RED + "Action not recognized, retry" + Colors.RESET);
            actionName = sc.next();
            actionArgument = sc.nextLine();
        }
        listeners.firePropertyChange(actionName, null, actionArgument);
    }

    @Override
    public void onGameInitialization(ModelView modelView) {
        MapPrinter printer = new MapPrinter(0, 0);
        this.modelView = modelView;
        printer.initializeMap(modelView);
        printer.printMap();
    }
}
