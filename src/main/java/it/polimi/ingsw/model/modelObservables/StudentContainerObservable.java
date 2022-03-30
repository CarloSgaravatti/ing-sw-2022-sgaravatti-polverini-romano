package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class StudentContainerObservable {
    List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(StudentContainer studentContainer) {
        for (ModelObserver o: observers) {
            o.updateStudentContainer(studentContainer);
        }
    }
}
