package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MotherNatureListener is a PropertyChangeListener that listen to the Game in order to know when mother nature is moved
 * by a player
 *
 * @see java.beans.PropertyChangeListener
 */
public class MotherNatureListener implements PropertyChangeListener {
    private final RemoteView remoteView;

    /**
     * Constructs a new MotherNatureListener that will forward mother nature movements to the specified remote view
     *
     * @param remoteView the remote view of a client
     */
    public MotherNatureListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    /**
     * Responds to an event that contains the update of the mother nature position
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("InitialPosition", evt.getOldValue());
        messagePayload.setAttribute("FinalPosition", evt.getNewValue());
        remoteView.sendMessage(messagePayload,"MotherNatureMovement", ServerMessageType.GAME_UPDATE);
    }
}
