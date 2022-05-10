package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.enumerations.*;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SchoolListener implements PropertyChangeListener {
    private final RemoteView remoteView;

    public SchoolListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    public void onProfessorUpdate(RealmType type, String namePlayer){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("professorType", type);
        messagePayload.setAttribute("playerName", namePlayer);
        remoteView.sendMessage(messagePayload,"ProfessorUpdate", ServerMessageType.GAME_UPDATE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "Professor" -> onProfessorUpdate((RealmType) evt.getNewValue(), (String) evt.getSource());
            //... TODO
        }
    }

    //TODO: student in school update
}
