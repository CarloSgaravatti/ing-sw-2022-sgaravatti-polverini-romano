package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.FieldView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;
import java.util.Random;

public class CloudImage extends AnchorPane {
    private int cloudId;
    private FieldView fieldView;

    public CloudImage(int cloudId, FieldView fieldView, double imageWidth) {
        super();
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/clouds/cloud_card_" + (new Random().nextInt(5) + 1) + ".png")));
        /*BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
        Background background = new Background(backgroundImage);
        super.setBackground(background);*/
        ImageView islandImage = new ImageView();
        islandImage.setFitWidth(imageWidth);
        islandImage.setPreserveRatio(true);
        islandImage.setImage(image);
        super.getChildren().add(islandImage);
        this.cloudId = cloudId;
        this.fieldView = fieldView;
    }
}
