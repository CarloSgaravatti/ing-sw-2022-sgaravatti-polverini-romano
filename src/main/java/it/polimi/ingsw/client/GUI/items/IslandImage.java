package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.FieldView;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.Objects;
import java.util.Random;

public class IslandImage {
    private int islandId;
    private FieldView fieldView;
    //private final Rectangle motherNature;
    private final AnchorPane islandPane;

    /*public IslandImage(int islandId, FieldView fieldView, double imageWidth) {
        super();
        super.setWidth(imageWidth);
        super.setHeight(imageWidth);
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/islands/island" + (new Random().nextInt(3) + 1) + ".png")));
        BackgroundSize backgroundSize = new BackgroundSize(imageWidth, imageWidth, false, false, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        super.setBackground(background);
        /*ImageView islandImage = new ImageView();
        islandImage.setFitWidth(imageWidth);
        islandImage.setPreserveRatio(true);
        islandImage.setImage(image);
        super.getChildren().add(islandImage);*/
        /*this.islandId = islandId;
        this.fieldView = fieldView;
        motherNature = new Rectangle(super.getWidth() / 5, super.getHeight() / 5);
        motherNature.setLayoutX(super.getWidth() / 2);
        motherNature.setLayoutY(super.getHeight() / 2);
        motherNature.setFill(new ImagePattern(new Image(Objects
                .requireNonNull(getClass().getResourceAsStream("/images/mother_nature.png")))));
        setMotherNature(fieldView.getMotherNaturePosition() == islandId);
        super.getChildren().add(motherNature);
    }*/

    public IslandImage(int islandId, FieldView fieldView, AnchorPane islandPane) {
        this.islandPane = islandPane;
        this.islandId = islandId;
        this.fieldView = fieldView;
        /*motherNature = new Rectangle(islandPane.getWidth() / 5, islandPane.getHeight() / 5);
        motherNature.setLayoutX(islandPane.getWidth() / 2);
        motherNature.setLayoutY(islandPane.getHeight() / 2);
        motherNature.setFill(new ImagePattern(new Image(Objects
                .requireNonNull(getClass().getResourceAsStream("/images/mother_nature.png")))));
        setMotherNature(fieldView.getMotherNaturePosition() == islandId);
        islandPane.getChildren().add(motherNature);*/
        islandPane.getStyleClass().clear();
        islandPane.getStyleClass().add("island-pane" + (new Random().nextInt(3) + 1));
        islandPane.setId("Island" + islandId);
    }

    public void setMotherNature(boolean present, Rectangle motherNature) {
        //motherNature.setOpacity((present) ? 1 : 0);
        if (present) islandPane.getChildren().add(motherNature);
        else islandPane.getChildren().remove(motherNature);
    }

    public int getIslandId() {
        return islandId;
    }

    /*public void setDragStartHandler(EventHandler<MouseEvent> dragStartHandler) {
        motherNature.setOnDragDetected(dragStartHandler);
    }*/

    public AnchorPane getIslandPane() {
        return islandPane;
    }
}
