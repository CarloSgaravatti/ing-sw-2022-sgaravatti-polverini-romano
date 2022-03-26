package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

public interface InfluenceStrategy {
    int getInfluence(Island island, Player player);
}
