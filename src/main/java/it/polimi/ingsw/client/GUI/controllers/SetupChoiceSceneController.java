package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SetupChoiceSceneController extends FXMLController {
    @FXML
    private HBox hBox;
    @FXML
    private Label upperText;
    @FXML
    private Button confirmChoice;
    @FXML
    private Label bottomText;
    private List<ImageView> images;
    private static final Map<TowerType, String> towerImages = Map.of(TowerType.BLACK, "/images/black_tower.png",
            TowerType.WHITE, "/images/white_tower.png", TowerType.GREY, "/images/grey_tower.png");
    private static final Map<WizardType, String> wizardImages = Map.of(WizardType.values()[0], "/images/assistants/CarteTOT_back_1@3x.png",
            WizardType.values()[1], "/images/assistants/CarteTOT_back_11@3x.png",
            WizardType.values()[2], "/images/assistants/CarteTOT_back_21@3x.png",
            WizardType.values()[3], "/images/assistants/CarteTOT_back_31@3x.png");
    private final EventHandler<MouseEvent> towerEventHandler = mouseEvent -> {
        ImageView imageClicked = (ImageView) mouseEvent.getTarget();
        TowerType tower = TowerType.valueOf(imageClicked.getId());
        bottomText.setText("You have chosen " + tower.toString().toLowerCase());
        confirmChoice.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent1 ->
                firePropertyChange("TowerChoice", null, tower));
    };
    private final EventHandler<MouseEvent> wizardEventHandler = mouseEvent -> {
        ImageView imageClicked = (ImageView) mouseEvent.getTarget();
        WizardType wizard = WizardType.valueOf(imageClicked.getId());
        bottomText.setText("You have chosen " + wizard.toString().toLowerCase());
        confirmChoice.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent1 ->
                firePropertyChange("WizardChoice", null, wizard));
    };

    @Override
    public void onError(ErrorMessageType error) {

    }

    public void setSceneWithTowers(TowerType[] towersFree) {
        upperText.setText("You have to choose a tower");
        bottomText.setText("");
        images = new ArrayList<>();
        for (TowerType tower: towersFree) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(towerImages.get(tower))));
            images.add(createImageView(image, tower.toString(), towerEventHandler));
        }
        addImagesToScene();
    }

    public void setSceneWithWizards(WizardType[] wizardsFree) {
        upperText.setText("You have to choose a wizard");
        bottomText.setText("");
        images = new ArrayList<>();
        for (WizardType wizard: wizardsFree) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(wizardImages.get(wizard))));
            images.add(createImageView(image, wizard.toString(), wizardEventHandler));
        }
        addImagesToScene();
    }

    private ImageView createImageView(Image image, String id, EventHandler<MouseEvent> handler) {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setId(id);
        imageView.getStyleClass().addAll("clickableImage");
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(hBox.getPrefHeight());
        return imageView;
    }

    private void addImagesToScene() {
        hBox.getChildren().clear();
        hBox.getChildren().addAll(images);
        /*double imageXPosition = imagesAnchor.getLayoutX();
        double imageYPosition = imagesAnchor.getLayoutY();
        double imageSize = imagesAnchor.getPrefWidth() / images.size();
        for (ImageView imageView: images) {
            imagesAnchor.getChildren().add(imageView);
            imageView.setX(imageXPosition);
            imageView.setY(imageYPosition);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(imageSize);
            imageXPosition += imageSize;
        }*/
    }
}
