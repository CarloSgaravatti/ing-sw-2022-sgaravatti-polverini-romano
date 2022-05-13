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

    private void onStudentsChange(int characterId, Student[] students) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Character", characterId);
        RealmType[] realms = Arrays.stream(students).map(Student::getStudentType).toList().toArray(new RealmType[0]);
        payload.setAttribute("Students", realms);
        remoteView.sendMessage(payload, "CharacterStudents", ServerMessageType.GAME_UPDATE);
    }

    private void onStudentsSwap(CharacterCard character, RealmType[] fromSource, RealmType[] toSource, boolean fromEntrance, boolean toDiningRoom) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", character.getId());
        payload.setAttribute("PlayerInvolved", character.getPlayerActive().getNickName());
        payload.setAttribute("StudentsFromSource", fromSource);
        payload.setAttribute("IsFromEntrance", fromEntrance);
        payload.setAttribute("StudentsToSource", toSource);
        payload.setAttribute("IsToDiningRoom", toDiningRoom);
        remoteView.sendMessage(payload, "StudentsSwap", ServerMessageType.GAME_UPDATE);
    }

    private void onNoEntryTileUpdate(int characterId, int islandId) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterId);
        payload.setAttribute("IslandId", islandId);
        remoteView.sendMessage(payload, "NoEntryTileUpdate", ServerMessageType.GAME_UPDATE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "PlayCharacter" -> onCharacterPlay((Integer) evt.getNewValue(), (String) evt.getSource());
            case "Students" -> onStudentsChange(((CharacterCard)evt.getSource()).getId(), (Student[]) evt.getNewValue());
            case "SwapFromEntrance" -> onStudentsSwap((CharacterCard) evt.getSource(),
                    (RealmType[]) evt.getNewValue(), (RealmType[]) evt.getOldValue(), true, false);
            case "SchoolSwap" -> onStudentsSwap((CharacterCard) evt.getSource(),
                    (RealmType[]) evt.getNewValue(), (RealmType[]) evt.getOldValue(), true, true);
            case "NoEntryTile" -> onNoEntryTileUpdate(((CharacterCard)evt.getSource()).getId(), (Integer) evt.getNewValue());
        }
    }
}
