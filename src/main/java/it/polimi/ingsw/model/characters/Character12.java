package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.School;

import java.util.List;
import java.util.Map;

public class Character12 extends CharacterCard {
    private static final int MAX_STUDENTS_TO_REMOVE = 3;
    //Temporary solution, maybe it can be done with less brute force
    private final transient Game game;

    public Character12(Game game) {
        super(3, 12);
        this.game = game;
    }

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
}
