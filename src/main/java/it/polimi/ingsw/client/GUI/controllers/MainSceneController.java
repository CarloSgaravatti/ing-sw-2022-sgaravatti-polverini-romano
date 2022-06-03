package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.client.GUI.items.CloudImage;
import it.polimi.ingsw.client.GUI.items.IslandImage;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.utils.Pair;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    }

    private void initializeMap() {
        double islandImageWidth = 2 * islandMap.getWidth()  / modelView.getField().getIslandSize();
        for (int i = 0; i < modelView.getField().getIslandSize(); i++) {
            islandMap.getChildren().add(new IslandImage(i, modelView.getField(), islandImageWidth));
        }
        double cloudImageWidth = 2 * islandMap.getWidth() / modelView.getField().getCloudStudents().size();
        for (int i = 0; i < modelView.getField().getCloudStudents().size(); i++) {
            cloudBox.getChildren().add(new CloudImage(i, modelView.getField(), islandImageWidth));
        }
    }
    @Override
    public void onError(ErrorMessageType error) {

    }
}
