package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.client.GUI.items.CloudImage;
import it.polimi.ingsw.client.GUI.items.IslandImage;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.utils.Pair;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Translate;

import java.util.*;

public class MainSceneController extends FXMLController {
    private ModelView modelView;
    private Pair<String, PlayerView> client;
    private Pair<String, PlayerView> secondPlayer;
    private Pair<String, PlayerView> thirdPlayer;
    private final Map<Integer, Pair<Integer, Integer>> islandsPositionInGrid = new HashMap<>();
    @FXML
    private ImageView clientSchool;
    @FXML
    private ImageView secondPlayerSchool;
    @FXML
    private ImageView thirdPlayerSchool;
    @FXML
    private ImageView secondPlayerAssistant;
    @FXML
    private ImageView clientAssistant;
    @FXML
    private VBox map;
    @FXML
    private FlowPane islandMap;
    @FXML
    private HBox cloudBox;
    @FXML
    private VBox clientBox;
    @FXML
    private HBox secondPlayerBox;

    public void initializeBoard(ModelView modelView, String clientNickname) {
        this.modelView = modelView;
        /*if (modelView.getPlayers().size() == 3) {
            Image thirdSchool = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/schools/Plancia_DEF2.png")));
            thirdPlayerSchool.setImage(thirdSchool);
            thirdPlayerSchool.setPreserveRatio(true);
        }*/
        Map<String, PlayerView> playersView = modelView.getPlayers();
        List<String> playersNicknames = new ArrayList<>(playersView.keySet()); //TODO: replace with player clockwise order
        int clientIndex = playersNicknames.indexOf(clientNickname);
        client = new Pair<>(clientNickname, playersView.get(clientNickname));
        clientAssistant.setImage(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream(Constants.wizardImages.get(playersView.get(clientNickname).getPlayerWizard())))));
        String secondPlayerNickname = playersNicknames.get((clientIndex + ((playersNicknames.size() == 2) ? 1 : 2))
                % playersNicknames.size());
        secondPlayer = new Pair<>(secondPlayerNickname, playersView.get(secondPlayerNickname));
        secondPlayerAssistant.setImage(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream(Constants.wizardImages.get(playersView.get(secondPlayerNickname).getPlayerWizard())))));
        if (playersNicknames.size() == 3) {
            String thirdPlayerNickname = playersNicknames.get((clientIndex + 1 ) % playersNicknames.size());
            thirdPlayer = new Pair<>(thirdPlayerNickname, playersView.get(thirdPlayerNickname));
        }
        initializeMap();
        initializeAccordions();
    }

    private void initializeMap() {
        /*double islandImageWidth = 2 * islandMap.getWidth()  / modelView.getField().getIslandSize();
        for (int i = 0; i < modelView.getField().getIslandSize(); i++) {
            islandMap.getChildren().add(new IslandImage(i, modelView.getField(), islandImageWidth));
        }
        double cloudImageWidth = 2 * islandMap.getWidth() / modelView.getField().getCloudStudents().size();
        for (int i = 0; i < modelView.getField().getCloudStudents().size(); i++) {
            cloudBox.getChildren().add(new CloudImage(i, modelView.getField(), islandImageWidth));
        }*/
    }
    @Override
    public void onError(ErrorMessageType error) {

    }

    public void initializeAccordions() {
        clientBox.setPrefHeight(AnchorPane.USE_COMPUTED_SIZE);
        clientBox.setMaxHeight(AnchorPane.USE_PREF_SIZE);
        secondPlayerBox.setPrefWidth(AnchorPane.USE_COMPUTED_SIZE);
        secondPlayerBox.setMaxWidth(AnchorPane.USE_PREF_SIZE);
        double clientBoxTranslation = ((AnchorPane)clientBox.getChildren().get(1)).getHeight();
        double secondPlayerBoxTranslation = ((AnchorPane)secondPlayerBox.getChildren().get(0)).getWidth();
        System.out.println(clientBoxTranslation);
        System.out.println(secondPlayerBoxTranslation);
        clientBox.setTranslateY(clientBoxTranslation);
        secondPlayerBox.setTranslateX(-secondPlayerBoxTranslation);
    }

    public void viewSchool(ActionEvent actionEvent) {
        double clientBoxTranslation = ((AnchorPane)clientBox.getChildren().get(1)).getHeight();
        Button clientButton = (Button) actionEvent.getTarget();
        //clientBox.getTransforms().clear();
        Translate translation = new Translate();
        translation.setX(0);
        if (clientButton.getText().equals("^")) {
            translation.setY(-clientBoxTranslation);
            //clientBox.setTranslateY(-((AnchorPane)clientBox.getChildren().get(1)).getHeight());
            clientButton.setText("v");
        } else {
            translation.setY(clientBoxTranslation);
            //clientBox.setTranslateY(((AnchorPane)clientBox.getChildren().get(1)).getHeight());
            clientButton.setText("^");
        }
        clientBox.getTransforms().add(translation);
    }

    public void viewSecondPlayerSchool(ActionEvent actionEvent) {
        double secondPlayerBoxTranslation = ((AnchorPane)secondPlayerBox.getChildren().get(0)).getWidth();
        Button secondPlayerButton = (Button) actionEvent.getTarget();
        Translate translation = new Translate();
        translation.setY(0);
        if (secondPlayerButton.getText().equals(">")) {
            translation.setX(secondPlayerBoxTranslation);
            //secondPlayerBox.setTranslateX(((AnchorPane)secondPlayerBox.getChildren().get(0)).getWidth());
            secondPlayerButton.setText("<");
        } else {
            translation.setX(-secondPlayerBoxTranslation);
            //secondPlayerBox.setTranslateX(-((AnchorPane)secondPlayerBox.getChildren().get(0)).getWidth());
            secondPlayerButton.setText(">");
        }
        secondPlayerBox.getTransforms().add(translation);
    }
}
