package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

public class Character5 extends CharacterCard {
    private int noEntryTiles;
    private static Character5 instance;

    protected Character5() {
        super(2);
    }

    public static Character5 getInstance() {
        if (instance == null) instance = new Character5();
        return instance;
    }

    @Override
    public void playCard(Player player) {

    }

    public void pickEntryTile(){}

    public void putEntryTile(){}
}
