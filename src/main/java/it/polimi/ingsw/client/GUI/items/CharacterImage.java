package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.model.enumerations.RealmType;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

public class CharacterImage extends AnchorPane {
    private final int characterId;
    private final ExpertFieldView expertField;
    private HBox studentsBox;
    private double studentRadius;

    public CharacterImage(int characterId, double imageWidth, ExpertFieldView expertField) {
        super();
        super.setWidth(imageWidth);
        this.characterId = characterId;
        this.expertField = expertField;
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/characters/character" + characterId + ".jpg")));
        ImageView imageView = new ImageView();
        imageView.setFitWidth(imageWidth);
        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        super.getChildren().add(imageView);
        super.setHeight(imageView.getFitHeight());
        if (expertField.isCharacterWithStudents(characterId)) {
            studentsBox = new HBox();
            studentsBox.setMaxWidth(super.getWidth());
            studentsBox.setSpacing(5);
            updateStudents();
            studentsBox.setMaxHeight(10 + 2 * studentRadius);
            studentsBox.setAlignment(Pos.CENTER);
            studentsBox.setLayoutY(super.getHeight() - studentsBox.getHeight());
            super.getChildren().add(studentsBox);
        }
    }

    public int getCharacterId() {
        return characterId;
    }

    public void updateStudents() {
        studentsBox.getChildren().clear();
        RealmType[] students = RealmType.getRealmsFromIntegerRepresentation(expertField.characterStudents(characterId));
        studentRadius = (studentsBox.getMaxWidth() - (5 * students.length)) / (2 * students.length);
        for (RealmType student: students) {
            StudentImage studentImage = new StudentImage(studentRadius, student);
            studentsBox.getChildren().add(studentImage);
        }
    }

    public void removeNoEntryTile() {

    }

    public void putCoin() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/schools/coin.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(super.getWidth() / 4);
        imageView.setPreserveRatio(true);
        imageView.setLayoutY(super.getHeight() / 2);
        super.getChildren().add(imageView);
    }
}
