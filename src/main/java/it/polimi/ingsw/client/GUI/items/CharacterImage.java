package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.model.enumerations.RealmType;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.Objects;

/**
 * CharacterImage is a representation of a character in the main scene as a usable card
 *
 */
public class CharacterImage extends AnchorPane {
    private final int characterId;
    private final ExpertFieldView expertField;
    private HBox studentsBox;
    private double studentRadius;

    /**
     * Constructor of the CharacterImage. The image will be a card that represent the action and the image of the character
     *
     * @param characterId index of the character
     * @param imageWidth width of the image of the character
     * @param expertField view of the field for expert game
     */
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
            AnchorPane.setBottomAnchor(studentsBox, 0.0);
            //studentsBox.setLayoutY(super.getHeight() - studentsBox.getHeight());
            super.getChildren().add(studentsBox);
        }
    }

    /**
     * method getCharacterId gets the index of the character
     *
     * @return returns the index of the character
     */
    public int getCharacterId() {
        return characterId;
    }

    /**
     * method updateStudents inserts new students after a character's action.
     *
     */
    public void updateStudents() {
        studentsBox.getChildren().clear();
        RealmType[] students = RealmType.getRealmsFromIntegerRepresentation(expertField.characterStudents(characterId));
        studentRadius = (studentsBox.getMaxWidth() - (5 * students.length)) / (2 * students.length);
        for (RealmType student: students) {
            StudentImage studentImage = new StudentImage(studentRadius, student);
            studentsBox.getChildren().add(studentImage);
        }
    }

    /**
     * method putCoin inserts a coin after a character's action
     *
     */
    public void putCoin() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/schools/coin.png")));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(super.getWidth() / 4);
        imageView.setPreserveRatio(true);
        AnchorPane.setBottomAnchor(imageView, 50.0);
        imageView.setLayoutY(super.getHeight() / 2);
        super.getChildren().add(imageView);
    }
}
