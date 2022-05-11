package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PlayerListener implements PropertyChangeListener {
    private static final String DINING_ROOM_REMOVAL = "DiningRoomRem";
    private static final String DINING_ROOM_INSERTION = "DiningRoomIns";
    private static final String ASSISTANT = "Assistant";
    private static final String PROFESSOR = "Professor";
    private final RemoteView view;

    public PlayerListener(RemoteView view) {
        this.view = view;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ASSISTANT -> onAssistantPlay((Integer) evt.getNewValue(), (String) evt.getSource());
            case DINING_ROOM_INSERTION -> onDiningRoomChange(
                    ((Player) evt.getSource()).getNickName(), (RealmType[]) evt.getNewValue(), true);
            case DINING_ROOM_REMOVAL -> onDiningRoomChange(
                    ((Player) evt.getSource()).getNickName(), (RealmType[]) evt.getNewValue(), false);
            case PROFESSOR -> onProfessorUpdate(((Player)evt.getSource()).getNickName(), (RealmType) evt.getNewValue());
        }
    }

    private void onAssistantPlay(int assistantIdx, String playerName) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("AssistantId", assistantIdx);
        messagePayload.setAttribute("PlayerName", playerName);
        view.sendBroadcast(messagePayload,"AssistantPlayed", ServerMessageType.GAME_SETUP);
    }

    //TODO: protocol specify another argument, but maybe is not useful
    private void onDiningRoomChange(String nickname, RealmType[] students, boolean isInsertion) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("Students", students);
        messagePayload.setAttribute("PlayerName", nickname);
        messagePayload.setAttribute("IsInsertion", isInsertion);
        view.sendBroadcast(messagePayload,"SchoolDiningRoomUpdate", ServerMessageType.GAME_SETUP);
    }

    private void onProfessorUpdate(String nickname, RealmType professor) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("ProfessorType", professor);
        messagePayload.setAttribute("PlayerName", nickname);
        view.sendBroadcast(messagePayload,"ProfessorUpdate", ServerMessageType.GAME_SETUP);
    }
}
