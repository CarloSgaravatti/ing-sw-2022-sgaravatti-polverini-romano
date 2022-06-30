package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.messages.ErrorMessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * PreviousGameController display a dialog scene that notifies the user that he was participating in a game before the
 * server stopped. The user is asked to make a choice: resume the game or delete it.
 *
 * @see it.polimi.ingsw.client.GUI.controllers.FXMLController
 */
public class PreviousGameController extends FXMLController {
    @FXML private Text gameInfo;

    /**
     * Display the dialog
     *
     * @param numPlayers the number of players of the game to resume
     * @param rules the type of rules of the game to resume
     * @param participants the participants of the game to resume
     */
    public void init(int numPlayers, boolean rules, String[] participants) {
        StringBuilder participantsString = new StringBuilder();
        for (int i = 0; i < participants.length - 1; i++) {
            participantsString.append(participants[i]).append(" ");
        }
        participantsString.append(participants[participants.length - 1]);
        gameInfo.setText("Number of players: " + numPlayers + "\n" +
                "Type of rules: " + ((rules) ? "expert" : "simple") + "\n" +
                "Participants: " + participantsString);
    }

    /**
     * Responds to a click on the resume button by firing an event to the gui main class
     *
     * @param event the action event fired when the button is pressed
     */
    @FXML
    void onResumeClick(ActionEvent event) {
        firePropertyChange("RestoreGame", null, null);
    }

    /**
     * Responds to a click on the delete button by firing an event to the gui main class
     *
     * @param event the action event fired when the button is pressed
     */
    @FXML
    void onDeleteClick(ActionEvent event) {
        firePropertyChange("DeleteSavedGame", null, null);
    }

    @Override
    public void onError(ErrorMessageType error, String errorInfo) {}
}
