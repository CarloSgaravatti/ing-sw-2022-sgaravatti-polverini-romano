package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.RealmType;

public class NoStudentInfluenceStrategy implements InfluenceStrategy {
    private final InfluenceStrategy influenceStrategy;
    private final RealmType studentType;

    public NoStudentInfluenceStrategy(InfluenceStrategy influenceStrategy, RealmType studentType) {
        this.influenceStrategy = influenceStrategy;
        this.studentType = studentType;
    }

    @Override
    public int getInfluence (Island island, Player player) {
        int toSubtract = island.getNumStudentsOfType(studentType);
        return influenceStrategy.getInfluence(island, player) - toSubtract;
    }
}
