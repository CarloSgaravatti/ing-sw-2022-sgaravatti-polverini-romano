package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.messages.simpleModel.SimpleField;
import it.polimi.ingsw.messages.simpleModel.SimpleIsland;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Triplet;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.modelView.ModelView.insertStudents;

public class FieldView {
    private final List<Triplet<Integer[], Integer, TowerType>> islands;
    private final Map<Integer, Integer[]> cloudStudents;
    private int motherNaturePosition;
    private final String[] professorsOwners = new String[RealmType.values().length];
    private ExpertFieldView expertField;

    public FieldView(List<SimpleIsland>  islands, Map<Integer, Integer[]> cloudStudents, int motherNaturePosition) {
        this.islands = islands.stream().map(SimpleIsland::getIslandRepresentation).collect(Collectors.toList());
        this.cloudStudents = cloudStudents;
        this.motherNaturePosition = motherNaturePosition;
    }

    public FieldView(SimpleField simpleField) {
        this(simpleField.getIslands(), simpleField.getClouds(), simpleField.getMotherNaturePosition());
        if (!simpleField.getCharacters().isEmpty()) {
            expertField = new ExpertFieldView(simpleField.getCharacters());
        }
    }

    public void updateIslandStudents(int islandId, RealmType[] studentsInserted) {
        Integer[] islandStudents = islands.get(islandId).getFirst();
        insertStudents(islandStudents, studentsInserted);
    }

    public void mergeIslands(List<Integer> islands, Triplet<Integer[], Integer, TowerType> newIsland) {
        Optional<Integer> newIndex = islands.stream().min(Comparator.comparingInt(i -> i));
        if (newIndex.isEmpty()) return;
        for (Integer i: islands) {
            this.islands.remove(this.islands.get(i));
        }
        this.islands.add(newIsland);
    }

    public void updateMotherNaturePosition(int newPosition) {
        motherNaturePosition = newPosition;
    }

    public void updateCloudStudents(int cloudId, RealmType[] studentsInserted) {
        Integer[] cloudStudents = this.cloudStudents.get(cloudId);
        insertStudents(cloudStudents, studentsInserted);
    }

    public void resetCloud(int cloudId) {
        Integer[] emptyCloudStudents = new Integer[RealmType.values().length];
        Arrays.fill(emptyCloudStudents, 0);
        cloudStudents.replace(cloudId, emptyCloudStudents);
    }

    public Triplet<Integer[], Integer, TowerType> getIsland(int islandId) {
        return islands.get(islandId);
    }

    public int getIslandSize() {
        return islands.size();
    }

    public Integer[] getCloudStudents(int cloudId) {
        return cloudStudents.get(cloudId);
    }

    public int getMotherNaturePosition() {
        return motherNaturePosition;
    }

    public String getProfessorOwner(RealmType realm) {
        return professorsOwners[realm.ordinal()];
    }

    public Optional<String> updateProfessorOwner(RealmType realm, String newOwner) {
        Optional<String> lastOwner = Optional.ofNullable(professorsOwners[realm.ordinal()]);
        professorsOwners[realm.ordinal()] = newOwner;
        return lastOwner;
    }

    public ExpertFieldView getExpertField() {
        return expertField;
    }

    public Map<Integer, Integer[]> getCloudStudents() {
        return cloudStudents;
    }
}
