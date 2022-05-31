package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.utils.*;
import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.PlayerSetupHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Triplet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class CLI implements Runnable, UserInterface {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private final Scanner sc = new Scanner(System.in);
    private String nickname;
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private ModelView modelView;
    private MapPrinter printer;
    private UserHelper helper;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String serverAddress;
        int serverPort;
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
        PrintStaticMessage.printWelcome();
        ConnectionToServer connectionToServer = new ConnectionToServer(socket, this);
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
        System.out.println("Insert a username:");
        String nickname;
        do {
            System.out.print("> ");
            nickname = sc.next();
        } while (nickname == null || nickname.isBlank());
        this.nickname = nickname;
        listeners.firePropertyChange("Nickname", null, nickname);
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo) {
        clearScreen();
        System.out.println("There are currently " + numGames + " games not started.");
        LobbyPrintManager printManager = new LobbyPrintManager(gamesInfo);
        printManager.printLobby();
        String message = "Insert 'NewGame' to create a new game, 'Refresh' to update the global lobby";
        if (numGames > 5) {
            message += ", '>5' to view the next 5 games and '<5' to view the previous 5 games.";
        } else message += ".";
        message += " To enter a game lobby, insert the id of the game";
        boolean decisionMade = false;
        while (!decisionMade) {
            System.out.println(message);
            System.out.print("> ");
            String input = sc.next();
            switch (input) {
                case ">5" -> {
                    printManager.onNextFiveCommand();
                    clearScreen();
                    printManager.printLobby();
                }
                case "<5" -> {
                    printManager.onPreviousFiveCommand();
                    clearScreen();
                    printManager.printLobby();
                }
                case "NewGame" -> {
                    helpGameCreation();
                    decisionMade = true;
                }
                case "Refresh" -> {
                    listeners.firePropertyChange("RefreshLobby", null, null);
                    decisionMade = true;
                }
                default -> {
                    try {
                        int gameId = Integer.parseInt(input);
                        listeners.firePropertyChange("GameToPlay", null, gameId);
                        decisionMade = true;
                    }
                    catch (NumberFormatException e) {
                        System.out.println(Colors.RED + "Command not recognized!" + Colors.RESET);
                    }
                }
            }
        }
    }

    private void helpGameCreation() {
        int numPlayers = 0;
        do {
            System.out.println("Insert number of players: [2/3]");
            System.out.print("> ");
            try {
                numPlayers = Integer.parseInt(sc.next());
            }
            catch (NumberFormatException e) {
                System.out.println(Colors.RED + "You have to insert a number!" + Colors.RESET);
            }
        } while (numPlayers != 2 && numPlayers != 3);
        boolean rules;
        System.out.println("Insert rules types: to create an expert game type 'expert', otherwise a simple game will be created");
        System.out.print("> ");
        rules = sc.next().equals("expert");
        listeners.firePropertyChange("NewGame", numPlayers, rules);
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
            System.out.print("> ");
            try {
                choice = TowerType.valueOf(sc.next().toUpperCase());
                towerChosen = true;
            } catch (IllegalArgumentException e) {
                System.out.println(Colors.RED + "Not a valid input, retry" + Colors.RESET);
            }
        }
        listeners.firePropertyChange("TowerChoice", null, choice);
    }

    @Override
    public void askWizardChoice(WizardType[] freeWizards){
        System.out.println("You have to choose a wizard, these are the options: " + Arrays.toString(freeWizards));
        boolean wizardChosen = false;
        WizardType choice = null;
        while (!wizardChosen) {
            System.out.print("> ");
            try {
                choice = WizardType.valueOf(sc.next().toUpperCase());
                wizardChosen = true;
            } catch (IllegalArgumentException e) {
                System.out.println(Colors.RED + "Not a valid input, retry" + Colors.RESET);
            }
        }
        listeners.firePropertyChange("WizardChoice", null, choice);
    }

    @Override
    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {
        System.out.print("You entered the lobby, there are currently " + waitingPlayers.length + " players waiting.\n"
                + "Their names are:");
        Arrays.stream(waitingPlayers).forEach(p -> System.out.print(" " + p));
        System.out.println("\nRemember: this game requires " + numPlayers + " players and the rules are "
                + ((rules) ? "expert" : "simple"));
    }

    @Override
    public void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {
        clearScreen();
        printer.printMap();
        System.out.println("This is what you can do: ");
        for (int i = 0; i < actions.size(); i++) {
            String actionCommand = ((currentPossibleActions.contains(actionCommands.get(i))) ? Colors.YELLOW : "") +
                    actionCommands.get(i) + Colors.RESET;
            System.out.println("\t- " + actions.get(i) + " (" + actionCommand + ")");
        }
        System.out.println("To end your turn simply type 'EndTurn'. If you need help type 'Help'");
    }

    @Override
    public void askAction(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {
        System.out.print("> ");
        String actionName = sc.next();
        if (actionName.equals("EndTurn")) {
            listeners.firePropertyChange(actionName, null, null);
        }else if (actionName.equals("Help")) {
            helper.onHelpRequest();
            printTurnMenu(actions, actionCommands, currentPossibleActions);
            askAction(actions, actionCommands, currentPossibleActions);
        } else if (actionCommands.contains(actionName)){
            String action = sc.nextLine();
            String correctedAction = action;
            for (int i = 0; action.charAt(i) == ' '; i++) {
                correctedAction = correctedAction.substring(1);
            }
            listeners.firePropertyChange(actionName, null, correctedAction);
        } else {
            String action = sc.nextLine(); //to reset input
            System.out.println(Colors.RED + "Action not recognized, retry" + Colors.RESET);
            askAction(actions, actionCommands, currentPossibleActions);
        }
    }

    @Override
    public void onGameInitialization(ModelView modelView) {
        printer = new MapPrinter(0, 0);
        this.modelView = modelView;
        printer.initializeMap(modelView, nickname);
        helper = new UserHelper(modelView, sc);
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        String endGameMessage = null;
        switch (evt.getPropertyName()) {
            case "CloudsRefill" -> printer.recomputeCloudMap();
            case "IslandStudentsUpdate" -> printer.replaceIsland((Integer) evt.getNewValue());
            case "IslandTowerUpdate" -> {
                printer.replaceIsland((Integer) evt.getNewValue());
                printer.recomputeSchoolMap(); //can be better
            }
            case "MotherNatureUpdate" -> {
                printer.replaceIsland((Integer) evt.getOldValue());
                printer.replaceIsland((Integer) evt.getNewValue());
            }
            case "IslandUnification" -> printer.recomputeIslandMap();
            case "SchoolDiningRoomUpdate", "CoinsUpdate", "SchoolSwap", "EntranceSwap", "EntranceUpdate", "AssistantUpdate"->
                    printer.replaceSchool((String) evt.getNewValue());
            case "ProfessorUpdate" -> {
                if (evt.getOldValue() != null) {
                    printer.replaceSchool((String) evt.getOldValue());
                }
                printer.replaceSchool((String) evt.getNewValue());
            }
            case "PickFromCloud" -> {
                printer.replaceCloud((Integer) evt.getNewValue());
                printer.replaceSchool((String) evt.getSource());
            }
            case "CharacterStudents", "CharacterPrice" -> printer.replaceCharacter((Integer) evt.getNewValue());
            case "NoEntryTileUpdate" -> {
                printer.replaceCharacter((Integer) evt.getOldValue());
                printer.replaceIsland((Integer) evt.getNewValue());
            }
            case "NewTurn" -> {
                clearScreen();
                printer.printMap();
                System.out.println("Now is " + evt.getNewValue() + "'s turn");
            }
            case "Winner" ->{
                PrintStaticMessage.printWin();
                endGameMessage = "You have won!";
            }
            case "Loser" -> {
                PrintStaticMessage.printLoose();
                endGameMessage = "You have lose. " + evt.getNewValue() + " has won";
            }
            case "Tie" -> {
                PrintStaticMessage.printTie();
                endGameMessage = "It's a tie. These are the tiers: " + Arrays.toString((String[]) evt.getNewValue());
            }
            case "TieLoser" -> {
                PrintStaticMessage.printLoose();
                endGameMessage = "You have lose, but nobody has won, these are the tiers" +
                        Arrays.toString((String[]) evt.getNewValue());
            }
        }
        if (endGameMessage != null) {
            onEndGame(endGameMessage);
        }
    }

    private void onEndGame(String message) {
        System.out.println(message);
        System.out.println();
        System.out.println("Insert 'ok' to return to the global lobby or 'q' to quit the application");
        String command;
        do {
            command = sc.next();
        } while(!command.equals("q") && !command.equals("ok"));
        if (command.equals("q")) {
            //TODO: shutdown application (find how to do that)
        }
    }

    public void onError(ErrorMessageType error) {
        System.out.println(Colors.RED + "Received error: " + error + Colors.RESET);
    }
}
