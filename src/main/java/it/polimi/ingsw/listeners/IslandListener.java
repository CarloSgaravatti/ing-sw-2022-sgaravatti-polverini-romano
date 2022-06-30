package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleIsland;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * IslandListener is a PropertyChangeListener that will listen an island in order to inform a specific client that an
 * event that regard the island was fired
 *
 * @see java.beans.PropertyChangeListener
 */
public class IslandListener implements PropertyChangeListener {
    private static final String STUDENTS = "IslandStudents";
    private static final String TOWER = "IslandTower";
    private static final String UNIFICATION = "IslandUnification";
    private final RemoteView remoteView;

    /**
     * Constructs an IslandListener that is associated to the specified remote view
     *
     * @param remoteView the remote view of the client
     */
    public IslandListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    /**
     * Responds to an event fired from the island that the class is listening
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case STUDENTS -> onStudentUpdate((Integer) evt.getSource(), (RealmType[]) evt.getNewValue(), (Boolean) evt.getOldValue());
            case TOWER -> onTowerUpdate((Integer) evt.getSource(), (Player) evt.getOldValue(), (Player) evt.getNewValue());
            case UNIFICATION -> onIslandUnification((Integer[]) evt.getOldValue(), (Island) evt.getNewValue());
        }
    }

    /**
     * Forwards an IslandStudentsUpdate message to inform the client that some students were added to the island
     *
     * @param island the id of the island
     * @param realmTypes the students that were added
     * @param isFromEntrance true if the students come the entrance of the active player, otherwise false
     */
    private void onStudentUpdate(int island, RealmType[] realmTypes, boolean isFromEntrance){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("IslandId", island);
        messagePayload.setAttribute("Students", realmTypes);
        messagePayload.setAttribute("IsFromEntrance", isFromEntrance);
        remoteView.sendMessage(messagePayload, "IslandStudentsUpdate", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards an IslandTowerUpdate message to inform the client that the tower of the island is changed
     *
     * @param indexIsland the id of the island
     * @param previousOwner the previous owner of the island (can be null if no one previously own the island)
     * @param newOwner the new owner of the island
     */
    private void onTowerUpdate(int indexIsland, Player previousOwner, Player newOwner) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("TowerType", newOwner.getSchool().getTowerType());
        messagePayload.setAttribute("IslandId", indexIsland);
        messagePayload.setAttribute("NewOwner", newOwner.getNickName());
        if (previousOwner != null) {
            messagePayload.setAttribute("PreviousOwner", previousOwner.getNickName());
        }
        remoteView.sendMessage(messagePayload, "IslandTowerUpdate", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards an IslandUnification message to inform the client the specified islands have been unified
     *
     * @param islandIndexes the ids of the unified islands
     * @param island the new island group
     */
    private void onIslandUnification(Integer[] islandIndexes, Island island){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("IslandsId", islandIndexes);
        RealmType[] newStudents = island.getStudents().stream().map(Student::getStudentType).toList().toArray(new RealmType[0]);
        SimpleIsland newIsland = new SimpleIsland(RealmType.getIntegerRepresentation(newStudents),
                island.getNumTowers(), island.getTowerType(), island.getNoEntryTilePresents());
        messagePayload.setAttribute("NewIsland", newIsland);
        remoteView.sendMessage(messagePayload, "IslandUnification", ServerMessageType.GAME_UPDATE);
    }
}
