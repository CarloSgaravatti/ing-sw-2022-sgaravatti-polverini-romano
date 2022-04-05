package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.exceptions.StudentsNumberInCloudException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ActionControllerTest extends TestCase {
    ActionController actionController;
    GameController gameController;
    Player activePlayer; //Used to check values after the actions

    @BeforeEach
    void setup() {
        gameController = new GameController();
        gameController.initializeTurnController();
        InitController initController = gameController.getInitController();
        try {
            initController.initializeGameComponents();
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        initController.setNumPlayers(2);
        initController.addPlayer("player1");
        initController.addPlayer("player2");
        actionController = new ActionController(gameController, gameController.getTurnController());
        activePlayer = gameController.getTurnController().getActivePlayer();
    }

    @Test
    void playAssistantTest() {
        try {
            actionController.doAction("Assistant 3");
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(3, activePlayer.getTurnEffect().getOrderPrecedence());
    }

    @Test
    void studentMovementOnlyToDiningRoomTest() {
        Student[] students = new Student[3];
        for (int i = 0; i < 3; i++) {
            students[i] = new Student(RealmType.values()[i]);
        }
        activePlayer.setSchool(new School(8, TowerType.BLACK));
        activePlayer.getSchool().insertEntrance(students);
        //Now in the entrance there are: 1 Y, 1 B, 1 G
        try {
            actionController.doAction("Students Y D B D G D");
        } catch (Exception e) {
            Assertions.fail();
        }
        for (Student s: students) {
            Assertions.assertEquals(1, activePlayer.getSchool().getNumStudentsDiningRoom(s.getStudentType()));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 11})
    void studentMovementAlsoToIslandTest(int islandIndex) {
        Island island = gameController.getModel().getIslands().get(islandIndex);
        int initialYellowsInIsland = island.getNumStudentsOfType(RealmType.YELLOW_GNOMES);
        Student[] students = new Student[3];
        for (int i = 0; i < 3; i++) {
            students[i] = new Student(RealmType.YELLOW_GNOMES);
        }
        activePlayer.setSchool(new School(8, TowerType.BLACK));
        activePlayer.getSchool().insertEntrance(students);
        try {
            actionController.doAction("Students Y D Y D Y I" + islandIndex);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(2, activePlayer.getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(initialYellowsInIsland + 1,
                island.getNumStudentsOfType(RealmType.YELLOW_GNOMES));
    }

    @Test
    void motherNatureMovementTest() {
        int currMotherNature = gameController.getModel().motherNaturePositionIndex();
        try {
            activePlayer.playAssistant(5, new ArrayList<>());
        } catch (NoSuchAssistantException e) {
            Assertions.fail();
        }
        try {
            actionController.doAction("MotherNature 3");
        } catch (Exception e) {
            Assertions.fail();
        }
        int newMotherNature = gameController.getModel().motherNaturePositionIndex();
        Assertions.assertEquals(currMotherNature + 3, newMotherNature);

    }

    @Test
    void chooseCloudPickStudentsTest() {
        Cloud cloud = gameController.getModel().getClouds()[1];
        Student[] students = new Student[3];
        Arrays.fill(students, new Student(RealmType.YELLOW_GNOMES));
        try {
            cloud.insertStudents(students);
        } catch (StudentsNumberInCloudException e) {
            Assertions.fail();
        }
        activePlayer.setSchool(new School(8, TowerType.BLACK));
        try {
            actionController.doAction("Cloud 1");
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(3, activePlayer.getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
    }

    @RepeatedTest(3)
    void playCharacterTest() {
        CharacterCard characterCard = gameController.getModel().getCharacterCards()[new Random().nextInt(3)];
        int characterToPlay = characterCard.getId();
        try {
            actionController.doAction("PlayCharacter " + characterToPlay);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(activePlayer, characterCard.getPlayerActive());
    }

    @Test
    //TODO: one test for all characters that have an active effect
    void characterEffect() {

    }
}