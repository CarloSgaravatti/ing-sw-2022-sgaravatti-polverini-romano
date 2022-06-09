package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.constants.Constants;
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
    @FXML private HBox hBox;
    @FXML private Label upperText;
    @FXML private Button confirmChoice;
    @FXML private Label bottomText;
    private List<ImageView> images;
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
        double maxWidth = hBox.getWidth() / towersFree.length;
        upperText.setText("You have to choose a tower");
        bottomText.setText("");
        images = new ArrayList<>();
        for (TowerType tower: towersFree) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.towerImages.get(tower))));
            images.add(createImageView(image, tower.toString(), towerEventHandler, maxWidth));
        }
        addImagesToScene();
    }

    public void setSceneWithWizards(WizardType[] wizardsFree) {
        double maxWidth = hBox.getWidth() / wizardsFree.length;
        upperText.setText("You have to choose a wizard");
        bottomText.setText("");
        images = new ArrayList<>();
        for (WizardType wizard: wizardsFree) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.wizardImages.get(wizard))));
            images.add(createImageView(image, wizard.toString(), wizardEventHandler, maxWidth));
        }
        addImagesToScene();
    }

    private ImageView createImageView(Image image, String id, EventHandler<MouseEvent> handler, double maxWidth) {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setId(id);
        imageView.getStyleClass().addAll("clickableImage");
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
        imageView.setPreserveRatio(true);
        if ((image.getRequestedWidth() / image.getRequestedHeight()) * hBox.getHeight() > maxWidth) {
            imageView.setFitWidth(maxWidth);
        } else {
            imageView.setFitHeight(hBox.getHeight());
        }
        return imageView;
    }

    private void addImagesToScene() {
        hBox.getChildren().clear();
        hBox.getChildren().addAll(images);
    }
}
