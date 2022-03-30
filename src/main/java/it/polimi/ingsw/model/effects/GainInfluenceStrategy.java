package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

public class GainInfluenceStrategy implements InfluenceStrategy {
    private final InfluenceStrategy influenceStrategy;

    public GainInfluenceStrategy(InfluenceStrategy influenceStrategy) {
        this.influenceStrategy = influenceStrategy;
    }

    @Override
    public int getInfluence (Island island, Player player) {
        return influenceStrategy.getInfluence(island, player) + player.getTurnEffect().getAdditionalInfluence();
    }
}
