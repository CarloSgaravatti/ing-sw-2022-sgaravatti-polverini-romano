package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.School;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Character7 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 6;
    private final StudentContainer studentContainer;

    public Character7(ModelObserver observer) {
        super(1, 7);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    public Character7(StudentContainer studentContainer) {
        super(1, 7);
        this.studentContainer = studentContainer;
    }

    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        //Character controller checks the students number
        RealmType[] toPick = (RealmType[]) arguments.get("CharacterStudents");
        RealmType[] fromEntrance = (RealmType[]) arguments.get("EntranceStudents");
        List<Student> toEntrance = new ArrayList<>();
        List<Student> toCharacter = new ArrayList<>();
        School school = super.getPlayerActive().getSchool();
        try {
            for (RealmType realmType : toPick) {
                toEntrance.add(studentContainer.pickStudent(realmType, false));
            }
            for (RealmType realmType : fromEntrance) {
                toCharacter.add(school.removeStudentEntrance(realmType));
            }
        } catch (StudentNotFoundException e) {
            school.insertEntrance(toCharacter.toArray(new Student[0]));
            toEntrance.forEach(studentContainer::insertStudent);
            throw new IllegalCharacterActionRequestedException(e);
        }
        toCharacter.forEach(studentContainer::insertStudent);
        school.insertEntrance(toEntrance.toArray(new Student[0]));
        firePropertyChange(new PropertyChangeEvent(this, "EntranceSwap", fromEntrance, toPick));
        firePropertyChange(new PropertyChangeEvent(
                this, "Students", null, studentContainer.getStudents().toArray(new Student[0])));
    }

    @Override
    public void restoreCharacter(Game game) {
        this.studentContainer.addObserver(game);
    }

    //For testing
    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}
