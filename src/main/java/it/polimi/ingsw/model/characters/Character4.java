package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

public class Character4 extends CharacterCard {
    private static Character4 instance;

    protected Character4() {
        super(1);
    }

    public static Character4 getInstance() {
        if (instance == null) instance = new Character4();
        return instance;
    }
    @Override
    public void playCard(Player player) {

    }
}
