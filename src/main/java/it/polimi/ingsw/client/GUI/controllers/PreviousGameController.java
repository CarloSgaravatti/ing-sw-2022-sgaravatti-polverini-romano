package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.utils.JsonUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class PreviousGameController extends FXMLController {
    @FXML private Text gameInfo;

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

    @FXML
    void onResumeClick(ActionEvent event) {
        firePropertyChange("RestoreGame", null, null);
    }

    @FXML
    void onDeleteClick(ActionEvent event) {
        firePropertyChange("DeleteSavedGame", null, null);
    }

    @Override
    public void onError(ErrorMessageType error, String errorInfo) {}
}
