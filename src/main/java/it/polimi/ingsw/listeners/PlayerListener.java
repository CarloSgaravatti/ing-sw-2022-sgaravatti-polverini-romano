package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.Assistant;
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
    private static final String COINS = "Coins";
    private static final String MOTHER_NATURE_MOVE = "MotherNatureMovementIncrement";
    private final RemoteView view;

    public PlayerListener(RemoteView view) {
        this.view = view;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ASSISTANT -> onAssistantPlay((Assistant) evt.getNewValue(), (String) evt.getSource());
            //TODO: return to this after fixing school
            /*case DINING_ROOM_INSERTION -> onDiningRoomChange(
                    ((Player) evt.getSource()).getNickName(), (RealmType[]) evt.getNewValue(), true);
            case DINING_ROOM_REMOVAL -> onDiningRoomChange(
                    ((Player) evt.getSource()).getNickName(), (RealmType[]) evt.getNewValue(), false);*/
            case DINING_ROOM_INSERTION -> onDiningRoomChange(
                    ((Player) evt.getSource()).getNickName(), (RealmType) evt.getNewValue(), true);
            case DINING_ROOM_REMOVAL -> onDiningRoomChange(
                    ((Player) evt.getSource()).getNickName(), (RealmType) evt.getNewValue(), false);
            case PROFESSOR -> onProfessorUpdate(((Player)evt.getSource()).getNickName(), (RealmType) evt.getNewValue());
            case COINS -> onCoinsUpdate((String) evt.getSource(), (Integer) evt.getOldValue(), (Integer) evt.getNewValue());
            case MOTHER_NATURE_MOVE -> onMotherNatureMovementIncrement(((Player)evt.getSource()).getNickName(), (Integer) evt.getNewValue());
        }
    }

    private void onAssistantPlay(Assistant assistant, String playerName) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("AssistantId", assistant.getCardValue());
        messagePayload.setAttribute("MotherNatureMovement", assistant.getMotherNatureMovement());
        messagePayload.setAttribute("PlayerName", playerName);
        view.sendBroadcast(messagePayload,"AssistantPlayed", ServerMessageType.GAME_UPDATE);
    }

    //TODO: protocol specify another argument, but maybe is not useful
    private void onDiningRoomChange(String nickname, RealmType/*[]*/ students, boolean isInsertion) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("Students", students);
        messagePayload.setAttribute("PlayerName", nickname);
        messagePayload.setAttribute("IsInsertion", isInsertion);
        view.sendBroadcast(messagePayload,"SchoolDiningRoomUpdate", ServerMessageType.GAME_UPDATE);
    }

    private void onProfessorUpdate(String nickname, RealmType professor) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("ProfessorType", professor);
        messagePayload.setAttribute("PlayerName", nickname);
        view.sendBroadcast(messagePayload,"ProfessorUpdate", ServerMessageType.GAME_UPDATE);
    }

    private void onCoinsUpdate(String nickname, int oldCoins, int newCoins) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("PlayerName", nickname);
        messagePayload.setAttribute("OldCoins", oldCoins);
        messagePayload.setAttribute("NewCoins", newCoins);
        view.sendBroadcast(messagePayload,"CoinsUpdate", ServerMessageType.GAME_UPDATE);
    }

    private void onMotherNatureMovementIncrement(String nickname, int increment) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("PlayerName", nickname);
        messagePayload.setAttribute("MovementIncrement", increment);
        view.sendBroadcast(messagePayload,"MotherNatureMovementIncrement", ServerMessageType.GAME_UPDATE);
    }
}
