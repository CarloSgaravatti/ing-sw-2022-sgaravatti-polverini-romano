package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.ClientMessageType;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.enumerations.*;
import it.polimi.ingsw.server.RemoteView;

public class PlayerListener implements ModelListener {
    private final RemoteView remoteView;

    public PlayerListener(RemoteView remoteView){
        this.remoteView = remoteView;
    }

    public void eventPerformed(TowerType type, String playerName){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("towerType", type);
        messagePayload.setAttribute("playerName", playerName);
    }
    public void eventPerformedAssistant(int assistantIdx) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("assistantId", assistantIdx);
    }
}
