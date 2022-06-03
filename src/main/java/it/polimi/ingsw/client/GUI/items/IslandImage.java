package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.FieldView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;

import java.util.Objects;
import java.util.Random;

public class IslandImage extends AnchorPane {
    private int islandId;
    private FieldView fieldView;

    public IslandImage(int islandId, FieldView fieldView, double imageWidth) {
        super();
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/islands/island" + (new Random().nextInt(3) + 1) + ".png")));
        /*BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
        Background background = new Background(backgroundImage);
        super.setBackground(background);*/
        ImageView islandImage = new ImageView();
        islandImage.setFitWidth(imageWidth);
        islandImage.setPreserveRatio(true);
        islandImage.setImage(image);
        super.getChildren().add(islandImage);
        this.islandId = islandId;
        this.fieldView = fieldView;
    }

    public void setMotherNature(boolean present) {
        if (present) {

        } else {

        }
    }
}
