package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;

public class Character12 extends CharacterCard {
    private static Character12 instance;
    private static final int MAX_STUDENTS_TO_REMOVE = 3;
    //Temporary solution, maybe it can be done with less brute force
    private final Game game;

    protected Character12(Game game) {
        super(3, 12);
        this.game = game;
    }

    public static Character12 getInstance(Game game) {
        if (instance == null) instance = new Character12(game);
        return instance;
    }

    @SuppressWarnings("unused") //accessed with reflection
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
