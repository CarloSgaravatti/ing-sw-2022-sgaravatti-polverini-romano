package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.effects.NoEntryTileManager;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

public class Character5 extends CharacterCard implements NoEntryTileManager {
    private int noEntryTiles;
    private final static int NUM_NO_ENTRY_TILES_MAX = 4;
    private final List<Island> islands;

    public Character5(Game game) {
        super(2, 5);
        noEntryTiles = NUM_NO_ENTRY_TILES_MAX;
        islands = game.getIslands();
    }

    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        Island island = (Island) arguments.get("Island");
        putNoEntryTileInIsland(island);
    }

    @Override
    public void putNoEntryTileInIsland (Island island) {
        if (noEntryTiles == 0) throw new IllegalStateException();
        island.insertNoEntryTile(this);
        noEntryTiles--;
        firePropertyChange(new PropertyChangeEvent(this, "NoEntryTile", null, islands.indexOf(island)));
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
