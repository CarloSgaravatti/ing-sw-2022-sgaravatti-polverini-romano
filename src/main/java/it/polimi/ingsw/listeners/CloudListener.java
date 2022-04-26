package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;


public class CloudListener implements ModelListener{
    private final RemoteView remoteView;

    public CloudListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    public void eventPerformed(int cloudIndex, String namePlayer){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CloudIndex", cloudIndex);
        messagePayload.setAttribute("NamePlayer",namePlayer);
        remoteView.sendMessage(messagePayload, "PickFromCloud", ServerMessageType.GAME_UPDATE);
    }
}
