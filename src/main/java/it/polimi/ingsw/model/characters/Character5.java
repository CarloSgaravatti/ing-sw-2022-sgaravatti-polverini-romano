package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.effects.NoEntryTileManager;

public class Character5 extends CharacterCard implements NoEntryTileManager {
    private int noEntryTiles;
    private static Character5 instance;
    private final static int NUM_NO_ENTRY_TILES_MAX = 4;

    protected Character5() {
        super(2, 5);
        noEntryTiles = NUM_NO_ENTRY_TILES_MAX;
    }

    public static Character5 getInstance() {
        if (instance == null) instance = new Character5();
        return instance;
    }

    @Override
    public void putNoEntryTileInIsland (Island island) {
        island.insertNoEntryTile(this);
        noEntryTiles--;
        //TODO: handle the case of 0 no entry tiles ready to be placed
    }

    @Override
    public void insertNoEntryTile() {
        noEntryTiles++;
        //TODO: handle the case of reaching the maximum number of no entry tiles
    }
}
