package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

/**
 * GainInfluenceStrategy is a way of calculating the influence that consider also the fact that a player can have an
 * advantage in the influence calculation (additional points).
 * @see it.polimi.ingsw.model.effects.InfluenceStrategy
 */
public class GainInfluenceStrategy implements InfluenceStrategy {
    private final InfluenceStrategy influenceStrategy;

    /**
     * Construct an instance of GainInfluenceStrategy that will add eventual additional points to the specified
     * influence strategy.
     * @param influenceStrategy the influence strategy that this class is adjusting with additional points
     */
    public GainInfluenceStrategy(InfluenceStrategy influenceStrategy) {
        this.influenceStrategy = influenceStrategy;
    }

    /**
     * Return an influence which is the influence of the associated influence strategy of this object, plus the (eventually
     * 0 additional) additional points of the specified player.
     * @param island the island on which the influence is calculated
     * @param player the player that has the returned influence on the island
     * @return the influence of the player in the island
     */
    @Override
    public int getInfluence (Island island, Player player) {
        return influenceStrategy.getInfluence(island, player) + player.getTurnEffect().getAdditionalInfluence();
    }
}
