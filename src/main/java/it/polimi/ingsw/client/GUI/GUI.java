package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.GUI.controllers.*;
import it.polimi.ingsw.client.GUI.items.AssistantsTab;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.messages.ErrorMessageType;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUI extends Application implements UserInterface {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private String nickname;
    private boolean nicknameSent = false;
    private Stage stage;
    private Scene scene;
    private FXMLController currentSceneController;
    //This executor is used to not use the javafx thread to compute actions (so javafx thread is only
    //used for javafx stuffs)
    private final ExecutorService responseHandlerExecutor = Executors.newSingleThreadExecutor();

    public GUI() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/welcomeToEriantys.fxml"));
        scene = new Scene(fxmlLoader.load()); //width and height (is still resizable)
        //with stage.setResizable(false) the stage is not resizable
        //stage.setFullScreen(true) set the stage to full screen
        this.stage = stage;
        this.stage.setTitle("Eriantys");
        this.stage.setScene(scene);
        this.stage.setFullScreen(true);
        this.stage.show(); //display the stage on the screen
        this.stage.centerOnScreen();

        WelcomeController welcomeController = fxmlLoader.getController();
        welcomeController.addGUI(this);
        welcomeController.addListener(this);
        currentSceneController = welcomeController;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void askNickname() {
        System.out.println(nickname);
        if (nickname != null && !nicknameSent) {
            listeners.firePropertyChange("Nickname", null, nickname);
            nicknameSent = true;
        }
    }

    @Override
    public void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/globalLobby.fxml"));
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
        });
    }

    @Override
    public void displayStringMessage(String message) {

    }

    @Override
    public void askTowerChoice(TowerType[] freeTowers) {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxml/setupScene.fxml"));
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                //TODO
            }
            String css = Objects.requireNonNull(this.getClass().getResource("/css/setupScene.css")).toExternalForm();
            scene.getStylesheets().addAll(css);
            this.stage.setScene(scene);
            SetupChoiceSceneController sceneController = fxmlLoader.getController();
            sceneController.addListener(this);
            sceneController.setSceneWithTowers(freeTowers);
            currentSceneController = sceneController;
            this.stage.show();
        });
    }

    @Override
    public void askWizardChoice(WizardType[] freeWizards) {
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
            sceneController.setSceneWithWizards(freeWizards);
            this.stage.show();
        });
    }

    @Override
    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {

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
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/fxml/mainSceneV2.fxml"));
            try {
                scene = new Scene(loader.load());
            } catch (IOException e) {
                //TODO
            }
            stage.setScene(scene);
            stage.setFullScreen(true);
            currentSceneController = loader.getController();
            ((MainSceneV2Controller) currentSceneController).initializeBoard(modelView, this.nickname);
            currentSceneController.addListener(this);
            stage.show();
        });
    }

    @Override
    public void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {
        Platform.runLater(() -> {
            ((MainSceneV2Controller) currentSceneController).onTurn(currentPossibleActions);
            stage.setFullScreen(true);
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "AssistantUpdate" -> onAssistantUpdate((Integer) evt.getOldValue(), (String) evt.getNewValue());
            case "IslandStudents" -> {}
            case "IslandTower" -> {}
            //...
            //events that come from gui controllers
            default -> checkEventFromControllers(evt);
            //default -> listeners.firePropertyChange(evt);
        }
    }

    private void onAssistantUpdate(int assistant, String player) {
        Platform.runLater(() -> {
            MainSceneV2Controller controller = ((MainSceneV2Controller) currentSceneController);
            if (player.equals(nickname)) {
                AssistantsTab assistantsTab = controller.getAssistantsTab();
                assistantsTab.removeAssistantFromDeck(assistant);
            }
            controller.setAssistantImage(player, assistant);
        });
    }

    private void checkEventFromControllers(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Nickname")) this.nickname = (String) evt.getNewValue();
        responseHandlerExecutor.submit(() -> listeners.firePropertyChange(evt));
    }

    public void doSetup(String serverIp, int serverPort, String nickname) {
        this.nickname = nickname;
        System.out.println(nickname);
        Socket socket;
        try {
            socket = new Socket(serverIp, serverPort);
        } catch (IOException e) {
            System.err.println("Error in connection with server");
            currentSceneController.onError(null);
            return; //TODO
        }
        System.out.println("Connection Established");
        ConnectionToServer connectionToServer = new ConnectionToServer(socket, this);
        Thread connectionHandlerThread = new Thread(connectionToServer);
        connectionHandlerThread.start();
        //TODO: find a way to shutdown connection to server
        stage.setOnCloseRequest(event -> connectionToServer.setActive(false));
    }

    public void onError(ErrorMessageType error) {
        Platform.runLater(() -> currentSceneController.onError(error));
    }
}
