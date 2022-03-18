package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.RealmType;

public class Character9 extends CharacterCard {
    private static Character9 instance;

    protected Character9() {
        super(3);
    }

    public static Character9 getInstance() {
        if (instance == null) instance = new Character9();
        return instance;
    }

    @Override
    public void playCard(Player player) {

    }

    public RealmType chooseRealm(){
        return null;
    }
}
