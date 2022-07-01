package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.effects.NoEntryTileManager;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

/**
 * Character5 is a CharacterCard that contains no entry tiles, and therefore it implements also the NoEntryTileManger
 * interface.
 *
 * @see it.polimi.ingsw.model.CharacterCard
 * @see it.polimi.ingsw.model.effects.NoEntryTileManager
 */
public class Character5 extends CharacterCard implements NoEntryTileManager {
    private int noEntryTiles;
    private final static int NUM_NO_ENTRY_TILES_MAX = 4;
    private transient List<Island> islands;

    /**
     * Constructs a Character5 that will be manage no entry tiles for the islands that are present in the game
     *
     * @param game the game from which the character will see the islands
     */
    public Character5(Game game) {
        super(2, 5);
        noEntryTiles = NUM_NO_ENTRY_TILES_MAX;
        if (game != null) islands = game.getIslands();
    }

    /**
     * Uses the effect to insert a no entry tile on an island. The island is present in the map at the key "Island"
     *
     * @param arguments the character parameters
     * @throws IllegalCharacterActionRequestedException if the character doesn't have anymore no entry tiles on it
     * @see CharacterCard#useEffect(Map)
     */
    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        Island island = (Island) arguments.get("Island");
        try {
            putNoEntryTileInIsland(island);
        } catch (IllegalStateException e) {
            throw new IllegalCharacterActionRequestedException("All no entry tiles have been already putted on islands");
        }
    }

    /**
     * Restore the character after the game was restored from persistence data
     *
     * @param game the restored game
     */
    @Override
    public void restoreCharacter(Game game) {
        this.islands = game.getIslands();
    }

    /**
     * Insert a no entry tile on the specified island
     *
     * @param island the island on which the no entry tile will be inserted
     */
    @Override
    public void putNoEntryTileInIsland (Island island) {
        if (noEntryTiles == 0) throw new IllegalStateException();
        island.insertNoEntryTile(this);
        noEntryTiles--;
        firePropertyChange(new PropertyChangeEvent(this, "NoEntryTile", null, islands.indexOf(island)));
    }

    /**
     * Insert a no entry tile on the character
     */
    @Override
    public void insertNoEntryTile() {
        if (noEntryTiles == NUM_NO_ENTRY_TILES_MAX) throw new IllegalStateException();
        noEntryTiles++;
    }

    /**
     * Returns the number of no entry tiles that the character contain
     *
     * @return the number of no entry tiles that the character contain
     */
    public int getNoEntryTiles() {
        return noEntryTiles;
    }

    /**
     * Set the number of no entry tiles that the character contain
     *
     * @param noEntryTiles the number of no entry tiles that the character contain
     */
    public void setNoEntryTiles(int noEntryTiles) {
        this.noEntryTiles = noEntryTiles;
    }
}
