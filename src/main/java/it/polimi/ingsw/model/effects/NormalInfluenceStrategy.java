package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.RealmType;

public class NormalInfluenceStrategy implements InfluenceStrategy {
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
