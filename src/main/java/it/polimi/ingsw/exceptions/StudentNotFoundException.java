package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.model.enumerations.RealmType;

/**
 * StudentNotFoundException is thrown when someone tries to select a student from a place that doesn't have the selected
 * student.
 */
public class StudentNotFoundException extends Exception {
    private RealmType studentType;
    private String description; //dining room, entrance or character (1, 7, 11)

    public StudentNotFoundException() {}

    /**
     * Constructs a new StudentNotFoundException that was thrown while searching a student of the specified type in the
     * specified place
     *
     * @param studentType the type of student that was searched
     * @param description the place whe
     */
    public StudentNotFoundException(RealmType studentType, String description) {
        this.studentType = studentType;
        this.description = description;
    }

    /**
     * Return the message associated to the StudentNotFoundException, this will be sent to the client in an
     * error message
     *
     * @return the message associated to the StudentNotFoundException
     */
    @Override
    public String getMessage() {
        return "The student of realm " + studentType + " wasn't found in " + description;
    }
}
