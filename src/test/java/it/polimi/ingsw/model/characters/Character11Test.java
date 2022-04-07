package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class Character11Test extends TestCase {
    Character11 character11;
    Game game;

    @BeforeEach
    void setupCharacter11() {
        Game game = new Game(null, null);
        CharacterCreator characterCreator = new CharacterCreator(game);
        game.createAllStudentsForBag();
        character11 = (Character11) characterCreator.getCharacter(11);
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void pickAndSendToDiningRoomTest(RealmType studentType) {
        Player player = new Player("player");
        int expected = 0;
        player.setSchool(new School(8, TowerType.BLACK));
        for (int i = 0; i < character11.getPrice(); i++) player.insertCoin();
        try {
            character11.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Optional<Student> student = character11.getStudents().stream()
                .filter(s -> s.getStudentType() == studentType)
                .findAny();
        if (student.isPresent()) expected = 1;
        try {
            character11.pickAndSendToDiningRoom(studentType);
        } catch (StudentNotFoundException e) {
            if (expected == 1) Assertions.fail();
        } catch (FullDiningRoomException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(expected, player.getSchool().getNumStudentsDiningRoom(studentType));
    }
}