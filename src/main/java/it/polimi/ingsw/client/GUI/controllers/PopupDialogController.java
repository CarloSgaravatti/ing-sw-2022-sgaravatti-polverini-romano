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

/**
 * PopupDialogController is a dialog popup that ca be displayed on top of the current scene and does not permit to click
 * anything that isn't on the popup. The popup has two buttons, one permit to return to the main menu and one permit to
 * quit the application. The popup can be displayed only in the main scene.
 */
public class PopupDialogController extends HBox implements Initializable {
    @FXML private Text text;
    @FXML private Button mainMenuButton;
    @FXML private Button closeButton;
    @FXML private Button quitButton;
    private GameMainSceneController gameMainSceneController;

    /**
     * Constructs a new PopupDialogController that will be the root of the sub scene and also the controller of the sub scene
     * that contains the popup dialog.
     */
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

    /**
     * Insert the specified text in the popup and binds the game main scene controller to this object
     *
     * @param content the content of the popup
     * @param gameMainSceneController the controller of the main scene
     */
    public void show(String content, GameMainSceneController gameMainSceneController) {
        this.gameMainSceneController = gameMainSceneController;
        text.setText(content);
    }

    /**
     * Initialize the sub scene
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainMenuButton.setOnAction(actionEvent -> gameMainSceneController.returnToMainMenu(actionEvent));
        closeButton.setOnAction(actionEvent -> gameMainSceneController.quitApplication(actionEvent));
        quitButton.setOnAction(actionEvent -> gameMainSceneController.quitApplication(actionEvent));
    }
}
