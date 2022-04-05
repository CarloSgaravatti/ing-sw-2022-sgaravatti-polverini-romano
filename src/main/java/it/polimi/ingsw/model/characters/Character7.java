package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.List;

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

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        int studentsToPick = Integer.parseInt(args.get(0));
        if (studentsToPick > 3 || studentsToPick <= 0) {
            throw new IllegalCharacterActionRequestedException();
        }
        RealmType[] toPick = RealmType.getRealmsByAbbreviations(args.subList(1, studentsToPick + 1));
        RealmType[] fromEntrance = RealmType.getRealmsByAbbreviations(args.
                subList(studentsToPick + 1, (2 * studentsToPick) + 1));
        try {
            pickAndSwapStudents(toPick, fromEntrance);
        } catch (StudentNotFoundException e) {
            throw new IllegalCharacterActionRequestedException();
        }
    }

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

    //For testing
    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}
