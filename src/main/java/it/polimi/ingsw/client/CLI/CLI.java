package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.utils.*;
import it.polimi.ingsw.client.ConnectionToServer;
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

/**
 * Class CLI is used to display the command line interface  of the game
 */
public class CLI implements Runnable, UserInterface {
    private final Socket socket;
    private final Scanner sc = new Scanner(System.in);
    private String nickname;
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private MapPrinter printer;
    private UserHelper helper;
    private InputManager inputManager;
    private boolean isGameFinished = false;

    /**
     * main of the CLI that initialize components (for example the connection)
     *
     * @param args arguments of the program
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String serverAddress;
        int serverPort;
        Socket socket = null;
        boolean serverSocketCreated = false;
        while (!serverSocketCreated) {
            System.out.println("Insert server ip address: ");
            System.out.print("> ");
            serverAddress = sc.next();
            System.out.println("Insert server port: ");
            System.out.print("> ");
            serverPort = sc.nextInt();
            try {
                socket = new Socket(serverAddress, serverPort);
                serverSocketCreated = true;
            } catch (IOException e) {
                System.out.println(Colors.RED + "Error in connection with server" + Colors.RESET);
            } catch (IllegalArgumentException e) { //if port is not a valid port (out of range)
                System.out.println(Colors.RED + e.getMessage() + Colors.RESET);
            }
        }
        System.out.println("Connection Established");
        new CLI(socket).run();
    }

    /**
     * Construct a new CLI associated with the defined socket
     *
     * @param socket socketo of the CLI
     */
    public CLI(Socket socket) {
        this.socket = socket;
    }

    /**
     * clearScreen role is to clear the screen after all action that do some changes in the game
     */
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

    /**
     * Add a property change listener in this class, that will listen this class on specify propertyName
     *
     * @param listener the PropertyChangeListener to be added
     * @param propertyName the property name that the listener will listen to
     */
    @Override
    public void addListener(PropertyChangeListener listener, String propertyName) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * run method print the beginning of the game before the connection and start a new connection
     */
    @Override
    public void run() {
        clearScreen();
        PrintStaticMessage.printWelcome();
        ConnectionToServer connectionToServer = new ConnectionToServer(socket, this);
        Thread t = new Thread(connectionToServer);
        t.start();
        inputManager = new InputManager(sc);
        Thread inputThread = new Thread(inputManager);
        inputThread.start();
        try {
            t.join();
            inputThread.join();
        } catch (InterruptedException e) {
            System.out.println(Colors.RED + "A fatal error has occurred, application will now close" + Colors.RESET);
            connectionToServer.setActive(false);
            System.exit(1);
        }
    }

    /**
     * askNickname prints the request for the nickname
     */
    @Override
    public void askNickname() {
        System.out.println("Insert a username:");
        String nickname;
        do {
            inputManager.setInputPermitted(true);
            System.out.print("> ");
            nickname = inputManager.getLastInput();
        } while (nickname == null || nickname.isBlank());
        this.nickname = nickname;
        inputManager.setInputPermitted(false);
        listeners.firePropertyChange("Nickname", null, nickname);
    }

    /**
     * Returns the nickname of the player associated at the CLI
     *
     * @return he nickname of the player associated at the CLI
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * displayGlobalLobby prints the global lobby on command line interface
     *
     * @param numGames the number of games on the server that are not already started
     * @param gamesInfo all the games not started information (number of players, rules, players nicknames)
     */
    @Override
    public void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo) {
        clearScreen();
        System.out.println("There are currently " + numGames + " games not started.");
        LobbyPrintManager printManager = new LobbyPrintManager(gamesInfo);
        printManager.printLobby();
        String message = "Insert 'NewGame' to create a new game, 'Refresh' to update the global lobby";
        if (numGames > 5) {
            message += ", '5>' to view the next 5 games and '<5' to view the previous 5 games.";
        } else message += ".";
        message += " To enter a game lobby, insert the id of the game";
        boolean decisionMade = false;
        while (!decisionMade) {
            System.out.println(message);
            inputManager.setInputPermitted(true);
            System.out.print("> ");
            String input = inputManager.getLastInput();
            isGameFinished = false;
            inputManager.setInputPermitted(false);
            switch (input) {
                case "5>" -> {
                    if (printManager.onNextFiveCommand()) {
                        clearScreen();
                        printManager.printLobby();
                    }
                }
                case "<5" -> {
                    if (printManager.onPreviousFiveCommand()) {
                        clearScreen();
                        printManager.printLobby();
                    }
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

    /**
     * helpGameCreation prints the steps for creating a new game
     */
    private void helpGameCreation() {
        int numPlayers = 0;
        inputManager.setInputPermitted(true);
        do {
            System.out.println("Insert number of players: [2/3]");
            System.out.print("> ");
            try {
                numPlayers = Integer.parseInt(inputManager.getLastInput());
            }
            catch (NumberFormatException e) {
                System.out.println(Colors.RED + "You have to insert a number!" + Colors.RESET);
            }
        } while (numPlayers != 2 && numPlayers != 3);
        boolean rules;
        System.out.println("Insert rules types: to create an expert game type 'expert', otherwise a simple game will be created");
        System.out.print("> ");
        //rules = sc.next().equals("expert");
        rules = inputManager.getLastInput().equals("expert");
        inputManager.setInputPermitted(false);
        listeners.firePropertyChange("NewGame", numPlayers, rules);
    }

    /**
     * Prints a string in the CLI
     *
     * @param message the message to be notified
     */
    @Override
    public void displayStringMessage(String message) {
        System.out.println(message);
    }

    /**
     * askTowerChoice prints the request for choosing the tower,
     * show also the remaining towers
     *
     * @param freeTowers the towers that the client can choose
     */
    @Override
    public void askTowerChoice(TowerType[] freeTowers){
        if (freeTowers.length == 1) {
            System.out.println("Only the " + freeTowers[0] + " tower remain. It will be assigned to you.");
            listeners.firePropertyChange("TowerChoice", null, freeTowers[0]);
            return;
        }
        System.out.println("You have to choose a tower, these are the options: " + Arrays.toString(freeTowers));
        boolean towerChosen = false;
        TowerType choice = null;
        inputManager.setInputPermitted(true);
        while (!towerChosen) {
            System.out.print("> ");
            try {
                choice = TowerType.valueOf(inputManager.getLastInput().toUpperCase());
                towerChosen = true;
            } catch (IllegalArgumentException e) {
                System.out.println(Colors.RED + "Not a valid input, retry" + Colors.RESET);
            }
        }
        inputManager.setInputPermitted(false);
        listeners.firePropertyChange("TowerChoice", null, choice);
        System.out.println("Tou have chosen the " + choice + " tower. Now wait for other players choices.");
    }

    /**
     * askWizardChoice prints the request for choosing the wizard,
     * show also the remaining wizards
     *
     * @param freeWizards the wizards that the client can choose
     */
    @Override
    public void askWizardChoice(WizardType[] freeWizards){
        if (freeWizards.length == 1) {
            System.out.println("Only the " + freeWizards[0] + " wizard remain. It will be assigned to you.");
            listeners.firePropertyChange("WizardChoice", null, freeWizards[0]);
            return;
        }
        System.out.println("You have to choose a wizard, these are the options: " + Arrays.toString(freeWizards));
        boolean wizardChosen = false;
        WizardType choice = null;
        inputManager.setInputPermitted(true);
        while (!wizardChosen) {
            System.out.print("> ");
            try {
                choice = WizardType.valueOf(inputManager.getLastInput().toUpperCase());
                wizardChosen = true;
            } catch (IllegalArgumentException e) {
                System.out.println(Colors.RED + "Not a valid input, retry" + Colors.RESET);
            }
        }
        inputManager.setInputPermitted(false);
        listeners.firePropertyChange("WizardChoice", null, choice);
        System.out.println("Tou have chosen the wizard " + choice + ". Now wait for other players choices.");
    }

    /**
     * displayLobbyInfo prints teh info of the lobby (as number of players, rules etc...)
     *
     * @param numPlayers the number of players of the game
     * @param rules the type of rules
     * @param waitingPlayers the players that are already connected to the game
     */
    @Override
    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {
        System.out.print("You entered the lobby, there are currently " + waitingPlayers.length + " players waiting.\n"
                + "Their names are:");
        Arrays.stream(waitingPlayers).forEach(p -> System.out.print(" " + p));
        System.out.println("\nRemember: this game requires " + numPlayers + " players and the rules are "
                + ((rules) ? "expert" : "simple"));
    }

    /**
     * onPlayerJoined prints the nickname of the player who joined the game
     *
     * @param playerName the name of the player
     */
    @Override
    public void onPlayerJoined(String playerName) {
        System.out.println(playerName + " has joined the game");
    }

    /**
     * onGameStarted print that the game is starting just after all players joined the game
     */
    @Override
    public void onGameStarted() {
        System.out.println("The game will start soon!\nWait for other players choices");
    }

    /**
     * printTurnMenu prints the actions that you can do during your turn and put in evidence which action
     * you can do in that specific moment
     *
     * @param actions all the actions descriptions that the player can do
     * @param actionCommands all the actions command to call the actions that the player can do
     * @param currentPossibleActions all the actions commands of the actions that the client can do without doing
     */
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

    /**
     * askAction prints the request for the action
     *
     * @param actions all the actions descriptions that the player can do
     * @param actionCommands all the actions command to call the actions that the player can do
     * @param currentPossibleActions all the actions commands of the actions that the client can do without doing
     */
    @Override
    public void askAction(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {
        System.out.print("> ");
        inputManager.setInputPermitted(true);
        String command = inputManager.getLastInput();
        inputManager.setInputPermitted(false);
        String actionName = command.split(" ")[0];
        if (isGameFinished) {
            //if someone disconnects, this is a sort of rollback (last input does not concern this method)
           inputManager.restoreLastInput(command);
        } else if (actionName.equals("EndTurn")) {
            listeners.firePropertyChange(actionName, null, null);
        }else if (actionName.equals("Help")) {
            helper.onHelpRequest();
            printTurnMenu(actions, actionCommands, currentPossibleActions);
            askAction(actions, actionCommands, currentPossibleActions);
        } else if (actionCommands.contains(actionName)) {
            String action = command.substring(actionName.length());
            String correctedAction = action;
            for (int i = 0; i < action.length() && action.charAt(i) == ' '; i++) {
                correctedAction = correctedAction.substring(1);
            }
            listeners.firePropertyChange(actionName, null, correctedAction);
        } else {
            System.out.println(Colors.RED + "Action not recognized, retry" + Colors.RESET);
            askAction(actions, actionCommands, currentPossibleActions);
        }
    }

    /**
     * Initialize the game map printed on CLI
     *
     * @param modelView the ModelView of the game
     */
    @Override
    public void onGameInitialization(ModelView modelView) {
        printer = new MapPrinter(0, 0);
        printer.initializeMap(modelView, nickname);
        helper = new UserHelper(modelView, inputManager);
    }

    /**
     * Received an event from a message handler and process it based of the type of the event
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        String endGameMessage = null;
        switch (evt.getPropertyName()) {
            case "CloudsRefill" -> printer.recomputeCloudMap();
            case "IslandStudentsUpdate" -> printer.replaceIsland((Integer) evt.getSource());
            case "IslandTowerUpdate" -> {
                printer.replaceIsland((Integer) evt.getNewValue());
                printer.recomputeSchoolMap(); //can be better
            }
            case "MotherNatureUpdate" -> {
                printer.replaceIsland((Integer) evt.getOldValue());
                printer.replaceIsland((Integer) evt.getNewValue());
            }
            case "IslandUnification" -> printer.recomputeIslandMap();
            case "DiningRoomInsertion", "DiningRoomRemoval", "EntranceSwap", "SchoolSwap" ->
                    printer.replaceSchool((String) evt.getSource());
            case "CoinsUpdate", "EntranceUpdate", "AssistantUpdate"-> printer.replaceSchool((String) evt.getNewValue());
            case "ProfessorUpdate" -> {
                if (evt.getOldValue() != null) {
                    printer.replaceSchool((String) evt.getOldValue());
                }
                printer.replaceSchool((String) evt.getNewValue());
            }
            case "CloudSelected" -> {
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
                clearScreen();
                PrintStaticMessage.printWin();
                endGameMessage = "You have won!";
            }
            case "Loser" -> {
                clearScreen();
                PrintStaticMessage.printLoose();
                endGameMessage = "You have lose. " + evt.getNewValue() + " has won";
            }
            case "Tie" -> {
                clearScreen();
                PrintStaticMessage.printTie();
                endGameMessage = "It's a tie. These are the tiers: " + Arrays.toString((String[]) evt.getNewValue());
            }
            case "TieLoser" -> {
                clearScreen();
                PrintStaticMessage.printLoose();
                endGameMessage = "You have lose, but nobody has won, these are the tiers" +
                        Arrays.toString((String[]) evt.getNewValue());
            }
            case "Disconnection" -> {
                clearScreen();
                endGameMessage = evt.getNewValue() + " have disconnected";
            }
            case "GameDeleted" -> {
                clearScreen();
                endGameMessage = evt.getNewValue() + " has decided to not resume the game. The game has been deleted";
            }
        }
        if (endGameMessage != null) {
            onEndGame(endGameMessage);
        }
    }

    /**
     * onEndGame ask the user if he want to go back to lobby or quit the game, just after having printed the
     * static message of win, loose, tie.
     *
     * @param message what the method has to print
     */
    private void onEndGame(String message) {
        System.out.println("\n" + message);
        System.out.println();
        System.out.println("Insert 'ok' to return to the global lobby or 'q' to quit the application");
        inputManager.setInputPermitted(true);
        String command;
        do {
            System.out.print("> ");
            command = inputManager.getLastInput();
        } while(!command.equals("q") && !command.equals("ok"));
        inputManager.setInputPermitted(false);
        if (command.equals("q")) {
            inputManager.setActive(false);
            System.exit(0);
        } else {
            isGameFinished = true;
            listeners.firePropertyChange("RefreshLobby", null, null);
        }
    }

    /**
     * onError prints errors colored in red
     *
     * @param error the error type
     * @param info the error description
     */
    @Override
    public void onError(ErrorMessageType error, String info) {
        if (error != null) {
            System.out.println(Colors.RED + "Received error " + error + ": " + info + Colors.RESET);
        } else {
            System.out.println(Colors.RED + info + Colors.RESET);
        }
    }

    /**
     * onResumeGame ask teh users if they want to resume a saved games or not
     *
     * @param numPlayers the number of players in the saved game
     * @param rules the rules of the saved games
     * @param participants array of participants' name
     */
    public void onResumeGame(int numPlayers, boolean rules, String[] participants){
        clearScreen();
        String ruleInString = (rules)? "expert" : "simple";
        System.out.print("\nYou have an unfinished game saved on the server\n\nHere the specifications of the game:" +
                "\n- Number Of Players: " + numPlayers +
                "\n- Rules: " + ruleInString +
                "\n- List of Participants: ");
        for(int i = 0; i < numPlayers; i++){
            if(i == numPlayers - 1){
                System.out.print(participants[i]);
            }
            else{
                System.out.print(participants[i]+", ");
            }
        }
        System.out.println("\nDo you want to resume game? [y/n]");
        inputManager.setInputPermitted(true);
        String reply;
        do{
            System.out.print("> ");
            reply = inputManager.getLastInput();
        }while (!reply.equals("y") && !reply.equals("n") && !reply.equals("Y") && !reply.equals("N"));
        if(reply.equals("y") || reply.equals("Y")){
            listeners.firePropertyChange("RestoreGame", null, null);
        }
        else{
            listeners.firePropertyChange("DeleteSavedGame", null,null);
        }
    }

    /**
     * Shutdown the application after a connection error
     */
    @Override
    public void shutdown() {
        System.err.println(Colors.RED + "Application will now close" + Colors.RESET);
        System.exit(0);
    }
}
