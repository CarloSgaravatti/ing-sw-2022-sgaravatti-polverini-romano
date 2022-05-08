package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.School;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class ProfessorPresenceObservable is the subject part of the observer pattern, that is
 * observed by a ModelObserver. The observer is notified whenever a student is putted on a school
 * dining room of a player, in order to update the professor presence at the professor table.
 * @see ModelObserver
 * @see School
 */
public abstract class ProfessorPresenceObservable {
    /**
     * The list of observers
     */
    private final transient List<ModelObserver> observers = new ArrayList<>();

    /**
     * Add an observer to the observer list
     * @param observer the observer to add
     */
    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    /**
     * Method notifyObservers calls the update of the professor presence in all players schools
     * for the specified professor's RealmType
     * @param professorType the RealmType of the professor to update
     */
    public void notifyObservers(RealmType professorType) {
        for (ModelObserver o: observers) {
            o.updateProfessorPresence(professorType);
        }
    }
}
