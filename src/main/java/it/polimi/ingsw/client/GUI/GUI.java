package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.GUI.controllers.*;
import it.polimi.ingsw.client.GUI.items.*;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Triplet;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUI extends Application implements UserInterface {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private String nickname;
    private ModelView modelView;
    private boolean nicknameSent = false;
    private Stage stage;
    private Scene scene;
    private FXMLController currentSceneController;
    //This executor is used to not use the javafx thread to compute actions (so javafx thread is only
    //used for javafx stuffs)
    private final ExecutorService responseHandlerExecutor = Executors.newSingleThreadExecutor();
    private ResizeListener resizeListener;

    /**
     * Starts the application
     *
     * @param stage the main stage of the application
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/welcomeToEriantys.fxml"));
        scene = new Scene(fxmlLoader.load());
        this.stage = stage;
        this.stage.setTitle("Eriantys");
        this.stage.setScene(scene);
        this.stage.setFullScreen(true);
        this.stage.show(); //display the stage on the screen
        this.stage.centerOnScreen();
        this.stage.setMinWidth(1152);
        this.stage.setMinHeight(648);
        this.stage.getIcons().add(new Image((Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/background/Eriantys.jpg")))));
        /*this.stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) stage.setMaximized(true);
        });*/
        WelcomeController welcomeController = fxmlLoader.getController();
        welcomeController.addGUI(this);
        welcomeController.addListener(this);
        currentSceneController = welcomeController;

        //TODO: implement or delete (also the class)
        resizeListener = new ResizeListener(this.stage, this.scene);
        //resizeListener.registerHandlers();
    }

    /**
     * Returns the nickname of the user
     *
     * @return the nickname of the user
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * Ask the nickname to the user, if the user have already inserted a nickname it sends back it
     */
    @Override
    public void askNickname() {
        if (nickname != null && !nicknameSent) {
            listeners.firePropertyChange("Nickname", null, nickname);
            nicknameSent = true;
        }
    }

    /**
     * Show the global lobby scene that contains a table with all games that are not already full on the server
     *
     * @param numGames the number of games on the server that are not already started
     * @param gamesInfo all the games not started information (number of players, rules, players nicknames)
     */
    @Override
    public void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo) {
        Platform.runLater(() -> {
            if (currentSceneController instanceof WelcomeController welcomeController && welcomeController.isShowingMainMenu()) {
                welcomeController.showMainMenu(nickname);
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/globalLobby.fxml"));
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                this.stage.setScene(scene);
                this.stage.setFullScreen(stage.fullScreenProperty().get());
                GlobalLobbyController sceneController = fxmlLoader.getController();
                sceneController.addListener(this);
                sceneController.constructTable(gamesInfo);
                sceneController.setNickname(nickname);
                sceneController.setGui(this);
                currentSceneController = sceneController;
                this.stage.show();
                resizeListener.setScene(scene);
            }
        });
    }

    /**
     * Display all information about the game lobby on which the player has entered
     *
     * @param numPlayers the number of players of the game
     * @param rules the type of rules
     * @param waitingPlayers the players that are already connected to the game
     */
    @Override
    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/setupScene.fxml"));
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            this.stage.setScene(scene);
            this.stage.setFullScreen(stage.fullScreenProperty().get());
            SetupChoiceSceneController sceneController = fxmlLoader.getController();
            sceneController.addListener(this);
            sceneController.showGameLobby(numPlayers, rules, waitingPlayers);
            sceneController.setGui(this);
            currentSceneController = sceneController;
            this.stage.show();
            resizeListener.setScene(scene);
        });
    }

    /**
     * Informs the user that the specified player has joined the game lobby
     *
     * @param playerName the name of the player
     */
    @Override
    public void onPlayerJoined(String playerName) {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.onPlayerJoined(playerName);
        });
    }

    /**
     * Inform the user that the game is about to start
     */
    @Override
    public void onGameStarted() {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.onGameStarted();
        });
    }

    /**
     * Ask the client to choose a tower in the setup phase of a game
     *
     * @param freeTowers the towers that the client can choose
     */
    @Override
    public void askTowerChoice(TowerType[] freeTowers) {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.setSceneWithTowers(freeTowers);
        });
    }

    /**
     * Ask the client to choose a wizard in the setup phase of a game
     *
     * @param freeWizards the wizards that the client can choose
     */
    @Override
    public void askWizardChoice(WizardType[] freeWizards) {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.setSceneWithWizards(freeWizards);
        });
    }

    /**
     * Notifies the client to choose between starting a new game or resuming the previous saved game
     *
     * @param numPlayers the number of players in the saved game
     * @param rules the rules of the saved games
     * @param participants array of participants' name
     */
    @Override
    public void onResumeGame(int numPlayers, boolean rules, String[] participants) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/previousGameScene.fxml"));
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            this.stage.setScene(scene);
            this.stage.setFullScreen(stage.fullScreenProperty().get());
            PreviousGameController previousGameController = fxmlLoader.getController();
            previousGameController.addListener(this);
            previousGameController.init(numPlayers, rules, participants);
            this.stage.show();
            resizeListener.setScene(scene);
        });
    }

    @Override
    public void askAction(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {}

    @Override
    public void displayStringMessage(String message) {}

    /**
     * Adds the specified PropertyChangeListener to the user interface that will listen to the specified property name
     *
     * @param listener the PropertyChangeListener to be added
     * @param propertyName the property name that the listener will listen to
     */
    @Override
    public void addListener(PropertyChangeListener listener, String propertyName) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Performs all operations that need to be done when the game start
     *
     * @param modelView the ModelView of the game
     */
    @Override
    public void onGameInitialization(ModelView modelView) {
        this.modelView = modelView;
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/fxml/gameMainScene.fxml"));
            try {
                scene = new Scene(loader.load());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            stage.setScene(scene);
            this.stage.setFullScreen(stage.fullScreenProperty().get());
            currentSceneController = loader.getController();
            ((GameMainSceneController) currentSceneController).initializeBoard(modelView, this.nickname);
            currentSceneController.addListener(this);
            stage.show();
            resizeListener.setScene(scene);
        });
    }

    /**
     * Notifies the user that it is his turn
     *
     * @param actions all the actions descriptions that the player can do
     * @param actionCommands all the actions command to call the actions that the player can do
     * @param currentPossibleActions all the actions commands of the actions that the client can do without doing
     *                               anything before
     */
    @Override
    public void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {
        Platform.runLater(() -> {
            ((GameMainSceneController) currentSceneController).onTurn(actionCommands, currentPossibleActions);
        });
    }

    /**
     * Respond to an event that can come from a message handler or from a FXMLController
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "AssistantUpdate" -> onAssistantUpdate((Integer) evt.getOldValue(), (String) evt.getNewValue());
            case "IslandStudentsUpdate" -> onIslandStudentsUpdate((Integer) evt.getSource(), (RealmType[]) evt.getNewValue(), (Boolean) evt.getOldValue());
            case "IslandTowerUpdate" -> onTowerUpdate((TowerType) evt.getOldValue(), (Integer) evt.getNewValue());
            case "IslandUnification" -> onIslandsUnification(new ArrayList<>(Arrays.asList((Integer[]) evt.getNewValue())));
            case "MotherNatureUpdate" -> onMotherNatureMovement((Integer) evt.getOldValue(), (Integer) evt.getNewValue());
            case "DiningRoomInsertion" -> onDiningRoomUpdate((String) evt.getSource(), (RealmType[]) evt.getNewValue(), (Boolean) evt.getOldValue(), true);
            case "ProfessorUpdate" -> onProfessorUpdate((RealmType) evt.getSource(), (String) evt.getOldValue(), (String) evt.getNewValue());
            case "DiningRoomRemoval" -> onDiningRoomUpdate((String) evt.getSource(), (RealmType[]) evt.getNewValue(), false, false);
            case "CharacterPlayed" -> onCharacterPlayed((Integer) evt.getNewValue());
            case "CharacterStudents" -> onCharacterStudentsUpdate((Integer) evt.getNewValue());
            case "CharacterPrice" -> onCharacterPriceUpdate((Integer) evt.getNewValue());
            case "NoEntryTileUpdate" -> onNoEntryTileUpdate((Integer) evt.getNewValue(), (Integer) evt.getOldValue());
            case "EntranceSwap" -> onEntranceSwap((String) evt.getSource());
            case "CoinsUpdate" -> onCoinsUpdate((String) evt.getNewValue());
            case "SchoolSwap" -> onSchoolSwap((String) evt.getSource(), (RealmType[]) evt.getOldValue(), (RealmType[]) evt.getNewValue());
            case "CloudSelected" -> onCloudsSelection((String) evt.getSource(), (Integer) evt.getNewValue(), (RealmType[]) evt.getOldValue());
            case "CloudsRefill" -> onCloudRefill();
            case "NewTurn" -> onNewTurn((String) evt.getNewValue());
            case "Loser", "Winner", "Tie", "TieLoser" -> onEndGameEvent(evt);
            case "Disconnection" -> onDisconnection(evt);
            case "GameDeleted" -> onGameDeleted((String) evt.getNewValue());
            //events that come from gui controllers
            default -> checkEventFromControllers(evt);
        }
    }

    /**
     * Notifies the user that a new turn is started, where the new active player is not the user
     *
     * @param newActivePlayer the new active player nickname
     */
    private void onNewTurn(String newActivePlayer) {
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).notifyNewTurn(newActivePlayer));
    }

    /**
     * Updates the island map by unifying the specified islands
     *
     * @param islands the islands that are unified
     */
    private void onIslandsUnification(List<Integer> islands) {
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).mergeIslands(islands));
    }

    /**
     * Updates the price of the character by adding a coin on it
     *
     * @param characterId the of the character
     */
    private void onCharacterPriceUpdate(int characterId) {
        Platform.runLater(() -> {
            CharacterImage character = ((GameMainSceneController) currentSceneController).getCharacterImage(characterId);
            character.putCoin();
        });
    }

    /**
     * Updates the students that are present in the specified character
     *
     * @param characterId the id of the character
     */
    private void onCharacterStudentsUpdate(int characterId) {
        Platform.runLater(() -> {
            CharacterImage character = ((GameMainSceneController) currentSceneController).getCharacterImage(characterId);
            character.updateStudents();
        });
    }

    /**
     * Put in evidence the specified character for the rest of the turn in the main scene after a player has played it
     *
     * @param characterId the id of the character
     */
    private void onCharacterPlayed(int characterId) {
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).setDropShadowOnCharacter(characterId));
    }

    /**
     * Updates the mother nature position in the island map of the mai scene
     *
     * @param oldIsland the old position of mother nature
     * @param newIsland the new position of mother nature
     */
    private void onMotherNatureMovement(int oldIsland, int newIsland) {
        Platform.runLater(() -> {
            ((GameMainSceneController) currentSceneController).moveMotherNature(oldIsland, newIsland);
        });
    }

    /**
     * Updates the students that are presents on the specified island in the main scene
     *
     * @param islandId the id of the island
     * @param students students that are inserted on the island
     * @param fromEntrance true if the students are moved from the entrance of the active player, otherwise false
     */
    private void onIslandStudentsUpdate(Integer islandId, RealmType[] students, boolean fromEntrance) {
        Platform.runLater(() -> {
            this.stage.setFullScreen(true);
            if (fromEntrance && !modelView.getCurrentActivePlayer().equals(nickname)) {
                String currentPlayer = this.modelView.getCurrentActivePlayer();
                ((GameMainSceneController) currentSceneController).moveStudentsToIsland(currentPlayer, islandId, students);
            } else if (!fromEntrance){
                IslandMap islandMap = ((GameMainSceneController) currentSceneController).getIslandMap();
                islandMap.getIslandById(islandId).ifPresent(island -> Arrays.stream(students).forEach(island::addStudent));
            }
        });
    }

    /**
     * Updates the dining room of th specified player
     *
     * @param player the name of the player
     * @param students the students that are inserted/removed from the dining room
     * @param fromEntrance true if the students are inserted/removed from the entrance, otherwise false
     * @param isInsertion true if students are inserted, otherwise false
     */
    private void onDiningRoomUpdate(String player, RealmType[] students, boolean fromEntrance, boolean isInsertion) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            if (isInsertion) {
                if (fromEntrance && !player.equals(nickname)) {
                    gameMainSceneController.moveStudentsToDiningRoom(player, students);
                } else if (!fromEntrance){
                    SchoolBox schoolBox = gameMainSceneController.getSchoolBox(player);
                    gameMainSceneController.viewSchoolOf(player);
                    Arrays.stream(students).forEach(schoolBox::insertInDiningRoom);
                }
            } else {
                SchoolBox playerBox = gameMainSceneController.getSchoolBox(player);
                gameMainSceneController.viewSchoolOf(player);
                Arrays.stream(students).forEach(playerBox::removeFromDiningRoom);
            }
        });
    }

    /**
     * Updates the specified professor on the main scene by adding it to the school of the specified new owner and by
     * removing it from the specified last owner (if not null)
     *
     * @param professor the type of professor
     * @param lastOwner the last owner of the professor
     * @param newOwner the new owner of the professor
     */
    private void onProfessorUpdate(RealmType professor, String lastOwner, String newOwner) {
        Platform.runLater(() -> {
            ((GameMainSceneController) currentSceneController).insertProfessorAnimation(newOwner, professor);
            if (lastOwner != null) ((GameMainSceneController) currentSceneController).getSchoolBox(lastOwner).removeProfessor(professor);
        });
    }

    /**
     * Updates the last played assistant of the specified player by modifying the assistant image near the player school
     *
     * @param assistant the is of the assistant
     * @param player the player who have played the assistant
     */
    private void onAssistantUpdate(int assistant, String player) {
        Platform.runLater(() -> {
            GameMainSceneController controller = (GameMainSceneController) currentSceneController;
            if (player.equals(nickname)) {
                AssistantsTab assistantsTab = controller.getAssistantsTab();
                assistantsTab.removeAssistantFromDeck(assistant);
            }
            controller.setAssistantImage(player, assistant);
        });
    }

    /**
     * Updates the towers of the specified island in the main scene
     *
     * @param lastTower the tower inserted on the island
     * @param islandId the id of the island
     */
    private void onTowerUpdate(TowerType lastTower, int islandId) {
        TowerType newTower = modelView.getField().getIsland(islandId).getThird();
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).moveTowers(lastTower, newTower, islandId));
    }

    /**
     * Updates the no entry tiles on the specified island on the main scene. The number of no entry tiles of the island is
     * taken from the model view
     *
     * @param islandId the id of the island
     * @param characterId the id of the character that has the no entry tiles
     */
    private void onNoEntryTileUpdate(int islandId, int characterId) {
        Platform.runLater(() -> {
            IslandMap islandMap = ((GameMainSceneController) currentSceneController).getIslandMap();
            islandMap.getIslandById(islandId).ifPresent(island -> {
                        int noEntryTileOnIsland = modelView.getField().getExpertField().getNoEntryTilesOnIsland(islandId);
                        while(island.getNumNoEntryTile() != noEntryTileOnIsland) {
                            if (island.getNumNoEntryTile() < noEntryTileOnIsland) island.insertNoEntryTile();
                            else island.removeNoEntryTile();
                        }
                    });
            //TODO: update character
        });
    }

    /**
     * Updates the entrance of the school of the specified player after the specified player has played the character 7
     *
     * @param playerName the name of the player
     */
    private void onEntranceSwap(String playerName) {
        Platform.runLater(() -> {
            SchoolBox schoolBox = ((GameMainSceneController) currentSceneController).getSchoolBox(playerName);
            schoolBox.updateEntrance();
            ((GameMainSceneController) currentSceneController).viewSchoolOf(playerName);
        });
    }

    /**
     * Modifies the school of the specified players on the main scene after the specified player has played character 10
     *
     * @param playerName the name of the player
     * @param toEntrance students that are inserted to the entrance from the dining room
     * @param toDiningRoom students that are inserted to th dining room from the entrance
     */
    private void onSchoolSwap(String playerName, RealmType[] toEntrance, RealmType[] toDiningRoom) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            SchoolBox schoolBox = gameMainSceneController.getSchoolBox(playerName);
            gameMainSceneController.viewSchoolOf(playerName);
            Arrays.stream(toEntrance).forEach(schoolBox::removeFromDiningRoom);
            Arrays.stream(toDiningRoom).forEach(schoolBox::insertInDiningRoom);
            schoolBox.updateEntrance();
        });
    }

    /**
     * Modifies the coins of the specified player on the game main scene
     *
     * @param playerName the name of the player
     */
    private void onCoinsUpdate(String playerName) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.viewSchoolOf(playerName);
            gameMainSceneController.getSchoolBox(playerName).updateCoins();
        });
    }

    /**
     * Process a CloudSelection event after a player have picked students from the specified cloud
     *
     * @param playerName the name of the player
     * @param cloudId the id of the cloud
     * @param students the students that are picked from the cloud
     */
    private void onCloudsSelection(String playerName, int cloudId, RealmType[] students) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.moveStudentsFromCloud(playerName, cloudId, students);
        });
    }

    /**
     * Process a clod refill event by adding students to cloud
     */
    private void onCloudRefill() {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.moveStudentsToRefillClouds();
        });
    }

    /**
     * Handles an end game event by displaying a dialog on the game main scene. The user will be asked
     * if he wants to quit or to return to the main menu
     *
     * @param evt the end game event
     */
    private void onEndGameEvent(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.onEndGameEvent(evt);
        });
    }

    /**
     * Handles a player disconnection by displaying a dialog on the current scene. The user will be asked
     * if he wants to quit or to return to the main menu
     *
     * @param evt the disconnection event
     */
    private void onDisconnection(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            if (currentSceneController instanceof GameMainSceneController gameMainSceneController) {
                gameMainSceneController.onEndGameEvent(evt);
            } else if (currentSceneController instanceof SetupChoiceSceneController setupChoiceSceneController){
                setupChoiceSceneController.onGameDeleted("Oh no, " + evt.getNewValue() + " have disconnected!" +
                        "\n Decide what to do: you can return to the main menu or exit the application.");
            }
        });
    }

    /**
     * Responds to events that come from a FXMLController. The behaviour depends on the name of the event, if the event is
     * a user action, the event will be fires in the listener chain
     *
     * @param evt the event fired from a FXMLController
     */
    private void checkEventFromControllers(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Quit")) System.exit(0);
        else if (evt.getPropertyName().equals("QuitGame") || evt.getPropertyName().equals("DeleteSavedGame")) {
            this.returnToMainMenu();
        }
        responseHandlerExecutor.submit(() -> listeners.firePropertyChange(evt));
        if (evt.getPropertyName().equals("Nickname")) this.nickname = (String) evt.getNewValue();
    }

    /**
     * Performs the setup operations after the user has clicked the submit button on the first scene that contains the
     * server parameters and the username. The method will create a connection to the server.
     *
     * @param serverIp the ip of the server
     * @param serverPort the port of the server
     * @param nickname the username chosen by the user
     */
    public void doSetup(String serverIp, int serverPort, String nickname) {
        this.nickname = nickname;
        Socket socket;
        try {
            socket = new Socket(serverIp, serverPort);
        } catch (IOException e) {
            System.err.println("Error in connection with server");
            currentSceneController.onError(null, "Error in connection with server");
            return; //TODO
        }
        System.out.println("Connection Established");
        ConnectionToServer connectionToServer = new ConnectionToServer(socket, this);
        Thread connectionHandlerThread = new Thread(connectionToServer);
        connectionHandlerThread.start();
        stage.setOnCloseRequest(event -> System.exit(0));
    }

    /**
     * Notifies the client that an error from the server has occurred. The behaviour depends on the current scene.
     *
     * @param error the error type
     * @param info the error description
     */
    @Override
    public void onError(ErrorMessageType error, String info) {
        Platform.runLater(() -> currentSceneController.onError(error, info));
    }

    /**
     * Display a dialog on the setup scene to inform that the specified player has decided to not resume the previously
     * saved game. The user will be asked to decide if he wants to quit or to return to the main menu.
     *
     * @param playerName the player who deleted the game
     */
    public void onGameDeleted(String playerName) {
        Platform.runLater(() -> {
            SetupChoiceSceneController setupChoiceSceneController = (SetupChoiceSceneController) currentSceneController;
            setupChoiceSceneController.onGameDeleted("Oh no! " + playerName +
                    " has decided to delete the game. The game will be destroyed.\n" +
                    "What do you want to do?");
        });
    }

    /**
     * Returns to the main menu of the game
     */
    public void returnToMainMenu() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/welcomeToEriantys.fxml"));
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        this.stage.setScene(scene);
        this.stage.setFullScreen(stage.fullScreenProperty().get());
        WelcomeController welcomeController = fxmlLoader.getController();
        welcomeController.addGUI(this);
        welcomeController.addListener(this);
        welcomeController.showMainMenu(nickname);
        currentSceneController = welcomeController;
        this.stage.show();
        resizeListener.setScene(scene);
    }

    /**
     * Displays an alert to inform that there was an error in the connection with the server, therefore the application
     * will close when the alert is closed
     */
    @Override
    public void shutdown() {
        Platform.runLater(() -> {
            currentSceneController.displayShutdownAlert();
        });
    }
}
