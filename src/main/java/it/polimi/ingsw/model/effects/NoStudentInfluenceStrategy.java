package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.RealmType;

/**
 * This class calculates the influence considering the fact that all students of a specific RealmType will not be
 * considered during the influence calculation.
 */
public class NoStudentInfluenceStrategy implements InfluenceStrategy {
    private final InfluenceStrategy influenceStrategy;
    private final RealmType studentType;

    /**
     * Construct a NoStudentInfluenceStrategy that will not consider the specified RealmType
     * @param influenceStrategy the previous strategy that this class will use on calculation
     * @param studentType the student type that will not be considered
     */
    public NoStudentInfluenceStrategy(InfluenceStrategy influenceStrategy, RealmType studentType) {
        this.influenceStrategy = influenceStrategy;
        this.studentType = studentType;
    }

    /**
     * Return the influence of the player on the island
     * @param island the island on which the influence is calculated
     * @param player the player that has the returned influence on the island
     * @return the influence of the player on the island
     */
    @Override
    public int getInfluence (Island island, Player player) {
        int toSubtract = player.getSchool().isProfessorPresent(studentType) ? island.getNumStudentsOfType(studentType) : 0;
        return influenceStrategy.getInfluence(island, player) - toSubtract;
    }

    //For testing
    public RealmType getStudentType() {
        return studentType;
    }
}
