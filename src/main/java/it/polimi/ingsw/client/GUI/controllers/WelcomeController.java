package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.messages.ErrorMessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class WelcomeController extends FXMLController {
    private GUI gui;
    private boolean serverParametersOk = false;
    @FXML private TextField serverPort;
    @FXML private TextField serverIp;
    @FXML private TextField username;

    @FXML
    void onSubmit(ActionEvent event) {
        if (serverParametersOk) {
            getOnlyNickname();
            return;
        }
        String nickname = username.getCharacters().toString();
        String serverIP = serverIp.getCharacters().toString();
        int serverPort;
        try {
            serverPort = Integer.parseInt(this.serverPort.getCharacters().toString());
        } catch (NumberFormatException e) {
            displayAlert("Server port must be a number");
            return;
        } catch (NullPointerException e) {
            displayAlert("Some parameters are missing");
            return;
        }
        if (nickname.isBlank() || serverIP.isBlank()) {
            displayAlert("Some parameters are missing");
            return;
        }
        gui.doSetup(serverIP, serverPort, nickname);
    }

    private void getOnlyNickname() {
        String nickname = username.getCharacters().toString();
        if (nickname.isBlank()) displayAlert("You must enter a nickname");
        else firePropertyChange("Nickname", null, nickname);
    }

    public void addGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void onError(ErrorMessageType error) {
        if (error == ErrorMessageType.DUPLICATE_NICKNAME) {
            serverParametersOk = true;
            displayAlert("Nickname wasn't correct, retry");
        }
    }
}
