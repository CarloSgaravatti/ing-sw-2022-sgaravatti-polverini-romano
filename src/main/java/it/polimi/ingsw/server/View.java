package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.messages.MessageFromClient;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class View {
    private final PropertyChangeSupport controllerListeners = new PropertyChangeSupport(this);

    public View(GameController gameController) {
        addListener("ActionMessage", gameController);
        addListener("SetupMessage", gameController.getInitController());
    }

    protected void addListener(String propertyName, PropertyChangeListener listener) {
        controllerListeners.addPropertyChangeListener(propertyName, listener);
    }

    protected void fireSetupMessageEvent(MessageFromClient message) {
        controllerListeners.firePropertyChange("SetupMessage", null, message);
    }

    protected void fireActionMessageEvent(MessageFromClient message) {
        controllerListeners.firePropertyChange("ActionMessage", null, message);
    }
}
