package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

class Character12Test extends TestCase {
    Character12 character12;
    Game game;
    GameConstants gameConstants;

    @BeforeEach
    void setupCharacter12() {
        gameConstants = JsonUtils.constantsByNumPlayer(3);
        game = new Game(null, null, gameConstants);
        CharacterCreator characterCreator = new CharacterCreator(game);
        character12 = (Character12) characterCreator.getCharacter(12);
        game.setNumPlayers(3);
        game.addPlayer("player1");
        game.addPlayer("player2");
        game.addPlayer("player3");
        for (int i = 0; i < 3; i++) {
            game.getPlayers().get(i).setSchool(new School(6, TowerType.values()[i], gameConstants));
        }
    }

    @Test
    void useEffectTest() {
        List<String> args = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            try {
                game.getPlayers().get(0).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
        for (int i = 0; i < 1; i++) {
            try {
                game.getPlayers().get(1).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
        args.add("Y");
        try {
            character12.useEffect(args);
        } catch (IllegalCharacterActionRequestedException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(0,
                game.getPlayers().get(0).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(0,
                game.getPlayers().get(1).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(0,
                game.getPlayers().get(2).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
    }

    @Test
    void useEffectExceptionTest() {
        List<String> args = new ArrayList<>();
        args.add("A"); //Not a valid realm type abbreviation
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class,
                () -> character12.useEffect(args));
    }

    @ParameterizedTest
    @CsvSource({"4, 3, 0", "7, 2, 2", "0, 0, 0", "3, 3, 3"})
    void removeStudentsFromDiningRoomTest(int numStudents1, int numStudents2, int numStudents3) {
        for (int i = 0; i < numStudents1; i++) {
            try {
                game.getPlayers().get(0).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
        for (int i = 0; i < numStudents2; i++) {
            try {
                game.getPlayers().get(1).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
        for (int i = 0; i < numStudents3; i++) {
            try {
                game.getPlayers().get(2).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
        try {
            character12.removeStudentsFromDiningRoom(RealmType.YELLOW_GNOMES);
        } catch (StudentNotFoundException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(Math.max(numStudents1 - 3, 0),
                game.getPlayers().get(0).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(Math.max(numStudents2 - 3, 0),
                game.getPlayers().get(1).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(Math.max(numStudents3 - 3, 0),
                game.getPlayers().get(2).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
    }
}