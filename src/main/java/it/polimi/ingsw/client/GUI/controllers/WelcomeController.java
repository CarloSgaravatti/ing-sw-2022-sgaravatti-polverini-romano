package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.messages.ErrorMessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.beans.PropertyChangeEvent;

public class WelcomeController extends FXMLController {
    private GUI gui;
    private boolean serverParametersOk = false;
    @FXML private TextField serverPort;
    @FXML private TextField serverIp;
    @FXML private TextField username;
    @FXML private Pane dialogRoot;
    private boolean isShowingMainMenu = true;

    public boolean isShowingMainMenu() {
        return isShowingMainMenu;
    }

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
    public void onError(ErrorMessageType error, String errorInfo) {
        if (error == ErrorMessageType.DUPLICATE_NICKNAME) {
            serverParametersOk = true;
            displayAlert("Nickname wasn't correct, retry");
        } else displayAlert(errorInfo);
    }

    public void showMainMenu(String nickname) {
        isShowingMainMenu = true;
        VBox vBox = getContainerVbox();
        Text text = getTitleText("Hi " + nickname, vBox.getWidth());
        Button globalLobbyButton = new Button("Enter global lobby");
        globalLobbyButton.getStyleClass().add("main-menu-button");
        globalLobbyButton.setPrefWidth(300);
        globalLobbyButton.setOnAction(actionEvent -> {
            isShowingMainMenu = false;
            firePropertyChange("RefreshLobby", null, null);
        });
        Button newGameButton = new Button("Create new game");
        newGameButton.getStyleClass().add("main-menu-button");
        newGameButton.setPrefWidth(300);
        newGameButton.setOnAction(actionEvent -> showNewGameForm(nickname));
        Button quitButton = new Button("Quit");
        quitButton.getStyleClass().add("main-menu-button");
        quitButton.setPrefWidth(300);
        quitButton.setOnAction(actionEvent -> System.exit(0)); //TODO: do better
        vBox.getChildren().addAll(text, newGameButton, globalLobbyButton, quitButton);
        dialogRoot.getChildren().clear();
        dialogRoot.getChildren().add(vBox);
    }

    private void showNewGameForm(String nickname) {
        VBox vBox = getContainerVbox();
        Text text = getTitleText(nickname + ", create a new game", vBox.getWidth());

        HBox numPlayersBox = getContainerHBoxForCreateGame(vBox.getWidth());
        Label numberOfPlayers = new Label("Number of players");
        numberOfPlayers.getStyleClass().add("create-game-label");
        ChoiceBox<Integer> numPlayersChoiceBox = new ChoiceBox<>();
        numPlayersChoiceBox.getItems().addAll(2, 3);
        numPlayersChoiceBox.setPrefHeight(40);
        numPlayersChoiceBox.setPrefWidth(200);
        numPlayersBox.getChildren().addAll(numberOfPlayers, numPlayersChoiceBox);

        HBox rulesBox = getContainerHBoxForCreateGame(vBox.getWidth());
        Label rulesLabel = new Label("  Type of rules  ");
        rulesLabel.getStyleClass().add("create-game-label");
        ChoiceBox<String> rulesChoiceBox = new ChoiceBox<>();
        rulesChoiceBox.getItems().addAll("simple", "expert");
        rulesChoiceBox.setPrefHeight(40);
        rulesChoiceBox.setPrefWidth(200);
        rulesBox.getChildren().addAll(rulesLabel, rulesChoiceBox);

        HBox buttonsBox = getContainerHBoxForCreateGame(vBox.getWidth());
        buttonsBox.setSpacing(30);
        Button backButton = new Button("Back");
        backButton.getStyleClass().add("main-menu-button");
        backButton.setOnAction(actionEvent -> showMainMenu(nickname));
        Button newGameButton = new Button("Create");
        newGameButton.getStyleClass().add("main-menu-button");
        newGameButton.setOnAction(actionEvent -> {
            Integer numPlayers = numPlayersChoiceBox.getSelectionModel().getSelectedItem();
            String rules = rulesChoiceBox.getSelectionModel().getSelectedItem();
            if (numPlayers == null || rules == null) {
                displayAlert("Some parameters are missing, retry");
            } else {
                firePropertyChange(new PropertyChangeEvent(this, "NewGame", numPlayers, rules.equals("expert")));
            }
        });
        buttonsBox.getChildren().addAll(backButton, newGameButton);

        vBox.getChildren().addAll(text, numPlayersBox, rulesBox, buttonsBox);
        dialogRoot.getChildren().clear();
        dialogRoot.getChildren().add(vBox);
    }

    private VBox getContainerVbox() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(0);
        vBox.setLayoutY(0);
        vBox.setPrefWidth(dialogRoot.getWidth());
        vBox.setPrefHeight(dialogRoot.getHeight());
        vBox.setSpacing(40);
        return vBox;
    }

    private Text getTitleText(String textString, double width) {
        Text text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.getStyleClass().add("welcome-text");
        text.setWrappingWidth(width);
        text.setText(textString);
        return text;
    }

    private HBox getContainerHBoxForCreateGame(double width) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(30);
        hbox.setPrefHeight(70);
        hbox.setPrefWidth(width);
        return hbox;
    }
}
