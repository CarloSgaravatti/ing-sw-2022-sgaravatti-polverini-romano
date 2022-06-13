package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.School;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

public class Character11 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;

    public Character11(ModelObserver observer) {
        super(2, 11);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        RealmType studentType = (RealmType) arguments.get("Student");
        School school = getPlayerActive().getSchool();
        try {
            school.insertDiningRoom(new Student[] {studentContainer.pickStudent(studentType, true)}, true, false);
        } catch (FullDiningRoomException | StudentNotFoundException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        firePropertyChange(new PropertyChangeEvent(
                this, "Students", null, getStudents().toArray(new Student[0])));
    }

    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}

