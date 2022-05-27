package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.client.modelView.ModelView.insertStudents;
import static it.polimi.ingsw.client.modelView.ModelView.removeStudents;

public class PlayerView {
    private final Pair<Integer[], Integer[]> schoolStudents;
    private int numTowers;
    private TowerType tower;
    private WizardType wizard;
    private int playerCoins;
    private Pair<Integer, Integer> lastPlayedAssistant;

    public PlayerView() {
        schoolStudents = new Pair<>(new Integer[RealmType.values().length], new Integer[RealmType.values().length]);
        Arrays.fill(schoolStudents.getFirst(), 0);
        Arrays.fill(schoolStudents.getSecond(), 0);
    }

    public void updateDiningRoom(RealmType[] students, boolean isInsertion) {
        Integer[] diningRoom = schoolStudents.getSecond();
        updateStudents(diningRoom, students, isInsertion);
    }

    public void updateEntrance(RealmType[] students, boolean isInsertion) {
        System.out.println("Updating entrance with students " + Arrays.toString(students));
        Integer[] entrance = schoolStudents.getFirst();
        updateStudents(entrance, students, isInsertion);
    }

    private static void updateStudents(Integer[] studentContainer, RealmType[] students, boolean isInsertion) {
        if (isInsertion) insertStudents(studentContainer, students);
        else removeStudents(studentContainer, students);
    }

    public void updateCoins(int newCoins) {
        playerCoins = newCoins;
    }

    public void updateNumTowers(int numTowers) {
        this.numTowers = numTowers;
    }

    public void setWizard(WizardType wizard) {
        this.wizard = wizard;
    }

    public void setTower(TowerType tower) {
        this.tower = tower;
    }

    public Pair<Integer[], Integer[]> getSchoolStudents() {
        return schoolStudents;
    }

    public TowerType getPlayerTower() {
        return tower;
    }

    public WizardType getPlayerWizard() {
        return wizard;
    }

    public int getNumTowers() {
        return numTowers;
    }

    public int getPlayerCoins() {
        return playerCoins;
    }

    public void resetStudentsTo(RealmType[] entrance, RealmType[] diningRoom) {
        Arrays.fill(schoolStudents.getFirst(), 0);
        Arrays.fill(schoolStudents.getSecond(), 0);
        updateEntrance(entrance, true);
        updateDiningRoom(diningRoom, true);
    }

    public void updateLastPlayedAssistant(int assistant, int motherNatureMovement) {
        lastPlayedAssistant = new Pair<>(assistant, motherNatureMovement);
    }

    public Pair<Integer, Integer> getLastPlayedAssistant() {
        return lastPlayedAssistant;
    }
}
