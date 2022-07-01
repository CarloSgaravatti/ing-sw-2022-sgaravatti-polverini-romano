package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.io.Serializable;

/**
 * Interface ModelObserver defines some methods to automatically update some game aspects
 * in response to some changes that are caused by player actions.
 *
 * @see it.polimi.ingsw.model.modelObservables.MotherNatureMovementObservable
 * @see it.polimi.ingsw.model.modelObservables.ProfessorPresenceObservable
 * @see it.polimi.ingsw.model.modelObservables.StudentContainerObservable
 */
public interface ModelObserver {
    /**
     * Method updateProfessorPresence checks if there are some changes to do in the professor
     * tables of each player for the specified professor.
     *
     * @param studentType the Realm Type of the professor to udate
     */
    void updateProfessorPresence(RealmType studentType);

    /**
     * Method updateIslandTower checks if a player has conquered an island when mother nature
     * is moved on that island. If a player has conquered the specified island, towers from his
     * school are putted on the island and the towers that were present on the island before
     * (if there are already some towers on the island) are returned to the original school.
     *
     * @param island the island that needs to be updated
     */
    void updateIslandTower(Island island);

    /**
     * Method updateIslandUnification checks if the specified island can be merged with its island
     * neighbours, to form an island group, when there is an update in the specified island tower.
     * The neighbours islands that have the same tower of the specified island are merged in an island
     * group.
     *
     * @param island the island that have had a tower update
     */
    void updateIslandUnification(Island island);

    /**
     * Method updateStudentsContainer inserts a student in the specified student container in response
     * to a student pick from the student container from a player
     *
     * @param studentContainer the student container to update
     */
    void updateStudentContainer(StudentContainer studentContainer);
}
