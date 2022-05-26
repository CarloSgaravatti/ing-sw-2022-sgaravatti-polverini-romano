package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
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
            System.out.println("Passing message " + message.getServerMessageHeader().getMessageName());
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

        //TODO
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
        userInterface.firePropertyChange("MotherNatureUpdate", startingPosition, newMotherNaturePosition);
    }

    private void onSchoolDiningRoomUpdate(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        System.out.println("Received school dining room update " + Arrays.toString(students));
        boolean isInsertion = payload.getAttribute("IsInsertion").getAsBoolean();
        getModelView().getPlayers().get(playerName).updateDiningRoom(students, isInsertion);
        //Fixme after fixing school (for character 11)
        if (isInsertion) {
            getModelView().getPlayers().get(playerName).updateEntrance(students, false);
        }
        System.out.println("Firing school dining room update event");
        userInterface.firePropertyChange("SchoolDiningRoomUpdate", null, playerName);
    }

    //FIXME when a student come from a character
    private void onIslandStudentsUpdate(MessagePayload payload) {
        int islandId = payload.getAttribute("IslandId").getAsInt();
        RealmType[] students = (RealmType[]) payload.getAttribute("Students").getAsObject();
        getModelView().getPlayers().get(getModelView().getCurrentActivePlayer()).updateEntrance(students, false);
        getModelView().getField().updateIslandStudents(islandId, students);
        userInterface.firePropertyChange("IslandStudentsUpdate", null, islandId);
    }

    private void onIslandUnificationUpdate(MessagePayload payload) {
        Integer[] islandsId = (Integer[]) payload.getAttribute("IslandsId").getAsObject();
        System.out.println("Received island unification of" + Arrays.toString(islandsId));
        SimpleIsland island = (SimpleIsland) payload.getAttribute("NewIsland").getAsObject();
        getModelView().getField().mergeIslands(Arrays.asList(islandsId), island.getIslandRepresentation());
        userInterface.firePropertyChange("IslandUnification", null, null);
    }

    private void onIslandTowerUpdate(MessagePayload payload) {
        int island = payload.getAttribute("IslandId").getAsInt();
        TowerType tower = (TowerType) payload.getAttribute("TowerType").getAsObject();
        getModelView().updateIslandTower(island, tower);
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
        //System.out.println("Received assistant update: your assistants are " + Arrays.toString(values));
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
