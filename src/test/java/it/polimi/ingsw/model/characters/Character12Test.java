package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class Character12Test extends TestCase {
    Character12 character12;
    Game game;

    @BeforeEach
    void setupCharacter12() {
        game = new Game(null, null);
        CharacterCreator characterCreator = new CharacterCreator(game);
        character12 = (Character12) characterCreator.getCharacter(12);
        game.addPlayer("player1");
        game.addPlayer("player2");
        game.addPlayer("player3");
        for (int i = 0; i < 3; i++) {
            game.getPlayers().get(i).setSchool(new School(6, TowerType.values()[i]));
        }
    }

    @ParameterizedTest
    @CsvSource({"4, 3, 0"})
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