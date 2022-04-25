package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.util.List;

public class Character12 extends CharacterCard {
    private static final int MAX_STUDENTS_TO_REMOVE = 3;
    //Temporary solution, maybe it can be done with less brute force
    private final Game game;

    public Character12(Game game) {
        super(3, 12);
        this.game = game;
    }

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        RealmType studentType;
        try {
            studentType = RealmType.getRealmByAbbreviation(args.get(0));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        try {
            removeStudentsFromDiningRoom(studentType);
        } catch (StudentNotFoundException e) {
            throw new IllegalCharacterActionRequestedException();
        }
    }

    public void removeStudentsFromDiningRoom(RealmType studentType) throws StudentNotFoundException {
        School school;
        for (Player p: game.getPlayers()) {
            school = p.getSchool();
            int numStudentsToRemove = Integer.min(school.getNumStudentsDiningRoom(studentType), MAX_STUDENTS_TO_REMOVE);
            for (int i = 0; i < numStudentsToRemove; i++) {
                game.getBag().insertStudent(school.removeFromDiningRoom(studentType));
            }
        }
    }
}
