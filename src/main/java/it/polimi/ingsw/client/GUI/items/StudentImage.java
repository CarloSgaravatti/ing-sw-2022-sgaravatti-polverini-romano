package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.model.enumerations.RealmType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Objects;

/**
 * StudentImage is gui representation of a student, which is a javafx circle that is filled with an image of a type
 * of student.
 * @see javafx.scene.shape.Circle
 */
public class StudentImage extends Circle {
    private RealmType student;

    /**
     * Creates a StudentImage that have the specified radius and that contains the image of the specified realm
     *
     * @param radius the radius of the circle
     * @param student the realm of the student
     */
    public StudentImage(double radius, RealmType student) {
        super(radius);
        setStudent(student);
    }

    /**
     * Creates a StudentImage that will replace the specified circle in the scene. Indeed, it will have the same radius
     * and the same layout of the circle. The created student will be initially empty, therefore it will not be visible
     * in the scene.
     *
     * @param circle the circle that the student will replace
     */
    public StudentImage(Circle circle) {
        super(circle.getRadius());
        super.setLayoutX(circle.getLayoutX());
        super.setLayoutY(circle.getLayoutY());
        super.setOpacity(0);
    }

    /**
     * Set the realm of the StudentImage. This will change the image contained in the circle with the realm's corresponding
     * image.
     *
     * @param student the new realm of the student image
     */
    public void setStudent(RealmType student) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.studentsImages.get(student))));
        super.setFill(new ImagePattern(image));
        super.setOpacity(1);
        this.student = student;
    }

    /**
     * Return the realm of the student
     *
     * @return the realm of the student
     */
    public RealmType getStudentType() {
        return student;
    }

    /**
     * Reset the realm of the StudentImage, the student will not be visible until the realm will be set another time.
     */
    public void reset() {
        super.setOpacity(0);
        student = null;
    }
}
