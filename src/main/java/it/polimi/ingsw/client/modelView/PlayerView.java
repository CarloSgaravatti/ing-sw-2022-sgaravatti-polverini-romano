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

/**
 * PlayerView contains all information about a specific player that the client needs to know: school students, professors
 * and towers, the type of tower and the type of wizard of the player, the last played assistant associated to the
 * maximum movement that mother nature can be moved by him and (if the game is expert), the number of coins that the
 * player have. Students are represented with the Integer[] representation.
 */
public class PlayerView {
    private final Pair<Integer[], Integer[]> schoolStudents;
    private int numTowers;
    private TowerType tower;
    private WizardType wizard;
    private int playerCoins;
    private Pair<Integer, Integer> lastPlayedAssistant;

    /**
     * Constructs a new PlayerView which have 0 students in both entrance and dining room
     */
    public PlayerView() {
        schoolStudents = new Pair<>(new Integer[RealmType.values().length], new Integer[RealmType.values().length]);
        Arrays.fill(schoolStudents.getFirst(), 0);
        Arrays.fill(schoolStudents.getSecond(), 0);
    }

    /**
     * Updates the dining room of the player by inserting or removing the specified students.
     *
     * @param students the students that have been inserted/removed
     * @param isInsertion true if the students are inserted, otherwise false
     */
    public void updateDiningRoom(RealmType[] students, boolean isInsertion) {
        Integer[] diningRoom = schoolStudents.getSecond();
        updateStudents(diningRoom, students, isInsertion);
    }

    /**
     * Updates the entrance of the player by inserting or removing the specified students.
     *
     * @param students the students that have been inserted/removed
     * @param isInsertion true if the students are inserted, otherwise false
     */
    public void updateEntrance(RealmType[] students, boolean isInsertion) {
        Integer[] entrance = schoolStudents.getFirst();
        updateStudents(entrance, students, isInsertion);
    }

    /**
     * Updates the specified studentContainer (represented as Integer[]) by inserting or removing the specified students.
     *
     * @param studentContainer the Integer[] representation of the student container (entrance or dining room)
     * @param students the students that have been inserted/removed
     * @param isInsertion true if the students are inserted, otherwise false
     */
    private static void updateStudents(Integer[] studentContainer, RealmType[] students, boolean isInsertion) {
        if (isInsertion) insertStudents(studentContainer, students);
        else removeStudents(studentContainer, students);
    }

    /**
     * Updates the number of coins of the player
     *
     * @param newCoins the new coins of the player
     */
    public void updateCoins(int newCoins) {
        playerCoins = newCoins;
    }

    /**
     * Updates the number of towers present on the school of the player
     *
     * @param numTowers the number of towers present on the school of the player
     */
    public void updateNumTowers(int numTowers) {
        this.numTowers = numTowers;
    }

    /**
     * Set the specified wizard as the wizard of the player
     *
     * @param wizard the wizard of the player
     */
    public void setWizard(WizardType wizard) {
        this.wizard = wizard;
    }

    /**
     * Set the specified tower as the tower of the player
     *
     * @param tower the tower of the player
     */
    public void setTower(TowerType tower) {
        this.tower = tower;
    }

    /**
     * Returns both entrance and dining room students of the player's school
     *
     * @return entrance and dining room students of the player's school
     */
    public Pair<Integer[], Integer[]> getSchoolStudents() {
        return schoolStudents;
    }

    /**
     * Returns the tower of the player
     *
     * @return the tower of the player
     */
    public TowerType getPlayerTower() {
        return tower;
    }

    /**
     * Returns the wizard of the player
     *
     * @return the wizard of the player
     */
    public WizardType getPlayerWizard() {
        return wizard;
    }

    /**
     * Returns the number of towers that the player have on the school
     *
     * @return the number of towers that the player have on the school
     */
    public int getNumTowers() {
        return numTowers;
    }

    /**
     * Returns the number of coins of the player
     *
     * @return the number of coins of the player
     */
    public int getPlayerCoins() {
        return playerCoins;
    }

    /**
     * Resets students of entrance and dining room to the specified students. All previous students' information are removed
     *
     * @param entrance the new students of the entrance
     * @param diningRoom the new students of the dining room
     */
    public void resetStudentsTo(RealmType[] entrance, RealmType[] diningRoom) {
        Arrays.fill(schoolStudents.getFirst(), 0);
        Arrays.fill(schoolStudents.getSecond(), 0);
        updateEntrance(entrance, true);
        updateDiningRoom(diningRoom, true);
    }

    /**
     * Updates the last assistant played by the player. The assistant have the specified id and the specified maximum
     * mother nature movement
     *
     * @param assistant the id of the assistant
     * @param motherNatureMovement the maximum mother nature movement of the assistant
     */
    public void updateLastPlayedAssistant(int assistant, int motherNatureMovement) {
        lastPlayedAssistant = new Pair<>(assistant, motherNatureMovement);
    }

    /**
     * Returns the last assistant id played by the player associated with his maximum mother nature movement
     *
     * @return the last assistant id played by the player associated with his maximum mother nature movement
     */
    public Pair<Integer, Integer> getLastPlayedAssistant() {
        return lastPlayedAssistant;
    }
}
