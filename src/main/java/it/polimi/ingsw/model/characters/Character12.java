package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.RealmType;
import it.polimi.ingsw.model.Student;

public class Character12 extends CharacterCard {
    private static Character12 instance;

    protected Character12() {
        super(3);
    }

    public static Character12 getInstance() {
        if (instance == null) instance = new Character12();
        return instance;
    }

    @Override
    public void playCard(Player player) {

    }

    public RealmType chooseRealm(){
        return null;
    }

    //For all players
    public void sendToBag(Student[] students){

    }
}
