package it.polimi.ingsw.client.GUI.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PopupDialogController extends HBox implements Initializable {
    @FXML private Text text;
    @FXML private Button mainMenuButton;
    @FXML private Button closeButton;
    @FXML private Button quitButton;
    private GameMainSceneController gameMainSceneController;

    public PopupDialogController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/popupDialog.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show(String content, GameMainSceneController gameMainSceneController) {
        this.gameMainSceneController = gameMainSceneController;
        text.setText(content);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainMenuButton.setOnAction(actionEvent -> gameMainSceneController.returnToMainMenu(actionEvent));
        closeButton.setOnAction(actionEvent -> gameMainSceneController.quitApplication(actionEvent));
        quitButton.setOnAction(actionEvent -> gameMainSceneController.quitApplication(actionEvent));
    }
}
