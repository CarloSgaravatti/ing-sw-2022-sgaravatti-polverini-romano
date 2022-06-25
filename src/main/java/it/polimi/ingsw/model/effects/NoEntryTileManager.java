package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;

import java.io.Serializable;

public interface NoEntryTileManager {
    @SuppressWarnings("unused") //accessed with reflection
    void putNoEntryTileInIsland (Island island);
    void insertNoEntryTile();
}
