package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

public class Character1 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;
    private final List<Island> islands;

    public Character1(Game game) {
        super(1, 1);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, game);
        this.islands = game.getIslands();
    }

    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        RealmType studentType = (RealmType) arguments.get("Student");
        Island island = (Island) arguments.get("Island");
        Student student;
        try {
            student = studentContainer.pickStudent(studentType, true);
        } catch (StudentNotFoundException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        island.addStudents(false, student);
        firePropertyChange(new PropertyChangeEvent(
                this, "Students", null, studentContainer.getStudents().toArray(new Student[0])));
    }

    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}
