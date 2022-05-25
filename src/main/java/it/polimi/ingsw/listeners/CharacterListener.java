package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

public class CharacterListener implements PropertyChangeListener {

    private final RemoteView remoteView;

    public CharacterListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "PlayCharacter" -> onCharacterPlay(((CharacterCard)evt.getSource()).getId(), ((Player)evt.getNewValue()).getNickName());
            case "Students" -> onStudentsChange(((CharacterCard)evt.getSource()).getId(), (Student[]) evt.getNewValue());
            case "EntranceSwap" -> onEntranceSwap(((CharacterCard) evt.getSource()).getPlayerActive().getNickName(),
                    (RealmType[]) evt.getOldValue(), (RealmType[]) evt.getNewValue());
            case "SchoolSwap" -> onSchoolSwap(((CharacterCard) evt.getSource()).getPlayerActive().getNickName(),
                    (RealmType[]) evt.getOldValue(), (RealmType[]) evt.getNewValue());
            case "NoEntryTile" -> onNoEntryTileUpdate(((CharacterCard)evt.getSource()).getId(), (Integer) evt.getNewValue());
        }
    }

    private void onCharacterPlay(int characterId, String namePlayer) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CharacterId", characterId);
        messagePayload.setAttribute("NamePlayer", namePlayer);
        remoteView.sendMessage(messagePayload, "CharacterPlayed", ServerMessageType.GAME_UPDATE);
    }

    private void onStudentsChange(int characterId, Student[] students) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Character", characterId);
        RealmType[] realms = Arrays.stream(students).map(Student::getStudentType).toList().toArray(new RealmType[0]);
        payload.setAttribute("Students", realms);
        remoteView.sendMessage(payload, "CharacterStudents", ServerMessageType.GAME_UPDATE);
    }

    private void onSchoolSwap(String playerName, RealmType[] toEntrance, RealmType[] toDiningRoom) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", playerName);
        payload.setAttribute("ToEntrance", toEntrance);
        payload.setAttribute("ToDiningRoom", toDiningRoom);
        remoteView.sendMessage(payload, "SchoolSwap", ServerMessageType.GAME_UPDATE);
    }

    private void onNoEntryTileUpdate(int characterId, int islandId) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterId);
        payload.setAttribute("IslandId", islandId);
        remoteView.sendMessage(payload, "NoEntryTileUpdate", ServerMessageType.GAME_UPDATE);
    }

    private void onEntranceSwap(String playerName, RealmType[] removed, RealmType[] inserted) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", playerName);
        payload.setAttribute("Inserted", inserted);
        payload.setAttribute("Removed", removed);
        remoteView.sendMessage(payload, "EntranceSwap", ServerMessageType.GAME_UPDATE);
    }
}
