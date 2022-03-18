package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;

public class Character10 extends CharacterCard {
    private static Character10 instance;

    protected Character10() {
        super(1);
    }

    public static Character10 getInstance() {
        if (instance == null) instance = new Character10();
        return instance;
    }

    public int chooseNumSwap(){
        return 0;
    }

    @Override
    public void playCard(Player player) {

    }

    public void swap(Student[] toEntrance, Student[] toDiningRoom){

    }
}
