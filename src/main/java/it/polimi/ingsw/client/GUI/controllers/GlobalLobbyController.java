package it.polimi.ingsw.client.GUI.controllers;

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

public class GlobalLobbyController extends FXMLController implements Initializable {
    @FXML private Text messageLobby;
    @FXML private TableView<LobbyInfo> globalLobbyTable;
    @FXML private TableColumn<LobbyInfo, String> rulesCol;
    @FXML private TableColumn<LobbyInfo, Integer> numPlayersCol;
    @FXML private TableColumn<LobbyInfo, Integer> gameIdCol;
    @FXML public TableColumn<LobbyInfo, Integer> playersConnectedCol;
    @FXML private ChoiceBox<String> rulesChoice;
    @FXML private ChoiceBox<Integer> numPlayersChoice;

    public void constructTable(Map<Integer, Triplet<Integer, Boolean, String[]>> lobbyInfo) {
        List<LobbyInfo> lobby = new ArrayList<>();
        for (Integer i: lobbyInfo.keySet()) {
            lobby.add(new LobbyInfo(i, lobbyInfo.get(i).getFirst(), lobbyInfo.get(i).getThird().length, lobbyInfo.get(i).getSecond()));
        }
        globalLobbyTable.getItems().addAll(lobby);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numPlayersCol.setCellValueFactory(new PropertyValueFactory<>("numPlayers"));
        rulesCol.setCellValueFactory(new PropertyValueFactory<>("rules"));
        //playersConnectedCol.setCellValueFactory(new PropertyValueFactory<>("connectedPlayers"));
        numPlayersChoice.getItems().addAll(2, 3);
        rulesChoice.getItems().addAll("simple", "expert");
        //globalLobbyTable.setRowFactory();
    }

    public void setNickname(String nickname) {
        messageLobby.setText("Hi " + nickname + ", you are viewing the global lobby");
    }

    @FXML
    void participateGame(ActionEvent event) {
        LobbyInfo game = globalLobbyTable.getSelectionModel().getSelectedItem();
        System.out.println("Selected game " + game.getId());
        firePropertyChange(new PropertyChangeEvent(this, "GameToPlay", null, game.getId()));
    }

    @FXML
    void submitNewGame(ActionEvent event) {
        Integer numPlayers = numPlayersChoice.getSelectionModel().getSelectedItem();
        String rules = rulesChoice.getSelectionModel().getSelectedItem();
        if (numPlayers == null || rules == null) {
            displayAlert("Some parameters are missing, retry");
        } else {
            firePropertyChange(new PropertyChangeEvent(this, "NewGame", numPlayers, rules.equals("expert")));
        }
    }


    @FXML
    void onRefresh(ActionEvent event) {
        firePropertyChange("RefreshLobby", null, null);
    }

    @Override
    public void onError(ErrorMessageType error, String errorInfo) {
        if (error == ErrorMessageType.INVALID_REQUEST_GAME_ALREADY_STARTED) {
            displayAlert("The selected game was already started, retry");
        } else if (error == ErrorMessageType.INVALID_REQUEST_GAME_NOT_FOUND) {
            displayAlert("There isn't such a game, retry");
        }
    }
}
