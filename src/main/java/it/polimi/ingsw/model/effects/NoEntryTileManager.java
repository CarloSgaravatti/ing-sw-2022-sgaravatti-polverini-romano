package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;

/**
 * NoEntryTileManager is a container of no entry tiles that is able to dispatch no entry tiles to islands and to receive
 * no entry tiles from islands
 */
public interface NoEntryTileManager {
    /**
     * Insert a no entry on the specified island
     *
     * @param island the island on which the no entry tile will be inserted
     */
    void putNoEntryTileInIsland (Island island);

    /**
     * Insert a no entry tile on the no entry tile manager
     */
    void insertNoEntryTile();
}
