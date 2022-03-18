package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;

public class Character7 extends CharacterCard {
    private static final int numStudents = 6;
    private Student[] students;
    private static Character7 instance;

    protected Character7() {
        super(1);
    }

    public static Character7 getInstance() {
        if (instance == null) instance = new Character7();
        return instance;
    }


    @Override
    public void playCard(Player player) {

    }

    public void pickStudents(int num, Student[] studentsToInsert, Student[] studentsToTake){

    }
}
