package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.simpleModel.SimplePlayer;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Optional;

public class ExpertGameMessageHandler extends BaseMessageHandler {
    private static final List<String> messageHandled =
            List.of("CharacterPlayed", "CharacterStudents", "NoEntryTileUpdate", "SchoolSwap",
                    "EntranceSwap", "CoinsUpdate", "MotherNatureMovementIncrement");
    private final PropertyChangeSupport userInterface = new PropertyChangeSupport(this);

    public ExpertGameMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
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
        System.out.println("Received " + header.getMessageName());
        switch (header.getMessageName()) {
            case "CharacterPlayed" -> onCharacterPlayed(payload);
            case "CharacterStudents" -> onCharacterStudents(payload);
            case "NoEntryTileUpdate" -> onNoEntryTileUpdate(payload);
            case "SchoolSwap" -> onSchoolSwap(payload);
            case "EntranceSwap" -> onEntranceSwap(payload);
            case "CoinsUpdate" -> onCoinsUpdate(payload);
            case "MotherNatureMovementIncrement" -> onMotherNatureMovementIncrement(payload);
        }
    }

    private void onCharacterPlayed(MessagePayload payload) {
        //TODO
    }

    private void onCharacterStudents(MessagePayload payload) {
        int characterId = payload.getAttribute("CharacterId").getAsInt();
        RealmType[] newStudents = (RealmType[]) payload.getAttribute("Students").getAsObject();
        getModelView().getField().getExpertField().updateCharacterStudents(characterId, newStudents);
        userInterface.firePropertyChange("CharacterStudents", null, characterId);
    }

    private void onNoEntryTileUpdate(MessagePayload payload) {
        int islandId = payload.getAttribute("IslandId").getAsInt();
        ExpertFieldView expertField = getModelView().getField().getExpertField();
        expertField.updateIslandNoEntryTiles(expertField.getNoEntryTilesOnIsland(islandId) + 1, islandId);
        userInterface.firePropertyChange("NoEntryTileUpdate", null, islandId);
    }

    private void onSchoolSwap(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] toEntrance = (RealmType[]) payload.getAttribute("ToEntrance").getAsObject();
        RealmType[] toDiningRoom = (RealmType[]) payload.getAttribute("ToDiningRoom").getAsObject();
        PlayerView playerView = getModelView().getPlayers().get(playerName);
        playerView.updateEntrance(toEntrance, true);
        playerView.updateDiningRoom(toDiningRoom, true);
        playerView.updateEntrance(toDiningRoom, false);
        playerView.updateDiningRoom(toEntrance, false);
        userInterface.firePropertyChange("SchoolSwap", null, playerName);
    }

    private void onEntranceSwap(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] inserted = (RealmType[]) payload.getAttribute("Inserted").getAsObject();
        RealmType[] removed = (RealmType[]) payload.getAttribute("Removed").getAsObject();
        PlayerView playerView = getModelView().getPlayers().get(playerName);
        playerView.updateEntrance(inserted, true);
        playerView.updateEntrance(removed, false);
        userInterface.firePropertyChange("EntranceSwap", null, playerName);
    }

    private void onCoinsUpdate(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        int oldCoins = payload.getAttribute("OldCoins").getAsInt(); //TODO decide if useful
        int newCoins = payload.getAttribute("NewCoins").getAsInt();
        getModelView().getPlayers().get(playerName).updateCoins(newCoins);
        userInterface.firePropertyChange("CoinsUpdate", null, playerName);
    }

    private void onMotherNatureMovementIncrement(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        int increment = payload.getAttribute("MovementIncrement").getAsInt();
        Pair<Integer, Integer> lastAssistantValues = getModelView().getPlayers().get(playerName).getLastPlayedAssistant();
        getModelView().getPlayers().get(playerName).updateLastPlayedAssistant(lastAssistantValues.getFirst(),
                lastAssistantValues.getSecond() + increment);
    }
}
