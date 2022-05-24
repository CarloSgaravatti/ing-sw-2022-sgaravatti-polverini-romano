package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.simpleModel.SimplePlayer;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Optional;

public class CharacterMessageHandler extends BaseMessageHandler {
    private static final List<String> messageHandled =
            List.of("CharacterPlayed", "CharacterStudents", "NoEntryTileUpdate", "StudentSwap");
    private final PropertyChangeSupport userInterface = new PropertyChangeSupport(this);

    public CharacterMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
        this.userInterface.addPropertyChangeListener(userInterface);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if(header.getMessageType() != ServerMessageType.GAME_UPDATE || !messageHandled.contains(header.getMessageName())) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch (header.getMessageName()) {
            case "CharacterPlayed" -> onCharacterPlayed(payload);
            case "CharacterStudents" -> onCharacterStudents(payload);
            case "NoEntryTileUpdate" -> onNoEntryTileUpdate(payload);
            case "StudentSwap" -> onStudentSwap(payload);
        }
    }

    private void onCharacterPlayed(MessagePayload payload) {
        //TODO
    }

    private void onCharacterStudents(MessagePayload payload) {
        int characterId = payload.getAttribute("CharacterId").getAsInt();
        RealmType[] newStudents = (RealmType[]) payload.getAttribute("Students").getAsObject();
        getModelView().getField().getExpertField().updateCharacterStudents(characterId, newStudents);

        //TODO
    }

    private void onNoEntryTileUpdate(MessagePayload payload) {
        int islandId = payload.getAttribute("IslandId").getAsInt();
        ExpertFieldView expertField = getModelView().getField().getExpertField();
        expertField.updateIslandNoEntryTiles(expertField.getNoEntryTilesOnIsland(islandId) + 1, islandId);
        userInterface.firePropertyChange("NoEntryTileUpdate", null, islandId);
    }

    private void onStudentSwap(MessagePayload payload) {
        //TODO: decide what to do with these (if they are useful)
        String playerName = payload.getAttribute("PlayerInvolved").getAsString();
        boolean isFromEntrance = payload.getAttribute("IsFromEntrance").getAsBoolean();
        boolean isToDiningRoom = payload.getAttribute("IsToDiningRoom").getAsBoolean();
        RealmType[] fromSource = (RealmType[]) payload.getAttribute("StudentsFromSource").getAsObject();
        RealmType[] toSource = (RealmType[]) payload.getAttribute("StudentsToSource").getAsObject();

        int characterId = payload.getAttribute("CharacterId").getAsInt();
        Optional<MessageAttribute> newCharacterStudentsAttribute = Optional.of(payload.getAttribute("NewCharacterStudents"));
        newCharacterStudentsAttribute.ifPresent(n ->
            getModelView().getField().getExpertField().updateCharacterStudents(characterId, (RealmType[]) n.getAsObject())
        );
        SimplePlayer newSchool = (SimplePlayer) payload.getAttribute("NewSchool").getAsObject();
        getModelView().getPlayers().get(newSchool.getNickname()).resetStudentsTo(newSchool.getEntrance(), newSchool.getDiningRoom());

        //TODO
    }
}
