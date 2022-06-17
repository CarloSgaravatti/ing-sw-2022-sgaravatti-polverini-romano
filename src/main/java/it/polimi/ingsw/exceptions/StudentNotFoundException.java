package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.model.enumerations.RealmType;

public class StudentNotFoundException extends Exception {
    private RealmType studentType;
    private String description; //dining room, entrance or character (1, 7, 11)
    public StudentNotFoundException() {

    }

    public StudentNotFoundException(RealmType studentType, String description) {
        this.studentType = studentType;
        this.description = description;
    }

    @Override
    public String getMessage() {
        return "The student of realm " + studentType + " wasn't found in " + description;
    }
}
