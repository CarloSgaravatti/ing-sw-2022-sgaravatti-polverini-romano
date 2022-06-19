package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

/**
 * This class calculates the influence of a player in an island by not considering the eventual towers that are present
 * on an island.
 * @see it.polimi.ingsw.model.effects.InfluenceStrategy
 */
public class NoTowerInfluenceStrategy implements InfluenceStrategy {
    public final InfluenceStrategy influenceStrategy;

    public NoTowerInfluenceStrategy(InfluenceStrategy influenceStrategy) {
        this.influenceStrategy = influenceStrategy;
    }

    @Override
    public int getInfluence (Island island, Player player) {
        int toSubtract = (island.getTowerType() == player.getSchool().getTowerType()) ? island.getNumTowers() : 0;
        return influenceStrategy.getInfluence(island, player) - toSubtract;
    }
}
