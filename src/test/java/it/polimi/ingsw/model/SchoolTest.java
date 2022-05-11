package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class SchoolTest extends TestCase {
    School schoolTest;

    @BeforeEach
    void setup() {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(2);
        schoolTest = new School(8, TowerType.BLACK, gameConstants, null);
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void insertEntranceTest(RealmType studentType) {
        Student student = new Student(studentType);
        schoolTest.insertEntrance(student);
        try {
            Assertions.assertEquals(schoolTest.removeStudentEntrance(studentType), student);
        } catch (StudentNotFoundException e) {
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void insertDiningRoomTest(RealmType studentType) {
        Student student = new Student(studentType);
        int studentsBefore = schoolTest.getNumStudentsDiningRoom(studentType);
        try {
            Assertions.assertFalse(schoolTest.insertDiningRoom(student));
        } catch (FullDiningRoomException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(studentsBefore + 1, schoolTest.getNumStudentsDiningRoom(studentType));
    }

    @Test
    void insertDiningRoomWithCoinTest() {
        try {
            Assertions.assertFalse(schoolTest.insertDiningRoom(new Student(RealmType.YELLOW_GNOMES)));
            Assertions.assertFalse(schoolTest.insertDiningRoom(new Student(RealmType.YELLOW_GNOMES)));
            Assertions.assertTrue(schoolTest.insertDiningRoom(new Student(RealmType.YELLOW_GNOMES)));
            Assertions.assertEquals(3, schoolTest.getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        } catch (FullDiningRoomException e) {
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void moveFromEntranceToDiningRoomTest(RealmType studentType) {
        Student student = new Student(studentType);
        int previousStudents = schoolTest.getNumStudentsDiningRoom(studentType);
        schoolTest.insertEntrance(student);
        try {
            schoolTest.moveFromEntranceToDiningRoom(studentType);
            Assertions.assertEquals(previousStudents + 1, schoolTest.getNumStudentsDiningRoom(studentType));
        } catch(StudentNotFoundException | FullDiningRoomException e) {
            Assertions.fail();
        }
    }

    @Test
    void fullDiningRoomTest() {
        for (int i = 0; i < 10; i++) {
            try {
                schoolTest.insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
        Assertions.assertThrows(FullDiningRoomException.class,
                () -> schoolTest.insertDiningRoom(new Student(RealmType.YELLOW_GNOMES)));
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void professorTest(RealmType studentType) {
        Assertions.assertFalse(schoolTest.isProfessorPresent(studentType));
        schoolTest.insertProfessor(studentType);
        Assertions.assertTrue(schoolTest.isProfessorPresent(studentType));
        schoolTest.removeProfessor(studentType);
        Assertions.assertFalse(schoolTest.isProfessorPresent(studentType));
    }

    @Test
    void sendTowerToIslandTest() {
        Island island1 = new SingleIsland();
        Island island2 = new SingleIsland();
        Island island3 = new SingleIsland();
        Island islandGroup = new IslandGroup(island2, island3);
        schoolTest.sendTowerToIsland(island1);
        schoolTest.sendTowerToIsland(islandGroup);
        Assertions.assertEquals(schoolTest.getNumTowers(), 5);
        Assertions.assertNotNull(island1.getTowerType());
        Assertions.assertNotNull(islandGroup.getTowerType());
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void sendStudentToIsland(RealmType studentType) {
        Island island = new SingleIsland();
        int previousStudents = island.getNumStudentsOfType(studentType);
        schoolTest.insertEntrance(new Student(studentType));
        try {
            schoolTest.sendStudentToIsland(island, studentType);
        } catch (StudentNotFoundException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(previousStudents + 1, island.getNumStudentsOfType(studentType));
    }

    @Test
    void removeStudentEntranceTest() {
        Student student = new Student(RealmType.YELLOW_GNOMES);
        schoolTest.insertEntrance(student);
        try {
            schoolTest.removeStudentEntrance(RealmType.YELLOW_GNOMES);
        } catch (StudentNotFoundException e){
            Assertions.fail();
        }
        Assertions.assertThrows(StudentNotFoundException.class,
                () -> schoolTest.removeStudentEntrance(RealmType.YELLOW_GNOMES));
    }

    @Test
    void removeFromDiningRoomTest() {
        Student student = new Student(RealmType.YELLOW_GNOMES);
        schoolTest.insertEntrance(student);
        try {
            schoolTest.moveFromEntranceToDiningRoom(RealmType.YELLOW_GNOMES);
        } catch (StudentNotFoundException | FullDiningRoomException e) {
            Assertions.fail();
        }
        try {
            schoolTest.removeFromDiningRoom(RealmType.YELLOW_GNOMES);
        } catch (StudentNotFoundException e){
            Assertions.fail();
        }
        Assertions.assertThrows(StudentNotFoundException.class,
                () -> schoolTest.removeFromDiningRoom(RealmType.YELLOW_GNOMES));
    }
}