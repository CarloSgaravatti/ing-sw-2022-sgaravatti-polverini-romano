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
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: delete most of set full scene (I have put them because i need to switch to gui from cli window playing all in localhost)
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

    public GUI() {

    }

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
        this.stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) stage.setMaximized(true);
        });
        WelcomeController welcomeController = fxmlLoader.getController();
        welcomeController.addGUI(this);
        welcomeController.addListener(this);
        currentSceneController = welcomeController;

        resizeListener = new ResizeListener(this.stage, this.scene);
        //resizeListener.registerHandlers();
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void askNickname() {
        if (nickname != null && !nicknameSent) {
            listeners.firePropertyChange("Nickname", null, nickname);
            nicknameSent = true;
        }
    }

    @Override
    public void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo) {
        Platform.runLater(() -> {
            if (currentSceneController instanceof WelcomeController welcomeController && welcomeController.isShowingMainMenu()) {
                welcomeController.showMainMenu(nickname);
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/globalLobby2.fxml"));
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    //TODO
                }
                this.stage.setScene(scene);
                GlobalLobbyController sceneController = fxmlLoader.getController();
                sceneController.addListener(this);
                sceneController.constructTable(gamesInfo);
                sceneController.setNickname(nickname);
                currentSceneController = sceneController;
                this.stage.show();
                resizeListener.setScene(scene);
            }
        });
    }

    @Override
    public void displayStringMessage(String message) {

    }

    @Override
    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/setupScene.fxml"));
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                //TODO
            }
            this.stage.setScene(scene);
            SetupChoiceSceneController sceneController = fxmlLoader.getController();
            sceneController.addListener(this);
            sceneController.showGameLobby(numPlayers, rules, waitingPlayers);
            sceneController.setGui(this);
            currentSceneController = sceneController;
            this.stage.show();
            resizeListener.setScene(scene);
        });
    }

    @Override
    public void onPlayerJoined(String playerName) {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.onPlayerJoined(playerName);
        });
    }

    @Override
    public void onGameStarted() {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.onGameStarted();
        });
    }

    @Override
    public void askTowerChoice(TowerType[] freeTowers) {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.setSceneWithTowers(freeTowers);
        });
    }

    @Override
    public void askWizardChoice(WizardType[] freeWizards) {
        Platform.runLater(() -> {
            SetupChoiceSceneController sceneController = (SetupChoiceSceneController) currentSceneController;
            sceneController.setSceneWithWizards(freeWizards);
        });
    }

    @Override
    public void onResumeGame(int numPlayers, boolean rules, String[] participants) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/previousGameScene.fxml"));
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                //TODO
            }
            this.stage.setScene(scene);
            PreviousGameController previousGameController = fxmlLoader.getController();
            previousGameController.addListener(this);
            previousGameController.init(numPlayers, rules, participants);
            this.stage.show();
            resizeListener.setScene(scene);
        });
    }

    @Override
    public void askAction(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {

    }

    @Override
    public void addListener(PropertyChangeListener listener, String propertyName) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void onGameInitialization(ModelView modelView) {
        this.modelView = modelView;
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/fxml/gameMainScene.fxml"));
            try {
                scene = new Scene(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(scene);
            stage.setFullScreen(true);
            currentSceneController = loader.getController();
            //TODO: delete try catch when everything is ok
            try {
                ((GameMainSceneController) currentSceneController).initializeBoard(modelView, this.nickname);
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentSceneController.addListener(this);
            stage.show();
            resizeListener.setScene(scene);
        });
    }

    @Override
    public void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {
        Platform.runLater(() -> {
            ((GameMainSceneController) currentSceneController).onTurn(actionCommands, currentPossibleActions);
            stage.setFullScreen(true);
        });
    }

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
            case "EntranceSwap" -> onEntranceSwap((String) evt.getSource(), (RealmType[]) evt.getNewValue(), (RealmType[]) evt.getOldValue());
            //case "EntranceUpdate" -> {} //TODO: verify if has to be handled (i think no)
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

    private void onNewTurn(String newActivePlayer) {
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).notifyNewTurn(newActivePlayer));
    }

    private void onIslandsUnification(List<Integer> islands) {
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).mergeIslands(islands));
    }

    private void onCharacterPriceUpdate(int characterId) {
        Platform.runLater(() -> {
            CharacterImage character = ((GameMainSceneController) currentSceneController).getCharacterImage(characterId);
            character.putCoin();
        });
    }

    private void onCharacterStudentsUpdate(int characterId) {
        Platform.runLater(() -> {
            CharacterImage character = ((GameMainSceneController) currentSceneController).getCharacterImage(characterId);
            character.updateStudents();
        });
    }

    private void onCharacterPlayed(int characterId) {
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).setDropShadowOnCharacter(characterId));
    }

    private void onMotherNatureMovement(int oldIsland, int newIsland) {
        Platform.runLater(() -> {
            this.stage.setFullScreen(true);
            ((GameMainSceneController) currentSceneController).moveMotherNature(oldIsland, newIsland);
        });
    }

    private void onIslandStudentsUpdate(Integer islandId, RealmType[] students, boolean fromEntrance) {
        Platform.runLater(() -> {
            this.stage.setFullScreen(true);
            if (fromEntrance && !modelView.getCurrentActivePlayer().equals(nickname)) {
                String currentPlayer = this.modelView.getCurrentActivePlayer();
                ((GameMainSceneController) currentSceneController).moveStudentsToIsland(currentPlayer, islandId, students);
            } else if (!fromEntrance){
                ((GameMainSceneController) currentSceneController).moveAccordionDown();
                IslandMap islandMap = ((GameMainSceneController) currentSceneController).getIslandMap();
                islandMap.getIslandById(islandId).ifPresent(island -> Arrays.stream(students).forEach(island::addStudent));
            }
        });
    }

    private void onDiningRoomUpdate(String player, RealmType[] students, boolean fromEntrance, boolean isInsertion) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            this.stage.setFullScreen(true);
            if (isInsertion) {
                if (fromEntrance && !player.equals(nickname)) {
                    gameMainSceneController.moveStudentsToDiningRoom(player, students);
                } else if (!fromEntrance){
                    //TODO: else is from character 11
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

    private void onProfessorUpdate(RealmType professor, String lastOwner, String newOwner) {
        Platform.runLater(() -> {
            this.stage.setFullScreen(true);
            ((GameMainSceneController) currentSceneController).insertProfessorAnimation(newOwner, professor);
            if (lastOwner != null) ((GameMainSceneController) currentSceneController).getSchoolBox(lastOwner).removeProfessor(professor);
        });
    }

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

    private void onTowerUpdate(TowerType lastTower, int islandId) {
        TowerType newTower = modelView.getField().getIsland(islandId).getThird();
        Platform.runLater(() -> ((GameMainSceneController) currentSceneController).moveTowers(lastTower, newTower, islandId));
    }

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

    private void onEntranceSwap(String playerName, RealmType[] inserted, RealmType[] removed) {
        Platform.runLater(() -> {
            SchoolBox schoolBox = ((GameMainSceneController) currentSceneController).getSchoolBox(playerName);
            schoolBox.updateEntrance();
            ((GameMainSceneController) currentSceneController).viewSchoolOf(playerName);
            /*Arrays.stream(removed).forEach(student -> schoolBox.removeFromEntrance(student, playerName.equals(nickname)));
            Arrays.stream(inserted).forEach(schoolBox::insertStudentEntrance);*/
        });
    }

    private void onSchoolSwap(String playerName, RealmType[] toEntrance, RealmType[] toDiningRoom) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            SchoolBox schoolBox = gameMainSceneController.getSchoolBox(playerName);
            gameMainSceneController.viewSchoolOf(playerName);
            Arrays.stream(toEntrance).forEach(student -> {
                schoolBox.insertStudentEntrance(student);
                schoolBox.removeFromDiningRoom(student);
            });
            Arrays.stream(toDiningRoom).forEach(student -> {
                schoolBox.insertInDiningRoom(student);
                schoolBox.removeFromEntrance(student, playerName.equals(nickname));
            });
        });
    }

    private void onCoinsUpdate(String playerName) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.viewSchoolOf(playerName);
            gameMainSceneController.getSchoolBox(playerName).updateCoins();
        });
    }

    private void onCloudsSelection(String playerName, int cloudId, RealmType[] students) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.moveStudentsFromCloud(playerName, cloudId, students);
        });
    }

    private void onCloudRefill() {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.moveStudentsToRefillClouds();
        });
    }

    private void onEndGameEvent(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            GameMainSceneController gameMainSceneController = ((GameMainSceneController) currentSceneController);
            gameMainSceneController.onEndGameEvent(evt);
        });
    }

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

    private void checkEventFromControllers(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Quit")) System.exit(0); //TODO: do better
        else if (evt.getPropertyName().equals("QuitGame") || evt.getPropertyName().equals("DeleteSavedGame")) {
            this.returnToMainMenu();
        }
        responseHandlerExecutor.submit(() -> listeners.firePropertyChange(evt));
        if (evt.getPropertyName().equals("Nickname")) this.nickname = (String) evt.getNewValue();
    }

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
        //TODO: find a way to shutdown connection to server
        stage.setOnCloseRequest(event -> System.exit(0));
    }

    @Override
    public void onError(ErrorMessageType error, String info) {
        Platform.runLater(() -> currentSceneController.onError(error, info));
    }

    public void onGameDeleted(String playerName) {
        Platform.runLater(() -> {
            SetupChoiceSceneController setupChoiceSceneController = (SetupChoiceSceneController) currentSceneController;
            setupChoiceSceneController.onGameDeleted("Oh no! " + playerName +
                    " has decided to delete the game. The game will be destroyed.\n" +
                    "What do you want to do?");
        });
    }

    public void returnToMainMenu() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/welcomeToEriantys.fxml"));
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            //TODO
        }
        this.stage.setScene(scene);
        this.stage.setFullScreen(true);
        WelcomeController welcomeController = fxmlLoader.getController();
        welcomeController.addGUI(this);
        welcomeController.addListener(this);
        welcomeController.showMainMenu(nickname);
        currentSceneController = welcomeController;
        this.stage.show();
        resizeListener.setScene(scene);
    }
}
