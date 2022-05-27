package it.polimi.ingsw.listeners;

import it.polimi.ingsw.client.CLI.utils.Colors;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleIsland;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class IslandListener implements PropertyChangeListener {
    private static final String STUDENTS = "IslandStudents";
    private static final String TOWER = "IslandTower";
    private static final String UNIFICATION = "IslandUnification";
    private final RemoteView remoteView;

    public IslandListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case STUDENTS -> onStudentUpdate((Integer) evt.getSource(), (RealmType[]) evt.getNewValue(), (Boolean) evt.getOldValue());
            //case TOWER -> onTowerUpdate((TowerType) evt.getNewValue(), (Integer) evt.getSource());
            case TOWER -> onTowerUpdate((Integer) evt.getSource(), (Player) evt.getOldValue(), (Player) evt.getNewValue());
            case UNIFICATION -> onIslandUnification((Integer[]) evt.getOldValue(), (Island) evt.getNewValue());
        }
    }

    private void onStudentUpdate(int island, RealmType[] realmTypes, boolean isFromEntrance){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("IslandId", island);
        messagePayload.setAttribute("Students", realmTypes);
        messagePayload.setAttribute("IsFromEntrance", isFromEntrance);
        remoteView.sendMessage(messagePayload, "IslandStudentsUpdate", ServerMessageType.GAME_UPDATE);
    }

    private void onTowerUpdate(TowerType type, int indexIsland) {
        System.out.println(Colors.BLUE + "Sending island tower update");
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("TowerType", type);
        messagePayload.setAttribute("IslandId", indexIsland);
        remoteView.sendMessage(messagePayload, "IslandTowerUpdate", ServerMessageType.GAME_UPDATE);
    }

    private void onTowerUpdate(int indexIsland, Player previousOwner, Player newOwner) {
        System.out.println(Colors.BLUE + "Sending island tower update");
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("TowerType", newOwner.getSchool().getTowerType());
        messagePayload.setAttribute("IslandId", indexIsland);
        messagePayload.setAttribute("NewOwner", newOwner.getNickName());
        if (previousOwner != null) {
            messagePayload.setAttribute("PreviousOwner", previousOwner.getNickName());
        }
        remoteView.sendMessage(messagePayload, "IslandTowerUpdate", ServerMessageType.GAME_UPDATE);
    }

    private void onIslandUnification(Integer[] islandIndexes, Island island){
        System.out.println(Colors.BLUE + "Sending island unification");
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("IslandsId", islandIndexes);
        RealmType[] newStudents = island.getStudents().stream().map(Student::getStudentType).toList().toArray(new RealmType[0]);
        SimpleIsland newIsland = new SimpleIsland(RealmType.getIntegerRepresentation(newStudents),
                island.getNumTowers(), island.getTowerType(), island.getNoEntryTilePresents());
        messagePayload.setAttribute("NewIsland", newIsland);
        remoteView.sendMessage(messagePayload, "IslandUnification", ServerMessageType.GAME_UPDATE);
    }
}
