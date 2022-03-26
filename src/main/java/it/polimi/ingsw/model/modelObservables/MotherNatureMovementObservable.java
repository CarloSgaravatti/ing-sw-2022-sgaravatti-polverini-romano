package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.RealmType;

import java.util.List;

public abstract class MotherNatureMovementObservable {
    List<ModelObserver> observers;

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Island island) {
        for (ModelObserver o: observers) {
            o.updateIslandTower(island);
            o.updateIslandUnification(island);
        }
    }
}
