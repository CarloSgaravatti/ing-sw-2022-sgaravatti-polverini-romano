package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;

import java.util.Map;

/**
 * Character3 is a CharacterCard that is able to update an island even if mother nature is not present on the island
 */
public class Character3 extends CharacterCard {
    private transient ModelObserver modelObserver;

    /**
     * Constructs a Character3 that will be associated to the specified game in order to use the effect
     *
     * @param game the game that will be associated to the character
     */
    public Character3(Game game) {
        super(3, 3);
        this.modelObserver = game;
    }

    /**
     * Updates an island that has to be contained on the map with the key string "Island"
     *
     * @param arguments the character parameters
     * @see CharacterCard#useEffect(Map)
     */
    @Override
    public void useEffect(Map<String, Object> arguments) {
        Island island = (Island) arguments.get("Island");
        modelObserver.updateIslandTower(island);
    }

    /**
     * Restore the character after the game was restored from persistence data by associating the specified game to this
     *
     * @param game the restored game
     */
    @Override
    public void restoreCharacter(Game game) {
        this.modelObserver = game;
    }
}
