package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleIsland;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * ExpertGameMessageHandler handles all messages that have GAME_UPDATE as message type and that regard a simple game
 *
 * @see it.polimi.ingsw.client.messageHandlers.MessageHandler
 * @see it.polimi.ingsw.client.messageHandlers.BaseMessageHandler
 */
public class GameUpdateMessageHandler extends BaseMessageHandler {
    private static final List<String> messageHandled =
            List.of("AssistantPlayed", "ProfessorUpdate", "MotherNatureMovement", "SchoolDiningRoomUpdate", "IslandStudentsUpdate",
                    "IslandUnification", "IslandTowerUpdate", "PickFromCloud", "AssistantsUpdate", "CloudsRefill");
    private final PropertyChangeSupport userInterface = new PropertyChangeSupport(this);

    /**
     * Constructs a new GameUpdateMessageHandler that will be associated to the specified connection to the server, user
     * interface and model view
     *
     * @param connection the connection to the server that will pass the messages
     * @param userInterface the user interface of the client
     * @param modelView the model view of the client.
     */
    public GameUpdateMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
        this.userInterface.addPropertyChangeListener(userInterface);
    }

    /**
     * Handles a message that have been arrived from the server
     *
     * @param message the message from the server
     * @see MessageHandler#handleMessage(MessageFromServer)
     */
    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if(header.getMessageType() != ServerMessageType.GAME_UPDATE || !messageHandled.contains(header.getMessageName())) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch (header.getMessageName()) {
            case "AssistantPlayed" -> onAssistantPlayed(payload);
            case "ProfessorUpdate" -> onProfessorUpdate(payload);
            case "MotherNatureMovement" -> onMotherNatureMovement(payload);
            case "SchoolDiningRoomUpdate" -> onSchoolDiningRoomUpdate(payload);
            case "IslandStudentsUpdate" -> onIslandStudentsUpdate(payload);
            case "IslandUnification" -> onIslandUnificationUpdate(payload);
            case "IslandTowerUpdate" -> onIslandTowerUpdate(payload);
            case "PickFromCloud" -> onPickFromCloud(payload);
            case "AssistantsUpdate" -> onAssistantsUpdate(payload);
            case "CloudsRefill" -> onCloudsRefill(payload);
        }
    }

    /**
     * Notifies the user interface after an AssistantUpdate message have arrived and modifies the model view by
     * changing the last played assistant of the player contained in the message
     *
     * @param payload the payload of the message
     */
    private void onAssistantPlayed(MessagePayload payload) {
        int assistant = payload.getAttribute("AssistantId").getAsInt();
        int motherNatureMovement = payload.getAttribute("MotherNatureMovement").getAsInt();
        String playerName = payload.getAttribute("PlayerName").getAsString();
        getModelView().getPlayers().get(playerName).updateLastPlayedAssistant(assistant, motherNatureMovement);
        if (getUserInterface().getNickname().equals(playerName)) getModelView().removeAssistant(assistant);
        userInterface.firePropertyChange("AssistantUpdate", assistant, playerName);
    }

    /**
     * Notifies the user interface after a ProfessorUpdate message have arrived and modifies the model view by updating
     * the professor owner of the realm contained in the payload
     *
     * @param payload the payload of the message
     */
    private void onProfessorUpdate(MessagePayload payload) {
        String newOwner = payload.getAttribute("PlayerName").getAsString();
        RealmType professor = (RealmType) payload.getAttribute("ProfessorType").getAsObject();
        Optional<String> lastOwner = getModelView().getField().updateProfessorOwner(professor, newOwner);
        userInterface.firePropertyChange(new PropertyChangeEvent(professor, "ProfessorUpdate",
                lastOwner.orElse(null), newOwner));
    }

    /**
     * Notifies the user interface after a MotherNatureMovement message have arrived and modifies the model view by updating
     * the mother nature position
     *
     * @param payload the payload of the message
     */
    private void onMotherNatureMovement(MessagePayload payload) {
        int startingPosition = payload.getAttribute("InitialPosition").getAsInt();
        int newMotherNaturePosition = payload.getAttribute("FinalPosition").getAsInt();
        getModelView().getField().updateMotherNaturePosition(newMotherNaturePosition);
        if (getModelView().isExpert() && getModelView().getField().getExpertField().areNoEntryTilesPresents()) {
            ExpertFieldView expertField = getModelView().getField().getExpertField();
            Integer previousNoEntryTiles = expertField.getNoEntryTilesOnIsland(newMotherNaturePosition);
            Integer previousCharacterNoEntryTiles = expertField.getNumNoEntryTilesOnCharacter().getSecond();
            if (previousNoEntryTiles != null && previousNoEntryTiles > 0) {
                expertField.updateIslandNoEntryTiles(previousNoEntryTiles - 1, newMotherNaturePosition);
                expertField.updateNoEntryTilesOnCharacter(previousCharacterNoEntryTiles + 1);
                userInterface.firePropertyChange("NoEntryTileUpdate", 5, newMotherNaturePosition);
            }
        }
        userInterface.firePropertyChange("MotherNatureUpdate", startingPosition, newMotherNaturePosition);
    }

    /**
     * Notifies the user interface after a SchoolDiningRoomUpdate message have arrived and modifies the model view by updating
     * the school of the player contained in the message
     *
     * @param payload the payload of the message
     */
    private void onSchoolDiningRoomUpdate(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        boolean isInsertion = payload.getAttribute("IsInsertion").getAsBoolean();
        getModelView().getPlayers().get(playerName).updateDiningRoom(students, isInsertion);
        boolean isFromEntrance = payload.getAttribute("IsFromEntrance").getAsBoolean();
        if (isInsertion && isFromEntrance) {
            getModelView().getPlayers().get(playerName).updateEntrance(students, false);
        }
        if (isInsertion) {
            userInterface.firePropertyChange(
                    new PropertyChangeEvent(playerName, "DiningRoomInsertion", isFromEntrance, students));
        } else {
            userInterface.firePropertyChange(
                    new PropertyChangeEvent(playerName, "DiningRoomRemoval", null, students));
        }
    }

    /**
     * Notifies the user interface after a IslandStudentsUpdate message have arrived and modifies the model view by updating
     * the students of the island contained in the message
     *
     * @param payload the payload of the message
     */
    private void onIslandStudentsUpdate(MessagePayload payload) {
        int islandId = payload.getAttribute("IslandId").getAsInt();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        boolean isFromEntrance = payload.getAttribute("IsFromEntrance").getAsBoolean();
        if (isFromEntrance) {
            getModelView().getPlayers().get(getModelView().getCurrentActivePlayer()).updateEntrance(students, false);
            userInterface.firePropertyChange("EntranceUpdate", null, getModelView().getCurrentActivePlayer());
        }
        getModelView().getField().updateIslandStudents(islandId, students);
        userInterface.firePropertyChange(
                new PropertyChangeEvent(islandId, "IslandStudentsUpdate", isFromEntrance, students));
    }

    /**
     * Notifies the user interface after a IslandUnification message have arrived and modifies the model view by updating
     * the islands contained in the message
     *
     * @param payload the payload of the message
     */
    private void onIslandUnificationUpdate(MessagePayload payload) {
        Integer[] islandsId = (Integer[]) payload.getAttribute("IslandsId").getAsObject();
        SimpleIsland island = (SimpleIsland) payload.getAttribute("NewIsland").getAsObject();
        getModelView().getField().mergeIslands(Arrays.asList(islandsId), island.getIslandRepresentation());
        userInterface.firePropertyChange("IslandUnification", null, islandsId);
    }

    /**
     * Notifies the user interface after a IslandTowerUpdate message have arrived and modifies the model view by updating
     * the tower of the island contained in the message and the towers of the schools of the players that have
     * conquered or lost the island
     *
     * @param payload the payload of the message
     */
    private void onIslandTowerUpdate(MessagePayload payload) {
        int island = payload.getAttribute("IslandId").getAsInt();
        TowerType previousTower = getModelView().getField().getIsland(island).getThird();
        TowerType tower = (TowerType) payload.getAttribute("TowerType").getAsObject();
        int islandTowers = getModelView().getField().getIsland(island).getSecond();
        getModelView().getField().getIsland(island).setThird(tower);
        String newOwner = payload.getAttribute("NewOwner").getAsString();
        getModelView().getPlayers().get(newOwner)
                .updateNumTowers(getModelView().getPlayers().get(newOwner).getNumTowers() - islandTowers);
        if (payload.getAttribute("PreviousOwner") != null) {
            String previousOwner = payload.getAttribute("PreviousOwner").getAsString();
            getModelView().getPlayers().get(previousOwner)
                    .updateNumTowers(getModelView().getPlayers().get(newOwner).getNumTowers() + islandTowers);
        }
        userInterface.firePropertyChange("IslandTowerUpdate", previousTower, island);
    }

    /**
     * Notifies the user interface after a PickFromCloud message have arrived and modifies the model view by updating
     * the cloud and the entrance of the player contained in the message
     *
     * @param payload the payload of the message
     */
    private void onPickFromCloud(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        int cloudId = payload.getAttribute("CloudId").getAsInt();
        getModelView().getField().resetCloud(cloudId);
        getModelView().getPlayers().get(playerName).updateEntrance(students, true);
        userInterface.firePropertyChange(new PropertyChangeEvent(playerName, "CloudSelected", students, cloudId));
    }

    /**
     * Updates the assistants deck of the client after an AssistantsUpdate message
     *
     * @param payload the payload of the message
     */
    private void onAssistantsUpdate(MessagePayload payload) {
        Integer[] values = (Integer[]) payload.getAttribute("Values").getAsObject();
        Integer[] motherNature = (Integer[]) payload.getAttribute("MotherNatureMovements").getAsObject();
        Map<Integer, Integer> newClientAssistants = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            newClientAssistants.put(values[i], motherNature[i]);
        }
        getModelView().setClientPlayerAssistants(newClientAssistants);
    }

    /**
     * Updates the students of all clouds and notifies the user interface after a CloudsRefill message have arrived
     *
     * @param payload the payload of the message
     */
    private void onCloudsRefill(MessagePayload payload) {
        RealmType[][] cloudsStudents = (RealmType[][]) payload.getAttribute("CloudsStudents").getAsObject();
        FieldView fieldView = getModelView().getField();
        for (int i = 0; i < cloudsStudents.length; i++) {
            fieldView.updateCloudStudents(i, cloudsStudents[i]);
        }
        userInterface.firePropertyChange("CloudsRefill", null, null);
    }
}
