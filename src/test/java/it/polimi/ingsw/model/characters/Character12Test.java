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
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Character12Test extends TestCase {
    Character12 character12;
    Game game;

    @BeforeEach
    void setupCharacter12() {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(3);
        game = new Game(null, null, gameConstants, true);
        CharacterCreator characterCreator = new CharacterCreator(game);
        character12 = (Character12) characterCreator.getCharacter(12);
        game.setNumPlayers(3);
        game.addPlayer("player1");
        game.addPlayer("player2");
        game.addPlayer("player3");
        for (int i = 0; i < 3; i++) {
            Player player = game.getPlayers().get(i);
            player.setSchool(new School(6, TowerType.values()[i], gameConstants, player));
        }
    }

    @ParameterizedTest
    @CsvSource({"4, 3, 0", "7, 2, 2", "0, 0, 0", "3, 3, 3"})
    void useEffectTest(int numStudents1, int numStudents2, int numStudents3) {
        try {
            for (int i = 0; i < numStudents1; i++) {
                game.getPlayers().get(0).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            }
            for (int i = 0; i < numStudents2; i++) {
                game.getPlayers().get(1).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            }
            for (int i = 0; i < numStudents3; i++) {
                game.getPlayers().get(2).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            }
        } catch (FullDiningRoomException e) {
            Assertions.fail();
        }
        Map<String, Object> input = Map.of("Student", RealmType.YELLOW_GNOMES);
        character12.useEffect(input);
        Assertions.assertEquals(Math.max(numStudents1 - 3, 0),
                game.getPlayers().get(0).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(Math.max(numStudents2 - 3, 0),
                game.getPlayers().get(1).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(Math.max(numStudents3 - 3, 0),
                game.getPlayers().get(2).getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
    }
}