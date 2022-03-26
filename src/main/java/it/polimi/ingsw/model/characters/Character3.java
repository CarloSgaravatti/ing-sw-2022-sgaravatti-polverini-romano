package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

public class Character3 extends CharacterCard {
    private static Character3 instance;

    protected Character3() {
        super(3, 3);
    }

    public static Character3 getInstance() {
        if (instance == null) instance = new Character3();
        return instance;
    }

    @Override
    public void playCard(Player player) {

    }
}
