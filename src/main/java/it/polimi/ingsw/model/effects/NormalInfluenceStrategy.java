package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.RealmType;

/**
 * This class provides the basic way of calculating an influence on an island (without any additional effects provided
 * by a character).
 * @see it.polimi.ingsw.model.effects.InfluenceStrategy
 */
public class NormalInfluenceStrategy implements InfluenceStrategy {
    /**
     * Return the classic influence of the specified player on the specified island
     * @param island the island on which the influence is calculated
     * @param player the player that has the returned influence on the island
     * @return the influence of the player on the island
     */
    @Override
    public int getInfluence (Island island, Player player) {
        int res = 0;
        for (RealmType r: RealmType.values()) {
            if (player.getSchool().isProfessorPresent(r))
                res += island.getNumStudentsOfType(r);
        }
        if (island.getTowerType() == player.getSchool().getTowerType())
            res += island.getNumTowers();
        return res;
    }
}
