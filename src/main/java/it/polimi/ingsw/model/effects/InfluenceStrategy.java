package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

/**
 * An InfluenceStrategy represent a way to calculate the influence on an island, and it is a player property during his
 * turn. This is implemented as a Strategy pattern.
 */
public interface InfluenceStrategy {
    /**
     * Returns the influence of the specified player on the specified island
     * @param island the island on which the influence is calculated
     * @param player the player that has the returned influence on the island
     * @return the influence of the player on the island
     */
    int getInfluence(Island island, Player player);
}
