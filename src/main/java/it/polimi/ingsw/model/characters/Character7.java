package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.School;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class Character7 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 6;
    private final StudentContainer studentContainer;

    public Character7(ModelObserver observer) {
        super(1, 7);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
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
        if (toPick.length != fromEntrance.length || toPick.length > 3 || toPick.length <= 0) {
            throw new IllegalArgumentException();
        }
        Student[] toEntrance = new Student[toPick.length];
        School school = super.getPlayerActive().getSchool();
        for (int i = 0; i < toPick.length; i++) {
            toEntrance[i] = studentContainer.pickStudent(toPick[i], false);
        }
        for (RealmType s: fromEntrance) {
            studentContainer.insertStudent(school.removeStudentEntrance(s));
        }
        school.insertEntrance(toEntrance);
        firePropertyChange(new PropertyChangeEvent(this, "SwapFromEntrance", toPick, fromEntrance));
    }

    //For testing
    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}
