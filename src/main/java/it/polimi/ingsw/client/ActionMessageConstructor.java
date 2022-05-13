package it.polimi.ingsw.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActionMessageConstructor implements PropertyChangeListener {
    private final ConnectionToServer connection;

    public ActionMessageConstructor(ConnectionToServer connection) {
        this.connection = connection;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
