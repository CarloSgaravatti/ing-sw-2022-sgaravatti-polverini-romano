package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.School;

import java.util.Map;

/**
 * Character12 is a CharacterCard that is able to remote at maximum three students from all players dining rooms of a
 * specific type.
 *
 * @see it.polimi.ingsw.model.CharacterCard
 */
public class Character12 extends CharacterCard {
    private static final int MAX_STUDENTS_TO_REMOVE = 3;
    private transient Game game;

    /**
     * Constructs a Character12 that is associated to the specified game in order to get the players from which students
     * will be removed from the dining room
     *
     * @param game the game that will be associated to the character
     */
    public Character12(Game game) {
        super(3, 12);
        this.game = game;
    }

    /**
     * Uses the effect of the character by removing students from the dining rooms of all players. The type of
     * student that will be removed is specified in the input map with the "Student" key
     *
     * @param arguments the character parameters
     */
    @Override
    public void useEffect(Map<String, Object> arguments) {
        RealmType studentType = (RealmType) arguments.get("Student");
        School school;
        for (Player p: game.getPlayers()) {
            school = p.getSchool();
            int numStudentsToRemove = Integer.min(school.getNumStudentsDiningRoom(studentType), MAX_STUDENTS_TO_REMOVE);
            try {
                for (int i = 0; i < numStudentsToRemove; i++) {
                    game.getBag().insertStudent(school.removeFromDiningRoom(studentType, true));
                }
            } catch (StudentNotFoundException ignored) {}
        }
    }

    /**
     * Restore the character after the game was restored from persistence data by associating the specified game to this
     *
     * @param game the restored game
     */
    @Override
    public void restoreCharacter(Game game) {
        this.game = game;
    }
}
