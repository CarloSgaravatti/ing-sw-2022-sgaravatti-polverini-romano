package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

public class CharacterListener implements ModelListener {

    private final RemoteView remoteView;

    public CharacterListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    public void eventPerformed(int characterId, String namePlayer) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CharacterId", characterId);
        messagePayload.setAttribute("NamePlayer",namePlayer);
        remoteView.sendMessage(messagePayload, "CharacterPlayed", ServerMessageType.GAME_UPDATE);
    }
}
