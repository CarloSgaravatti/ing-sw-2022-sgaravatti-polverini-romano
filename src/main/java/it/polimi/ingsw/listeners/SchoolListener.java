package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.RealmType;
import it.polimi.ingsw.server.RemoteView;

public class SchoolListener implements ModelListener {
    private final RemoteView remoteView;

    public SchoolListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    public void eventPerformed(RealmType type, String namePlayer){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("professorType", type);
        messagePayload.setAttribute("playerName", namePlayer);
    }

}
