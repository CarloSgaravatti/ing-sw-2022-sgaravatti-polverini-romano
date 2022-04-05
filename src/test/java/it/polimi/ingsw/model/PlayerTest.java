package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.exceptions.StudentsNumberInCloudException;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class PlayerTest extends TestCase {
    Player playerToTest;

    @BeforeEach
    void setupPlayer() {
        playerToTest = new Player("");
    }

    @ParameterizedTest
    @ArgumentsSource(AssistantArgumentProvider.class)
    void playAssistantTest(int assistant, boolean expected, int[] assistants) {
        List<Assistant> assistantsPlayer = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            assistantsPlayer.add(new Assistant(i, 0, null)); //MNM not important
        }
        playerToTest.setAssistants(assistantsPlayer);
        List<Integer> assistantPlayed = new ArrayList<>();
        for (int i: assistants) assistantPlayed.add(i);
        try {
            Assertions.assertEquals(playerToTest.playAssistant(assistant, assistantPlayed), expected);
        } catch (NoSuchAssistantException e) {
            Assertions.fail();
        }
    }

    @Test
    void pickFromCloudTest() {
        Cloud cloud = new Cloud(3);
        Student[] students = new Student[3];
        Arrays.fill(students, new Student(RealmType.YELLOW_GNOMES));
        try {
            cloud.insertStudents(students);
        } catch (StudentsNumberInCloudException e) {
            Assertions.fail();
        }
        playerToTest.setSchool(new School(8, TowerType.BLACK));
        try {
            playerToTest.pickFromCloud(cloud);
        } catch (EmptyCloudException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(3, playerToTest.getSchool().getStudentsEntrance(RealmType.YELLOW_GNOMES));
    }

    @Test
    void resetTurnEffectTest() {
        playerToTest.getTurnEffect().incrementMotherNatureMovement(5);
        playerToTest.getTurnEffect().setOrderPrecedence(7);
        playerToTest.getTurnEffect().setProfessorPrecedence(true);
        playerToTest.resetTurnEffect();
        Assertions.assertFalse(playerToTest.getTurnEffect().isProfessorPrecedence());
        Assertions.assertEquals(0, playerToTest.getTurnEffect().getMotherNatureMovement());
        Assertions.assertEquals(0, playerToTest.getTurnEffect().getOrderPrecedence());
    }

    @Test
    void coinsTest() {
        Assertions.assertEquals(1, playerToTest.getNumCoins());
        playerToTest.insertCoin();
        Assertions.assertEquals(2, playerToTest.getNumCoins());
        try {
            playerToTest.removeCoins(2);
            Assertions.assertEquals(0, playerToTest.getNumCoins());
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        try {
            playerToTest.removeCoins(2);
            Assertions.fail();
        } catch (NotEnoughCoinsException e) {
            //test passed
        }
    }
}

class AssistantArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(1, true, new int[]{2, 3}),
                Arguments.of(2, false, new int[]{2, 3}),
                Arguments.of(5, true, new int[]{7, 10})
        );
    }
}