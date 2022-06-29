package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.messages.simpleModel.SimpleField;
import it.polimi.ingsw.messages.simpleModel.SimpleIsland;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Triplet;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.modelView.ModelView.insertStudents;

/**
 * FieldView is the part of the ModelView that contains all information about islands, clouds, professors and characters.
 * Islands are represented as a (Integer[], Integer, TowerType) tuple, where the Integer[] is a representation of the
 * students present, the Integer represent the number of towers that the island can take and the TowerType is the type of
 * tower that is present on the island. Clouds are represented as an Integer[] which represent the students that are
 * present on the cloud and is associated to the cloud id. Characters are contained on an ExpertFieldView.
 */
public class FieldView {
    private final List<Triplet<Integer[], Integer, TowerType>> islands;
    private final Map<Integer, Integer[]> cloudStudents;
    private int motherNaturePosition;
    private final String[] professorsOwners = new String[RealmType.values().length];
    private ExpertFieldView expertField;

    /**
     * Constructs a new instance of FieldView that will contain the specified islands, the specified clouds and the
     * specified starting mother nature position
     *
     * @param islands the initial islands of the game
     * @param cloudStudents the initial clouds of the game
     * @param motherNaturePosition the initial mother nature position
     * @see SimpleIsland
     */
    public FieldView(List<SimpleIsland>  islands, Map<Integer, Integer[]> cloudStudents, int motherNaturePosition) {
        this.islands = islands.stream().map(SimpleIsland::getIslandRepresentation).collect(Collectors.toList());
        this.cloudStudents = cloudStudents;
        this.motherNaturePosition = motherNaturePosition;
    }

    /**
     * Construct a new instance of FieldView which will get all starting information from the specified SimpleField
     *
     * @param simpleField the simple field arrived from the server, which contains information about islands, clouds and
     *                    characters
     * @see SimpleField
     */
    public FieldView(SimpleField simpleField) {
        this(simpleField.getIslands(), simpleField.getClouds(), simpleField.getMotherNaturePosition());
        if (!simpleField.getCharacters().isEmpty()) {
            expertField = new ExpertFieldView(simpleField.getCharacters());
            for (int i = 0; i < simpleField.getIslands().size(); i++) {
                int noEntryTiles = simpleField.getIslands().get(i).getNumEntryTiles();
                if (noEntryTiles > 0) {
                    expertField.updateIslandNoEntryTiles(noEntryTiles, i);
                }
            }
        }
    }

    /**
     * Update the specified island by inserting the specified students
     *
     * @param islandId the island to be updated
     * @param studentsInserted the students to insert
     */
    public void updateIslandStudents(int islandId, RealmType[] studentsInserted) {
        Integer[] islandStudents = islands.get(islandId).getFirst();
        insertStudents(islandStudents, studentsInserted);
    }

    /**
     * Updates the island list by removing all the specified islands and by inserting the new island. The new islands is
     * the representation of the merged island and will have the minimum of the specified ids as its id.
     *
     * @param islands the merged islands
     * @param newIsland the new island that will replace the merged island
     */
    public void mergeIslands(List<Integer> islands, Triplet<Integer[], Integer, TowerType> newIsland) {
        Optional<Integer> newIndex = islands.stream().min(Comparator.comparingInt(i -> i));
        if (newIndex.isEmpty()) return;
        List<Triplet<Integer[], Integer, TowerType>> islandsToRemove = islands.stream().map(this.islands::get).toList();
        this.islands.removeAll(islandsToRemove);
        this.islands.add(newIndex.get(), newIsland);
        int noEntryTiles = 0;
        if (expertField != null) {
            for (Integer i : islands) {
                noEntryTiles += expertField.getNoEntryTilesOnIsland(i);
                expertField.resetNoEntryTilesOnIsland(i);
            }
            for (int i = 0; i < noEntryTiles; i++) {
                expertField.insertNoEntryTileOnIsland(newIndex.get());
            }
        }
        updateMotherNaturePosition(newIndex.get());
    }

    /**
     * Updates the position of mother nature
     *
     * @param newPosition the new position of mother nature
     */
    public void updateMotherNaturePosition(int newPosition) {
        motherNaturePosition = newPosition;
    }

    /**
     * Updates the students that are present on the specified cloud
     *
     * @param cloudId the id of the cloud
     * @param studentsInserted students that are inserted on the cloud
     */
    public void updateCloudStudents(int cloudId, RealmType[] studentsInserted) {
        Integer[] cloudStudents = this.cloudStudents.get(cloudId);
        insertStudents(cloudStudents, studentsInserted);
    }

    /**
     * Reset the students that are present on the cloud
     *
     * @param cloudId the cloud that will be resetted
     */
    public void resetCloud(int cloudId) {
        Integer[] emptyCloudStudents = new Integer[RealmType.values().length];
        Arrays.fill(emptyCloudStudents, 0);
        cloudStudents.replace(cloudId, emptyCloudStudents);
    }

    /**
     * Returns the representation of the specified island
     *
     * @param islandId the id of the island
     * @return the representation of the specified island
     */
    public Triplet<Integer[], Integer, TowerType> getIsland(int islandId) {
        return islands.get(islandId);
    }

    /**
     * Returns the number of islands that are present
     *
     * @return the number of islands that are present
     */
    public int getIslandSize() {
        return islands.size();
    }

    /**
     * Returns mother nature position
     *
     * @return mother nature position
     */
    public int getMotherNaturePosition() {
        return motherNaturePosition;
    }

    /**
     * Returns the owner of the specified professor realm, if present; otherwise null
     *
     * @param realm the realm of the professor
     * @return the owner of the professor
     */
    public String getProfessorOwner(RealmType realm) {
        return professorsOwners[realm.ordinal()];
    }

    /**
     * Updates the professor owner and returns the last owner of it
     *
     * @param realm the realm of the professor
     * @param newOwner the new owner of the professor
     * @return the last owner of the professor if present, otherwise an empty Optional
     */
    public Optional<String> updateProfessorOwner(RealmType realm, String newOwner) {
        Optional<String> lastOwner = Optional.ofNullable(professorsOwners[realm.ordinal()]);
        professorsOwners[realm.ordinal()] = newOwner;
        return lastOwner;
    }

    /**
     * Returns the expert field view of the game
     *
     * @return the expert field view of the game
     */
    public ExpertFieldView getExpertField() {
        return expertField;
    }

    /**
     * Returns all clouds information, that consists of all students per cloud
     *
     * @return all clouds information, that consists of all students per cloud
     */
    public Map<Integer, Integer[]> getCloudStudents() {
        return cloudStudents;
    }
}
