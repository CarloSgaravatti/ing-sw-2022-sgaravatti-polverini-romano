package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;

public class Character1 extends CharacterCard {
    private static Character1 instance;
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;

    protected Character1(ModelObserver observer) {
        super(1, 1);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    public static Character1 getInstance(ModelObserver observer) {
        if (instance == null) instance = new Character1(observer);
        return instance;
    }

    @SuppressWarnings("unused") //accessed with reflection
    public void pickAndSendToIsland(RealmType studentType, Island island) throws StudentNotFoundException {
        Student student = studentContainer.pickStudent(studentType, true);
        island.addStudent(student);
    }
}
