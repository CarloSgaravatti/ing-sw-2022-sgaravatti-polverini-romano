package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.server.RemoteView;


public class CloudListener implements ModelListener{
    private final RemoteView remoteView;

    public CloudListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    //TODO: missing method of remot view for sending the message
    //  and missing header name

    public void eventPerformed(int cloudIndex, String namePlayer){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CloudIndex", cloudIndex);
        messagePayload.setAttribute("Name Player",namePlayer);
    }
}
