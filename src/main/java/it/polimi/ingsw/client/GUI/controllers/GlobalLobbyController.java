package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.client.GUI.items.LobbyInfo;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.utils.Triplet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * GlobalLobbyController controls the scene that contains the global lobby table
 *
 * @see it.polimi.ingsw.client.GUI.controllers.FXMLController
 * @see javafx.fxml.Initializable
 */
public class GlobalLobbyController extends FXMLController implements Initializable {
    @FXML private Text messageLobby;
    @FXML private TableView<LobbyInfo> globalLobbyTable;
    @FXML private TableColumn<LobbyInfo, String> rulesCol;
    @FXML private TableColumn<LobbyInfo, Integer> numPlayersCol;
    @FXML private TableColumn<LobbyInfo, Integer> gameIdCol;
    @FXML private TableColumn<LobbyInfo, Integer> playersConnectedCol;
    private GUI gui;

    /**
     * Constructs the global lobby table by inserting information of all games
     *
     * @param lobbyInfo all the information for the not started games
     */
    public void constructTable(Map<Integer, Triplet<Integer, Boolean, String[]>> lobbyInfo) {
        List<LobbyInfo> lobby = new ArrayList<>();
        for (Integer i: lobbyInfo.keySet()) {
            lobby.add(new LobbyInfo(i, lobbyInfo.get(i).getFirst(), lobbyInfo.get(i).getThird().length, lobbyInfo.get(i).getSecond()));
        }
        globalLobbyTable.getItems().addAll(lobby);
    }

    /**
     * Initialize the scene
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numPlayersCol.setCellValueFactory(new PropertyValueFactory<>("numPlayers"));
        rulesCol.setCellValueFactory(new PropertyValueFactory<>("rules"));
        playersConnectedCol.setCellValueFactory(new PropertyValueFactory<>("connectedPlayers"));
    }

    /**
     * Set the nickname that will be used to display a personal message
     *
     * @param nickname the nickname of the user
     */
    public void setNickname(String nickname) {
        messageLobby.setText("Hi " + nickname + ", you are viewing the global lobby");
    }

    /**
     * Set the gui that will be notified if the scene have to be switched
     *
     * @param gui the gui of the user
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Responds to an event that occurs when the button that permit to participate in a selected game is pressed
     *
     * @param event event fired when the button is pressed
     */
    @FXML
    void participateGame(ActionEvent event) {
        LobbyInfo game = globalLobbyTable.getSelectionModel().getSelectedItem();
        if (game != null) {
            System.out.println("Selected game " + game.getId());
            firePropertyChange(new PropertyChangeEvent(this, "GameToPlay", null, game.getId()));
        } else {
            displayAlert("You have to select a table row to play the corresponding game");
        }
    }

    /**
     * Responds to an event that occurs when the button that permit to refresh the lobby is pressed
     *
     * @param event event fired when the button is pressed
     */
    @FXML
    void onRefresh(ActionEvent event) {
        firePropertyChange("RefreshLobby", null, null);
    }

    /**
     * Responds to an event that occurs when the button that permit to return to the main menu is pressed
     *
     * @param event event fired when the button is pressed
     */
    @FXML
    void onReturnToMainMenu(ActionEvent event) {
        gui.returnToMainMenu();
    }

    /**
     * Display an alert message that contains a string that depends on the particular error
     *
     * @param error the type of error
     * @param errorInfo the description of the error
     */
    @Override
    public void onError(ErrorMessageType error, String errorInfo) {
        if (error == ErrorMessageType.INVALID_REQUEST_GAME_ALREADY_STARTED) {
            displayAlert("The selected game was already started, retry");
        } else if (error == ErrorMessageType.INVALID_REQUEST_GAME_NOT_FOUND) {
            displayAlert("There isn't such a game, retry");
        }
    }
}
