package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleIsland;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GameUpdateMessageHandler extends BaseMessageHandler {
    private static final List<String> messageHandled =
            List.of("AssistantPlayed", "ProfessorUpdate", "MotherNatureMovement", "SchoolDiningRoomUpdate",
                    "IslandStudentsUpdate", "IslandUnification", "IslandTowerUpdate", "PickFromCloud");

    public GameUpdateMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if(header.getMessageType() != ServerMessageType.GAME_UPDATE && !messageHandled.contains(header.getMessageName())) {
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
            case "IslandUnificationUpdate" -> onIslandUnificationUpdate(payload);
            case "IslandTowerUpdate" -> onIslandTowerUpdate(payload);
            case "PickFromCloud" -> onPickFromCloud(payload);
        }
    }

    private void onAssistantPlayed(MessagePayload payload) {
        int assistant = payload.getAttribute("AssistantId").getAsInt();
        int motherNatureMovement = payload.getAttribute("MotherNatureMovement").getAsInt();
        String playerName = payload.getAttribute("PlayerName").getAsString();
        getModelView().getPlayers().get(playerName).updateLastPlayedAssistant(assistant, motherNatureMovement);
        if (getUserInterface().getNickname().equals(playerName)) getModelView().removeAssistant(assistant);

        //TODO
    }

    private void onProfessorUpdate(MessagePayload payload) {
        String newOwner = payload.getAttribute("PlayerName").getAsString();
        RealmType professor = (RealmType) payload.getAttribute("ProfessorType").getAsObject();
        Optional<String> lastOwner = getModelView().getField().updateProfessorOwner(professor, newOwner);

        //TODO
    }

    private void onMotherNatureMovement(MessagePayload payload) {
        int newMotherNaturePosition = payload.getAttribute("FinalPosition").getAsInt();
        getModelView().getField().updateMotherNaturePosition(newMotherNaturePosition);

        //TODO
    }

    private void onSchoolDiningRoomUpdate(MessagePayload payload) {
        //TODO
    }

    private void onIslandStudentsUpdate(MessagePayload payload) {
        int islandId = payload.getAttribute("IslandId").getAsInt();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        getModelView().getField().updateIslandStudents(islandId, students);

        //TODO
    }

    private void onIslandUnificationUpdate(MessagePayload payload) {
        Integer[] islandsId = (Integer[]) payload.getAttribute("IslandsId").getAsObject();
        SimpleIsland island = (SimpleIsland) payload.getAttribute("NewIsland").getAsObject();
        getModelView().getField().mergeIslands(Arrays.asList(islandsId), island.getIslandRepresentation());

        //TODO
    }

    private void onIslandTowerUpdate(MessagePayload payload) {
        int island = payload.getAttribute("IslandId").getAsInt();
        TowerType tower = (TowerType) payload.getAttribute("Tower").getAsObject();
        getModelView().updateIslandTower(island, tower);

        //TODO
    }

    private void onPickFromCloud(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        int cloudId = payload.getAttribute("CloudId").getAsInt();
        getModelView().getField().resetCloud(cloudId);
        getModelView().getPlayers().get(playerName).updateEntrance(students, true);

        //TODO
    }
}
