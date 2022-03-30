package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.RealmType;

import java.util.ArrayList;
import java.util.List;

public abstract class ProfessorPresenceObservable {
    List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(RealmType studentType) {
        for (ModelObserver o: observers) {
            o.updateProfessorPresence(studentType);
        }
    }
}
