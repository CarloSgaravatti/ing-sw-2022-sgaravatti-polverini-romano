package it.polimi.ingsw.client.GUI.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * ErrorPopupController is an error popup that contains an error info and can be displayed when the user commit an error.
 * This type of popup can be displayed only in the main scene.
 */
public class ErrorPopupController extends HBox {
    @FXML private Button closeButton;
    @FXML private Button quitButton;
    @FXML private Text text;

    /**
     * Constructs a new ErrorPopupController that will be the root of the sub scene and also the controller of the sub scene
     * that contains the error popup.
     */
    public ErrorPopupController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/popup.fxml"));
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
     * @param gameMainSceneController the controller of the main scene
     * @param content the content that will be displayed on the popup
     */
    public void show(GameMainSceneController gameMainSceneController, String content) {
        closeButton.setOnAction(actionEvent -> gameMainSceneController.closeErrorPopup(this));
        quitButton.setOnAction(actionEvent -> gameMainSceneController.closeErrorPopup(this));
        text.setText(content);
    }
}
