package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.GUI.controllers.FXMLController;
import it.polimi.ingsw.client.GUI.controllers.GlobalLobbyController;
import it.polimi.ingsw.client.GUI.controllers.MainSceneController;
import it.polimi.ingsw.client.GUI.controllers.WelcomeController;
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
    private Thread connectionHandlerThread;

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

    }

    @Override
    public void askWizardChoice(WizardType[] freeWizards) {

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
        FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/fxml/mainScene.fxml"));
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            //TODO
        }
        stage.setScene(scene);
        currentSceneController = loader.getController();
        currentSceneController.addListener(this);
        ((MainSceneController) currentSceneController).initializeBoard(modelView);
        stage.show();
    }

    @Override
    public void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "IslandStudents" -> {}
            case "IslandTower" -> {}
            //...
            //events that come from gui controllers
            default -> checkEventFromControllers(evt);
            //default -> listeners.firePropertyChange(evt);
        }
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
        connectionHandlerThread = new Thread(connectionToServer);
        connectionHandlerThread.start();
        //TODO: find a way to shutdown connection to server
        stage.setOnCloseRequest(event -> connectionToServer.setActive(false));
    }

    public void onError(ErrorMessageType error) {
        Platform.runLater(() -> currentSceneController.onError(error));
    }
}
