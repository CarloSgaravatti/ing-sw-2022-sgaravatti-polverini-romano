package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
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
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class Character11Test extends TestCase {
    Character11 character11;
    Player player;

    @BeforeEach
    void setupCharacter11() {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(2);
        Game game = new Character1Test.CharacterGameStub(null, gameConstants);
        CharacterCreator characterCreator = new CharacterCreator(game);
        game.createAllStudentsForBag();
        character11 = (Character11) characterCreator.getCharacter(11);
        player = new Player("player");
        player.setSchool(new School(8, TowerType.BLACK, gameConstants, player));
        for (int i = 0; i < character11.getPrice(); i++) player.insertCoin();
        try {
            character11.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void useEffectTest(RealmType studentType) {
        Map<String, Object> input = Map.of("Student", studentType);
        int expected = 0;
        Optional<Student> student = character11.getStudents().stream()
                .filter(s -> s.getStudentType() == studentType)
                .findAny();
        if (student.isPresent()) expected = 1;
        try {
            character11.useEffect(input);
        } catch (IllegalCharacterActionRequestedException e) {
            if (expected == 1) Assertions.fail();
        }
        Assertions.assertEquals(expected, player.getSchool().getNumStudentsDiningRoom(studentType));
    }

    @ParameterizedTest
    @EnumSource(value = RealmType.class, names = {"YELLOW_GNOMES", "BLUE_UNICORNS", "GREEN_FROGS", "PINK_FAIRES"})
    void useEffectStudentNotFound(RealmType studentType) {
        Map<String, Object> input = Map.of("Student", studentType);
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class, () -> character11.useEffect(input));
    }
}