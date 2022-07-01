package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.ModelObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * ModelObservable is the observable part of the Observer pattern, where the observed property is the position of
 * mother nature in the islands
 */
public abstract class MotherNatureMovementObservable {
    /**
     * The list of observers
     */
    private final transient List<ModelObserver> observers;

    /**
     * Constructs a MotherNatureMovementObservable that will have no observers listening to the instance
     */
    public MotherNatureMovementObservable() {
        observers = new ArrayList<>();
    }

    /**
     * Add an observer to the observer list
     *
     * @param observer the observer to add
     */
    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies the observers when mother nature is added to the specified island
     *
     * @param island the island on which mother nature is added
     */
    public void notifyObservers(Island island) {
        for (ModelObserver o: observers) {
            o.updateIslandTower(island);
        }
    }
}
