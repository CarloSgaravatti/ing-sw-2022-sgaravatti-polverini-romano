package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.messages.MessageFromClient;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Abstract class View represent the view part of the MVC pattern; the class is responsible to inform the controllers
 * when an event that regard the view (without knowing that the real view is a remote client).
 */
public abstract class View {
    private final PropertyChangeSupport controllerListeners = new PropertyChangeSupport(this);

    /**
     * Construct a new View that will have the specified controller as listener. The game controller will listen to the
     * "ActionMessage" property and the init controller contained in the game controller will listen to the
     * "SetupMessage" property.
     *
     * @param gameController the controller of the game
     */
    public View(GameController gameController) {
        addListener("ActionMessage", gameController);
        addListener("SetupMessage", gameController.getInitController());
    }

    /**
     * Adds a PropertyChangeListener to the object, that will listen the specified property
     *
     * @param propertyName the name of the property
     * @param listener the listener that will listen the property
     */
    protected void addListener(String propertyName, PropertyChangeListener listener) {
        controllerListeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Fire an event on the property name "SetupMessage", that will contain the specified message as the new value
     *
     * @param message the message that will be notified to the controllers
     */
    protected void fireSetupMessageEvent(MessageFromClient message) {
        controllerListeners.firePropertyChange("SetupMessage", null, message);
    }

    /**
     * Fire an event on the property name "ActionMessage", that will contain the specified message as the new value
     *
     * @param message the message that will be notified to the controllers
     */
    protected void fireActionMessageEvent(MessageFromClient message) {
        controllerListeners.firePropertyChange("ActionMessage", null, message);
    }
}
