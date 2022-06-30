package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * ErrorDispatcher is a PropertyChangeListener that will listen to controller objects in order to catch events that are
 * fired when a client sends a malformed action. The errors will be dispatched to the correct client, with the help of
 * a RemoteView.
 *
 * @see java.beans.PropertyChangeListener
 */
public class ErrorDispatcher implements PropertyChangeListener {
    private final List<RemoteView> clients;

    /**
     * Constructs a new ErrorDispatcher that will be associated to the specified remote views
     *
     * @param views the remote views of the clients
     */
    public ErrorDispatcher(List<RemoteView> views) {
        this.clients = views;
    }

    /**
     * Responds to an event fired after an error as occurred, the event will contain the error committer, the error type
     * and the error description. The method will forward to the error committer that error message
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ErrorMessageType error = (ErrorMessageType) evt.getOldValue();
        String errorCommitter = (String) evt.getSource();
        String errorInfo = (String) evt.getNewValue();
        for (RemoteView view: clients) {
            if (view.getPlayerNickname().equals(errorCommitter)) {
                view.sendError(error, errorInfo);
                return;
            }
        }
    }
}
