package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.ModelObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class MotherNatureMovementObservable {
    /**
     * The list of observers
     */
    private final transient List<ModelObserver> observers;

    public MotherNatureMovementObservable() {
        observers = new ArrayList<>();
    }

    /**
     * Add an observer to the observer list
     * @param observer the observer to add
     */
    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Island island) {
        for (ModelObserver o: observers) {
            o.updateIslandTower(island);
        }
    }
}
