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

public class GameUpdateMessageHandler extends BaseMessageHandler {
    private static final List<String> messageHandled =
            List.of("AssistantPlayed", "ProfessorUpdate", "MotherNatureMovement", "SchoolDiningRoomUpdate", "IslandStudentsUpdate",
                    "IslandUnification", "IslandTowerUpdate", "PickFromCloud", "AssistantsUpdate", "CloudsRefill");
    private final PropertyChangeSupport userInterface = new PropertyChangeSupport(this);

    public GameUpdateMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
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

    private void onAssistantPlayed(MessagePayload payload) {
        int assistant = payload.getAttribute("AssistantId").getAsInt();
        int motherNatureMovement = payload.getAttribute("MotherNatureMovement").getAsInt();
        String playerName = payload.getAttribute("PlayerName").getAsString();
        System.out.println(playerName + " has played assistant " + assistant);
        getModelView().getPlayers().get(playerName).updateLastPlayedAssistant(assistant, motherNatureMovement);
        if (getUserInterface().getNickname().equals(playerName)) getModelView().removeAssistant(assistant);
        userInterface.firePropertyChange("AssistantUpdate", assistant, playerName);
    }

    private void onProfessorUpdate(MessagePayload payload) {
        String newOwner = payload.getAttribute("PlayerName").getAsString();
        RealmType professor = (RealmType) payload.getAttribute("ProfessorType").getAsObject();
        Optional<String> lastOwner = getModelView().getField().updateProfessorOwner(professor, newOwner);
        userInterface.firePropertyChange("ProfessorUpdate", lastOwner.orElse(null), newOwner);
    }

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
            }
        }
        userInterface.firePropertyChange("MotherNatureUpdate", startingPosition, newMotherNaturePosition);
    }

    private void onSchoolDiningRoomUpdate(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        boolean isInsertion = payload.getAttribute("IsInsertion").getAsBoolean();
        getModelView().getPlayers().get(playerName).updateDiningRoom(students, isInsertion);
        boolean isFromEntrance = payload.getAttribute("IsFromEntrance").getAsBoolean();
        if (isInsertion && isFromEntrance) {
            getModelView().getPlayers().get(playerName).updateEntrance(students, false);
        }
        //userInterface.firePropertyChange("SchoolDiningRoomUpdate", null, playerName);
        if (isInsertion) {
            userInterface.firePropertyChange(
                    new PropertyChangeEvent(playerName, "DiningRoomInsertion", isFromEntrance, students));
        } else {
            userInterface.firePropertyChange(
                    new PropertyChangeEvent(playerName, "DiningRoomRemoval", null, students));
        }
    }

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

    private void onIslandUnificationUpdate(MessagePayload payload) {
        Integer[] islandsId = (Integer[]) payload.getAttribute("IslandsId").getAsObject();
        SimpleIsland island = (SimpleIsland) payload.getAttribute("NewIsland").getAsObject();
        getModelView().getField().mergeIslands(Arrays.asList(islandsId), island.getIslandRepresentation());
        userInterface.firePropertyChange("IslandUnification", null, null);
    }

    /*private void onIslandTowerUpdate(MessagePayload payload) {
        int island = payload.getAttribute("IslandId").getAsInt();
        TowerType tower = (TowerType) payload.getAttribute("TowerType").getAsObject();
        getModelView().updateIslandTower(island, tower);
        userInterface.firePropertyChange("IslandTowerUpdate", null, island);
    }*/

    private void onIslandTowerUpdate(MessagePayload payload) {
        int island = payload.getAttribute("IslandId").getAsInt();
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
        userInterface.firePropertyChange("IslandTowerUpdate", null, island);
    }

    private void onPickFromCloud(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        int cloudId = payload.getAttribute("CloudId").getAsInt();
        getModelView().getField().resetCloud(cloudId);
        getModelView().getPlayers().get(playerName).updateEntrance(students, true);
        userInterface.firePropertyChange(new PropertyChangeEvent(playerName, "PickFromCloud", null, cloudId));
    }

    private void onAssistantsUpdate(MessagePayload payload) {
        Integer[] values = (Integer[]) payload.getAttribute("Values").getAsObject();
        Integer[] motherNature = (Integer[]) payload.getAttribute("MotherNatureMovements").getAsObject();
        Map<Integer, Integer> newClientAssistants = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            newClientAssistants.put(values[i], motherNature[i]);
        }
        getModelView().setClientPlayerAssistants(newClientAssistants);
    }

    private void onCloudsRefill(MessagePayload payload) {
        RealmType[][] cloudsStudents = (RealmType[][]) payload.getAttribute("CloudsStudents").getAsObject();
        FieldView fieldView = getModelView().getField();
        for (int i = 0; i < cloudsStudents.length; i++) {
            fieldView.updateCloudStudents(i, cloudsStudents[i]);
        }
        userInterface.firePropertyChange("CloudsRefill", null, null);
    }
}
