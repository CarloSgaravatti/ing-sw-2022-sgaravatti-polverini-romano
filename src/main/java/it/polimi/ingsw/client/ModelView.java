package it.polimi.ingsw.client;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

import java.util.List;
import java.util.Map;

//Summary of game model
public class ModelView {
    private Map<Integer, Pair<Integer[], Pair<Integer, TowerType>>> islandStudents; //can be more than one attribute
    private Map<Integer, Integer[]> cloudStudents;
    private int motherNaturePosition;
    private Map<String, Pair<Integer[], Integer[]>> schoolStudents;
    private Map<String, RealmType[]> schoolProfessors;
    private Map<String, TowerType> towers;
    private Map<String, WizardType> wizards;
    private int[] charactersIds;
    private int[] noEntryTilesPositions;
    private Map<Integer, Integer[]> characterStudents;

    public ModelView() {

    }

    private void updateIslandStudents(int islandId, RealmType[] studentsInserted) {

    }

    private void updateIslandTower(int islandId, TowerType tower) {

    }

    private void mergeIslands(List<Integer> islands) {

    }

    private void updateMotherNaturePosition(int newPosition) {

    }

    private void updateCloudStudents(int cloudId, RealmType[] studentsInserted) {

    }
}
