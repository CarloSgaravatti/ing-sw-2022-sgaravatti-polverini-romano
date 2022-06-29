package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.commons.codec.language.bm.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetupChoiceSceneController extends FXMLController {
    @FXML private HBox hBox;
    @FXML private Label upperText;
    @FXML private Button confirmChoice;
    @FXML private Label bottomText;
    private List<ImageView> images;
    private String lastSelection;
    private final EventHandler<MouseEvent> sendTowerChoiceHandler = mouseEvent -> {
        firePropertyChange("TowerChoice", null, TowerType.valueOf(lastSelection));
        waitForOtherPlayersChoices();
    };
    private final EventHandler<MouseEvent> sendWizardChoiceHandler = mouseEvent -> {
        firePropertyChange("WizardChoice", null, WizardType.valueOf(lastSelection));
        waitForOtherPlayersChoices();
    };

    @Override
    public void onError(ErrorMessageType error, String errorInfo) {

    }

    public void showGameLobby(int numPlayers, boolean rules, String[] participants) {
        upperText.setText("");
        bottomText.setText("");
        confirmChoice.setVisible(false);
        StringBuilder participantsString = new StringBuilder();
        for (int i = 0; i < participants.length - 1; i++) {
            participantsString.append(participants[i]).append(" ");
        }
        participantsString.append(participants[participants.length - 1]);
        Text text = new Text();
        text.setWrappingWidth(hBox.getWidth() - hBox.getPadding().getLeft() - hBox.getPadding().getRight());
        text.setText("You have entered the game lobby. Wait for other players.\n" +
                "The game is a " + numPlayers + " players game.\n" +
                "The rules are " + ((rules) ? "expert" : "simple") + ".\n" +
                "Currently there are " + participants.length + " players connected.\n" +
                "Their names are: " + participantsString);
        text.getStyleClass().add("center-text");
        hBox.getChildren().clear();
        hBox.getChildren().add(text);
    }

    public void onPlayerJoined(String playerName) {
        Text text = (Text) hBox.getChildren().get(0);
        String textString = text.getText();
        text.setText(textString + "\n" +
                playerName + " has joined the game!");
    }

    public void onGameStarted() {
        Text text = (Text) hBox.getChildren().get(0);
        String textString = text.getText();
        text.setText(textString + "\n" +
                "The game will start soon!");
    }

    public void setSceneWithTowers(TowerType[] towersFree) {
        double maxWidth = (hBox.getWidth() - hBox.getSpacing() * towersFree.length) / towersFree.length;
        upperText.setText("You have to choose a tower");
        bottomText.setText("");
        images = new ArrayList<>();
        for (TowerType tower: towersFree) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.towerImages.get(tower))));
            images.add(createImageView(image, tower.toString(), sendTowerChoiceHandler, sendWizardChoiceHandler, maxWidth));
        }
        addImagesToScene();
        confirmChoice.setVisible(false);
    }

    public void setSceneWithWizards(WizardType[] wizardsFree) {
        double maxWidth = (hBox.getWidth() - hBox.getSpacing() * wizardsFree.length) / wizardsFree.length;
        upperText.setText("You have to choose a wizard");
        bottomText.setText("");
        images = new ArrayList<>();
        for (WizardType wizard: wizardsFree) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.wizardImages.get(wizard))));
            images.add(createImageView(image, wizard.toString(), sendWizardChoiceHandler, sendTowerChoiceHandler, maxWidth));
        }
        addImagesToScene();
        confirmChoice.setVisible(false);
    }

    private ImageView createImageView(Image image, String id, EventHandler<MouseEvent> handler,
                                      EventHandler<MouseEvent> handlerToRemove, double maxWidth) {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        double ratio = image.getHeight() / image.getWidth();
        imageView.setId(id);
        imageView.getStyleClass().addAll("clickableImage");
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            confirmChoice.setVisible(true);
            ImageView imageClicked = (ImageView) mouseEvent.getTarget();
            lastSelection = imageClicked.getId();
            bottomText.setText("You have chosen " + lastSelection);
            confirmChoice.removeEventHandler(MouseEvent.MOUSE_CLICKED, handlerToRemove);
            confirmChoice.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
        });
        if (maxWidth * ratio > hBox.getHeight()) {
            imageView.setFitHeight(hBox.getHeight());
        } else {
            imageView.setFitWidth(maxWidth);
        }
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void addImagesToScene() {
        hBox.getChildren().clear();
        hBox.getChildren().addAll(images);
    }

    private void waitForOtherPlayersChoices() {
        upperText.setText("");
        bottomText.setText("");
        confirmChoice.setVisible(false);
        Text text = new Text();
        text.setWrappingWidth(hBox.getWidth() - hBox.getPadding().getLeft() - hBox.getPadding().getRight());
        text.setText("Wait for other players choices ...");
        text.getStyleClass().add("center-text");
        hBox.getChildren().clear();
        hBox.getChildren().add(text);
    }

    public void onGameDeleted(String playerName) {
        upperText.setText("");
        bottomText.setText("");
        confirmChoice.setVisible(false);
        VBox vBox = new VBox();
        vBox.setPrefHeight(hBox.getHeight() - hBox.getPadding().getTop() - hBox.getPadding().getBottom());
        vBox.setPrefWidth(hBox.getWidth() - hBox.getPadding().getLeft() - hBox.getPadding().getRight());
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        Text text = new Text();
        text.setText("Oh no! " + playerName + " has decided to delete the game. The game will be destroyed.\n" +
                "What do you want to do?");
        text.getStyleClass().add("center-text");
        text.setWrappingWidth(vBox.getWidth());
        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(20);
        buttonsBox.setAlignment(Pos.CENTER);
        Button quitButton = new Button("Quit");
        quitButton.getStyleClass().add("setup-button");
        quitButton.getStyleClass().add("quit-button");
        quitButton.setOnAction(actionEvent -> System.exit(0));
        Button globalLobbyButton = new Button("Enter Global Lobby");
        globalLobbyButton.getStyleClass().add("setup-button");
        globalLobbyButton.getStyleClass().add("global-lobby-button");
        globalLobbyButton.setOnAction(actionEvent -> firePropertyChange("RefreshLobby", null, null));
        buttonsBox.getChildren().addAll(globalLobbyButton, quitButton);
        vBox.getChildren().addAll(text, buttonsBox);
        hBox.getChildren().clear();
        hBox.getChildren().add(vBox);
    }
}
