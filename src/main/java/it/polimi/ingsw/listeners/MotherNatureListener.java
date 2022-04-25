package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.server.RemoteView;

public class MotherNatureListener implements ModelListener {
    private final RemoteView remoteView;

    public MotherNatureListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    public void eventPerformed(int initialPosition, int finalPosition){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("initialPosition", initialPosition);
        messagePayload.setAttribute("finalPosition", finalPosition);
    }
}
