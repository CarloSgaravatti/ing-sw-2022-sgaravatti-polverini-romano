package it.polimi.ingsw.model.modelObservables;

import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class StudentContainerObservable is the subject part of the observer pattern, where
 * the observer is the ModelObserver. This class notifies the observers when a student is removed
 * from a StudentContainer, so it has to be replaced.
 * @see ModelObserver
 * @see StudentContainer
 */
public abstract class StudentContainerObservable {
    /**
     * The list of observers
     */
    private final transient List<ModelObserver> observers;

    /**
     * Constructs a StudentContainerObservable that will have no observers listening to the instance
     */
    public StudentContainerObservable() {
        observers = new ArrayList<>();
    }

    /**
     * Add an observer to the observer list
     * @param observer the observer to add
     */
    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    /**
     * Method notifyObservers calls the update of the specified student container, in order to
     * insert a student in it.
     * @param studentContainer the StudentContainer to update
     */
    public void notifyObservers(StudentContainer studentContainer) {
        for (ModelObserver o: observers) {
            o.updateStudentContainer(studentContainer);
        }
    }
}
