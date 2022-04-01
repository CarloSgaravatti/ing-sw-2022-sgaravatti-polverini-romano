package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;

public class Character7 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 6;
    private static Character7 instance;
    private final StudentContainer studentContainer;

    protected Character7(ModelObserver observer) {
        super(1, 7);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    public static Character7 getInstance(ModelObserver observer) {
        if (instance == null) instance = new Character7(observer);
        return instance;
    }

    @SuppressWarnings("unused") //accessed with reflection
    public void pickAndSwapStudents(RealmType[] toPick, RealmType[] fromEntrance) throws StudentNotFoundException {
        Student[] toEntrance = new Student[toPick.length];
        School school = super.getPlayerActive().getSchool();
        for (int i = 0; i < toPick.length; i++) {
            toEntrance[i] = studentContainer.pickStudent(toPick[i], false);
        }
        for (RealmType s: fromEntrance) {
            studentContainer.insertStudent(school.removeStudentEntrance(s));
        }
        school.insertEntrance(toEntrance);
    }
}
