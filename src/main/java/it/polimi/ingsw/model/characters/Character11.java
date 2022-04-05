package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.List;

public class Character11 extends CharacterCard {
    private static Character11 instance;
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;

    protected Character11(ModelObserver observer) {
        super(2, 11);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    public static Character11 getInstance(ModelObserver observer) {
        if (instance == null) instance = new Character11(observer);
        return instance;
    }

    @SuppressWarnings("unused") //accessed with reflection
    public void pickAndSendToDiningRoom(RealmType studentType) throws StudentNotFoundException, FullDiningRoomException {
        School school = super.getPlayerActive().getSchool();
        school.insertDiningRoom(studentContainer.pickStudent(studentType, true));
    }

    //For testing
    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}

