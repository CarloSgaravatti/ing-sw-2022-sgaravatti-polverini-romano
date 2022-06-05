package it.polimi.ingsw.client.GUI.items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SchoolBox {
    private final ImageView school;
    private final ImageView lastAssistantPlayed;

    public SchoolBox(String player, AnchorPane anchorPane) {
        school = (ImageView) anchorPane.getChildren().get(0);
        lastAssistantPlayed = (ImageView) anchorPane.getChildren().get(1);
    }

    public void setAssistantImage(Image image) {
        lastAssistantPlayed.setImage(image);
        lastAssistantPlayed.setPreserveRatio(true);
    }
}
