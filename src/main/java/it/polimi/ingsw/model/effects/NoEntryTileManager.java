package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;

public interface NoEntryTileManager {
    @SuppressWarnings("unused") //accessed with reflection
    void putNoEntryTileInIsland (Island island);
    void insertNoEntryTile();
}
