package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * PlayerListener is a PropertyChangeListener that listen to a Player instance and is associated the RemoteView that
 * represent the player. Message will be forwarded to the remote view, that will broadcast them to all other remote views
 * of the game.
 *
 * @see java.beans.PropertyChangeListener
 */
public class PlayerListener implements PropertyChangeListener {
    private static final String DINING_ROOM_REMOVAL = "DiningRoomRem";
    private static final String DINING_ROOM_INSERTION = "DiningRoomIns";
    private static final String ASSISTANT = "Assistant";
    private static final String PROFESSOR = "Professor";
    private static final String COINS = "Coins";
    private static final String MOTHER_NATURE_MOVE = "MotherNatureMovementIncrement";
    private final RemoteView view;

    /**
     * Constructs a new PlayerListener associated to the specified remote view
     *
     * @param view the remote view to which message will be forwarded
     */
    public PlayerListener(RemoteView view) {
        this.view = view;
    }

    /**
     * Responds to an event that specify what have changed in the player instance that the listener is listening
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ASSISTANT -> onAssistantPlay((Assistant) evt.getNewValue(), (String) evt.getSource());
            case DINING_ROOM_INSERTION -> onDiningRoomChange(((Player) evt.getSource()).getNickName(),
                    (RealmType[]) evt.getNewValue(), true, (Boolean) evt.getOldValue());
            case DINING_ROOM_REMOVAL -> onDiningRoomChange(((Player) evt.getSource()).getNickName(),
                    (RealmType[]) evt.getNewValue(), false, false);
            case PROFESSOR -> onProfessorUpdate(((Player)evt.getSource()).getNickName(), (RealmType) evt.getNewValue());
            case COINS -> onCoinsUpdate((String) evt.getSource(), (Integer) evt.getOldValue(), (Integer) evt.getNewValue());
            case MOTHER_NATURE_MOVE -> onMotherNatureMovementIncrement(((Player)evt.getSource()).getNickName(), (Integer) evt.getNewValue());
        }
    }

    /**
     * Forwards an AssistantPlayed message to the remote view after tha player has played the specified assistant
     *
     * @param assistant the assistant that was played
     * @param playerName the name of the player
     */
    private void onAssistantPlay(Assistant assistant, String playerName) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("AssistantId", assistant.getCardValue());
        messagePayload.setAttribute("MotherNatureMovement", assistant.getMotherNatureMovement());
        messagePayload.setAttribute("PlayerName", playerName);
        view.sendBroadcast(messagePayload,"AssistantPlayed", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards a SchoolDiningRoomUpdate message to the remote view that will inform clients that the player have
     * changed the dining room students by inserting or removing the specified students
     *
     * @param nickname the nickname of the player
     * @param students the students that were inserted/removed
     * @param isInsertion true if students are inserted, otherwise false
     * @param isFromEntrance true if students come from the entrance, otherwise false
     */
    private void onDiningRoomChange(String nickname, RealmType[] students, boolean isInsertion, boolean isFromEntrance) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("Students", students);
        messagePayload.setAttribute("PlayerName", nickname);
        messagePayload.setAttribute("IsInsertion", isInsertion);
        messagePayload.setAttribute("IsFromEntrance", isFromEntrance);
        view.sendBroadcast(messagePayload,"SchoolDiningRoomUpdate", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards a ProfessorUpdate message to the remote view that will inform clients that the specified professor has
     * been taken by the player.
     *
     * @param nickname the name of the player
     * @param professor the professor that was taken
     */
    private void onProfessorUpdate(String nickname, RealmType professor) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("ProfessorType", professor);
        messagePayload.setAttribute("PlayerName", nickname);
        view.sendBroadcast(messagePayload,"ProfessorUpdate", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards a CoinsUpdate message that will inform clients that the coins of the player have changed
     *
     * @param nickname the name of the player
     * @param oldCoins the previous coins of the players
     * @param newCoins the new coins of the players
     */
    private void onCoinsUpdate(String nickname, int oldCoins, int newCoins) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("PlayerName", nickname);
        messagePayload.setAttribute("OldCoins", oldCoins);
        messagePayload.setAttribute("NewCoins", newCoins);
        view.sendBroadcast(messagePayload,"CoinsUpdate", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards a MotherNatureMovementIncrement that will inform clients that the player has played character 4, and
     * therefore he can move mother nature by two additional spaces.
     *
     * @param nickname the name of the player
     * @param increment the increment of the mother nature movement
     */
    private void onMotherNatureMovementIncrement(String nickname, int increment) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("PlayerName", nickname);
        messagePayload.setAttribute("MovementIncrement", increment);
        view.sendBroadcast(messagePayload,"MotherNatureMovementIncrement", ServerMessageType.GAME_UPDATE);
    }
}
