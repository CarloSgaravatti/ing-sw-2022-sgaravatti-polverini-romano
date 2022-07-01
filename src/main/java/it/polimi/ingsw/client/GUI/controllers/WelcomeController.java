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

/**
 * WelcomeController handles the starting scene of the gui, the main menu and the game creation form
 */
public class WelcomeController extends FXMLController {
    private GUI gui;
    private boolean serverParametersOk = false;
    @FXML private TextField serverPort;
    @FXML private TextField serverIp;
    @FXML private TextField username;
    @FXML private Pane dialogRoot;
    private boolean isShowingMainMenu = true;

    /**
     * Returns true if the controller is showing the main menu, otherwise false
     *
     * @return true if the controller is showing the main menu, otherwise false
     */
    public boolean isShowingMainMenu() {
        return isShowingMainMenu;
    }

    /**
     * Responds to the submit button present in the starting scene. The method will control that every text field (for the
     * username, the server ip and the server port) is correctly compiled, if so notifies the gui class otherwise it
     * displays an alert. If the server port and ip are already processed but the nickname no (for example if the
     * nickname was duplicated on the server and so the user have to choose another one), the method will control only
     * the nickname text field
     *
     * @param event the action event fired after the submit button press
     */
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

    /**
     * Controls the nickname present on the nickname text field, if a nickname is present it fires an event to the gui
     */
    private void getOnlyNickname() {
        String nickname = username.getCharacters().toString();
        if (nickname.isBlank()) displayAlert("You must enter a nickname");
        else firePropertyChange("Nickname", null, nickname);
    }

    /**
     * Associate the specified gui instance to this controller
     *
     * @param gui the instance of the gui
     */
    public void addGUI(GUI gui) {
        this.gui = gui;
    }

    /**
     * Display an alert that contains a different message in base of the specified error and error info
     *
     * @param error the type of error
     * @param errorInfo the description of the error
     */
    @Override
    public void onError(ErrorMessageType error, String errorInfo) {
        if (error == ErrorMessageType.DUPLICATE_NICKNAME) {
            serverParametersOk = true;
            displayAlert("Nickname wasn't correct, retry");
        } else displayAlert(errorInfo);
    }

    /**
     * Shows the main menu of the game, where the user can choose if he wants to create a new game, or to search a game
     * in the global lobby or to quit the application
     *
     * @param nickname the nickname of the user, used to print a personal message
     */
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
        quitButton.setOnAction(actionEvent -> System.exit(0));
        vBox.getChildren().addAll(text, newGameButton, globalLobbyButton, quitButton);
        dialogRoot.getChildren().clear();
        dialogRoot.getChildren().add(vBox);
    }

    /**
     * Shows the form for creating a new game. The form contains the number of players choice box, where the user can
     * select the number of players of the game, and the rules choice box, where the user can select the type of rules.
     * A button to return to the main menu is also present.
     *
     * @param nickname the nickname of the user, used to print a personal message
     */
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

    /**
     * Returns a newly created VBox used as the container for the main menu or for the create game form
     *
     * @return a newly created VBox used as the container for the main menu or for the create game form
     */
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

    /**
     * Returns a Text that have the specified string and the specified wrapping width. The text will have a center text
     * alignment.
     *
     * @param textString the content of the text
     * @param width the wrapping width of the text
     * @return a Text that have the specified string and the specified wrapping width
     */
    private Text getTitleText(String textString, double width) {
        Text text = new Text();
        text.setTextAlignment(TextAlignment.CENTER);
        text.getStyleClass().add("welcome-text");
        text.setWrappingWidth(width);
        text.setText(textString);
        return text;
    }

    /**
     * Returns the container HBox for all items of the create game form. The hBox will wrap the number of players choice box and
     * label or the rules choice box and label or the bottom bar with the buttons
     *
     * @param width the width of the hBox
     * @return the container HBox for all items of the create game form
     */
    private HBox getContainerHBoxForCreateGame(double width) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(30);
        hbox.setPrefHeight(70);
        hbox.setPrefWidth(width);
        return hbox;
    }
}
