package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.model.enumerations.RealmType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Map;
import java.util.Objects;

public class StudentImage extends Circle {
    private RealmType student;

    public StudentImage(double radius, RealmType student) {
        super(radius);
        setStudent(student);
    }

    public StudentImage(Circle circle) {
        super(circle.getRadius());
        super.setLayoutX(circle.getLayoutX());
        super.setLayoutY(circle.getLayoutY());
        super.setOpacity(0);
    }

    public void setStudent(RealmType student) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.studentsImages.get(student))));
        super.setFill(new ImagePattern(image));
        super.setOpacity(1);
        this.student = student;
    }

    public RealmType getStudentType() {
        return student;
    }

    public void reset() {
        super.setOpacity(0);
        student = null;
    }
}
