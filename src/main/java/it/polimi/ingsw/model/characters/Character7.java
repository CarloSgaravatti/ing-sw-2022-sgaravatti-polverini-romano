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

/**
 * Character7 is a CharacterCard that controls 6 students. The character is able to take at maximum 2 students from the
 * entrance of the player who plays the character and swap them with 2 students from the student container.
 *
 * @see it.polimi.ingsw.model.CharacterCard
 * @see StudentContainer
 */
public class Character7 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 6;
    private final StudentContainer studentContainer;

    /**
     * Constructs a Character7 that will contain a student container that will be associated to the specified observer
     *
     * @param observer the game from which the student container will take students
     */
    public Character7(ModelObserver observer) {
        super(1, 7);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    /**
     * Constructs a Character7 with the specified students container
     *
     * @param studentContainer the student container of the character
     */
    public Character7(StudentContainer studentContainer) {
        super(1, 7);
        this.studentContainer = studentContainer;
    }

    /**
     * Uses the effect of the character for the player who has played the character. The input map have to contain
     * two RealmType[] with the keys "CharacterStudents" and "EntranceStudents". The CharacterController will make
     * sure that the input is correct and that the two arrays have the same length.
     *
     * @param arguments the character parameters
     * @throws IllegalCharacterActionRequestedException if a student was not found in the entrance or in the student
     *          container of the character
     * @see CharacterCard#useEffect(Map)
     */
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
