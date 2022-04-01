package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.ModelObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class MotherNatureMovementObservable {
    List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Island island) {
        for (ModelObserver o: observers) {
            o.updateIslandTower(island);
        }
    }
}
