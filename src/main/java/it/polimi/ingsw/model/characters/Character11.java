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

/**
 * Character11 is a CharacterCard that controls 4 students. The character is able to insert in the dining room of the
 * active player of the character a student from the student container that contains the students of the character
 *
 * @see it.polimi.ingsw.model.CharacterCard
 * @see StudentContainer
 */
public class Character11 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;

    /**
     * Constructs a Character11 that will contain a student container that will be associated to the specified observer
     *
     * @param observer the game from which the student container will take students
     */
    public Character11(ModelObserver observer) {
        super(2, 11);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    /**
     * Constructs a Character11 with the specified students container
     *
     * @param studentContainer the student container of the character
     */
    public Character11(StudentContainer studentContainer) {
        super(2, 11);
        this.studentContainer = studentContainer;
    }

    /**
     * Uses the effect of this character. The input map needs a RealmType on the "Student" key
     *
     * @param arguments the character parameters
     * @throws IllegalCharacterActionRequestedException if the student container does not have a student of the specified
     *          student type in the input map
     * @see CharacterCard#useEffect(Map)
     */
    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        RealmType studentType = (RealmType) arguments.get("Student");
        School school = getPlayerActive().getSchool();
        try {
            school.insertDiningRoom(new Student[] {studentContainer.pickStudent(studentType, true)}, true, false);
        } catch (FullDiningRoomException | StudentNotFoundException e) {
            throw new IllegalCharacterActionRequestedException(e);
        }
        firePropertyChange(new PropertyChangeEvent(
                this, "Students", null, getStudents().toArray(new Student[0])));
    }

    /**
     * Restore the character after the game was restored from persistence data by adding the game as an observer of the
     * student container
     *
     * @param game the restored game
     */
    @Override
    public void restoreCharacter(Game game) {
        this.studentContainer.addObserver(game);
    }

    /**
     * Returns the students that are contained on the student container of the character
     *
     * @return the students that are contained on the student container of the character
     */
    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}

