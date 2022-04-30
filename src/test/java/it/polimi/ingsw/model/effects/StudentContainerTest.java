package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class StudentContainerTest extends TestCase {
    StudentContainer studentContainerToTest;
    GameConstants gameConstants;

    @BeforeEach
    void setupStudentContainerToTest() {
        studentContainerToTest = new StudentContainer();
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void pickStudentTest(RealmType realmType) {
        studentContainerToTest.insertStudent(new Student(realmType));
        try {
            Student student = studentContainerToTest.pickStudent(realmType, false);
            Assertions.assertEquals(realmType, student.getStudentType());
        } catch (StudentNotFoundException e) {
            Assertions.fail();
        }
        Assertions.assertThrows(StudentNotFoundException.class,
                () -> studentContainerToTest.pickStudent(realmType, false));
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void insertStudentTest(RealmType realmType) {
        Student student = new Student(realmType);
        studentContainerToTest.insertStudent(student);
        Assertions.assertTrue(studentContainerToTest.getStudents().contains(student));
    }

    @Test
    void initializationTest() {
        gameConstants = JsonUtils.constantsByNumPlayer(2);
        Game game = new Game(null, null, gameConstants);
        game.createAllStudentsForBag();
        studentContainerToTest = new StudentContainer(4, game);
        Assertions.assertEquals(4, studentContainerToTest.getStudents().size());
    }
}