package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

/**
 * Character1 is an CharacterCard that control 4 students; the character can insert a students from the students that he
 * controls on an island, specified by the player who plays the character. The character will use a student container
 * to control students
 *
 * @see it.polimi.ingsw.model.CharacterCard
 * @see StudentContainer
 */
public class Character1 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;

    /**
     * Constructs a Character1 that will contain a student container that will be associated to the specified game
     *
     * @param game the game from which the student container will take students
     */
    public Character1(Game game) {
        super(1, 1);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, game);
    }

    /**
     * Constructs a Character1 with the specified students container
     *
     * @param studentContainer the student container of the character
     */
    public Character1(StudentContainer studentContainer) {
        super(1, 1);
        this.studentContainer = studentContainer;
    }

    /**
     * Uses the effect of this character. The input map needs to contain an island on which a student will be inserted
     * and a RealmType that represent the student of the student container
     *
     * @param arguments the character parameters
     * @throws IllegalCharacterActionRequestedException if the student container does not have a student of the specified
     *          student type
     * @see CharacterCard#useEffect(Map)
     */
    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        RealmType studentType = (RealmType) arguments.get("Student");
        Island island = (Island) arguments.get("Island");
        Student student;
        try {
            student = studentContainer.pickStudent(studentType, true);
        } catch (StudentNotFoundException e) {
            throw new IllegalCharacterActionRequestedException(e);
        }
        island.addStudents(false, student);
        firePropertyChange(new PropertyChangeEvent(
                this, "Students", null, studentContainer.getStudents().toArray(new Student[0])));
    }

    /**
     * Restore the character after the game was restored from persistence data by adding the game as an observer of the
     * student container
     *
     * @param game the restored game
     */
    @Override
    public void restoreCharacter(Game game) {
        studentContainer.addObserver(game);
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
