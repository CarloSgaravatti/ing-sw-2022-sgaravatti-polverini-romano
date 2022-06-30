package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 * CharacterListener is a PropertyChangeListener that listen to CharacterCard objects in order to inform clients for
 * events that regard characters.
 *
 * @see java.beans.PropertyChangeListener
 */
public class CharacterListener implements PropertyChangeListener {
    private final RemoteView remoteView;

    /**
     * Constructs a CharacterListener that will forward events to the specified remote view
     *
     * @param remoteView the remote view to which message will be forwarded
     */
    public CharacterListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    /**
     * Respond to the event fired by a character that describe what property has changed after playing a character
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "PlayCharacter" -> onCharacterPlay((Integer) evt.getSource(), (String) evt.getNewValue(), (Boolean) evt.getOldValue());
            case "Students" -> onStudentsChange(((CharacterCard)evt.getSource()).getId(), (Student[]) evt.getNewValue());
            case "EntranceSwap" -> onEntranceSwap(((CharacterCard) evt.getSource()).getPlayerActive().getNickName(),
                    (RealmType[]) evt.getOldValue(), (RealmType[]) evt.getNewValue());
            case "SchoolSwap" -> onSchoolSwap(((CharacterCard) evt.getSource()).getPlayerActive().getNickName(),
                    (RealmType[]) evt.getOldValue(), (RealmType[]) evt.getNewValue());
            case "NoEntryTile" -> onNoEntryTileUpdate(((CharacterCard)evt.getSource()).getId(), (Integer) evt.getNewValue());
        }
    }

    /**
     * Forwards a PlayCharacter message to the remote view after a character with the specified id was played by the
     * specified player. The message will also inform the client if a coin is inserted on the character
     *
     * @param characterId the id of the character
     * @param namePlayer the name of the player who has played the character
     * @param isWithCoinsIncrement true if a coin is putted on the character, otherwise false
     */
    private void onCharacterPlay(int characterId, String namePlayer, boolean isWithCoinsIncrement) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CharacterId", characterId);
        messagePayload.setAttribute("PlayerName", namePlayer);
        messagePayload.setAttribute("IsWithCoinUpdate", isWithCoinsIncrement);
        remoteView.sendMessage(messagePayload, "CharacterPlayed", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forward a CharacterStudents message that will inform the client that the students of the character with the specified
     * id have changed.
     *
     * @param characterId the id of the character
     * @param students the new students of the character
     */
    private void onStudentsChange(int characterId, Student[] students) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterId);
        RealmType[] realms = Arrays.stream(students).map(Student::getStudentType).toList().toArray(new RealmType[0]);
        payload.setAttribute("Students", realms);
        remoteView.sendMessage(payload, "CharacterStudents", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forward a SchoolSwap message that will inform the client that the specified player has played character 10 and has
     * swapped the specified students from entrance and from dining room.
     *
     * @param playerName the name of the player
     * @param toEntrance the students moved from the entrance to the dining room of the player
     * @param toDiningRoom the students moved from the dining room to the entrance of the player
     */
    private void onSchoolSwap(String playerName, RealmType[] toEntrance, RealmType[] toDiningRoom) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", playerName);
        payload.setAttribute("ToEntrance", toEntrance);
        payload.setAttribute("ToDiningRoom", toDiningRoom);
        remoteView.sendMessage(payload, "SchoolSwap", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards a NoEntryTileUpdate message that will inform the client that a no entry tile was added to the specified
     * island from a character that have the specified id
     *
     * @param characterId the id of the character
     * @param islandId the id of the island
     */
    private void onNoEntryTileUpdate(int characterId, int islandId) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterId);
        payload.setAttribute("IslandId", islandId);
        remoteView.sendMessage(payload, "NoEntryTileUpdate", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards a EntranceSwap message that will inform the client that a player with the specified name have played
     * character 7 by inserting the specified students in the entrance and also removing the specified students from
     * the entrance.
     *
     * @param playerName the name of the player
     * @param removed students that are removed from the entrance
     * @param inserted students that are inserted in the entrance
     */
    private void onEntranceSwap(String playerName, RealmType[] removed, RealmType[] inserted) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", playerName);
        payload.setAttribute("Inserted", inserted);
        payload.setAttribute("Removed", removed);
        remoteView.sendMessage(payload, "EntranceSwap", ServerMessageType.GAME_UPDATE);
    }
}
