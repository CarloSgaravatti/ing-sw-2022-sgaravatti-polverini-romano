package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

public class Character2 extends CharacterCard {
    private static Character2 instance;

    protected Character2() {
        super(2);
    }

    public static Character2 getInstance() {
        if (instance == null) instance = new Character2();
        return instance;
    }
    @Override
    public void playCard(Player player) {

    }
}
