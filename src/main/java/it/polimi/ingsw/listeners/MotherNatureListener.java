package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MotherNatureListener implements PropertyChangeListener {
    private final RemoteView remoteView;

    public MotherNatureListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("initialPosition", evt.getOldValue());
        messagePayload.setAttribute("finalPosition", evt.getNewValue());
        remoteView.sendMessage(messagePayload,"MotherNatureMovement", ServerMessageType.GAME_UPDATE);
    }
}
