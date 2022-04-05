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
        if (noEntryTiles == 0) throw new IllegalStateException();
        island.insertNoEntryTile(this);
        noEntryTiles--;
    }

    @Override
    public void insertNoEntryTile() {
        if (noEntryTiles == NUM_NO_ENTRY_TILES_MAX) throw new IllegalStateException();
        noEntryTiles++;
    }

    public int getNoEntryTiles() {
        return noEntryTiles;
    }
}
