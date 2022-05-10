package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CharacterListener implements PropertyChangeListener {

    private final RemoteView remoteView;

    public CharacterListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    private void onCharacterPlay(int characterId, String namePlayer) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CharacterId", characterId);
        messagePayload.setAttribute("NamePlayer", namePlayer);
        remoteView.sendMessage(messagePayload, "CharacterPlayed", ServerMessageType.GAME_UPDATE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "PlayCharacter" -> onCharacterPlay((Integer) evt.getNewValue(), (String) evt.getSource());
            //... TODO
        }
    }
}
