package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.School;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class Character10Test extends TestCase {
    Character10 character10;
    Player player;

    @BeforeEach
    void setupCharacter10() {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(2);
        CharacterCreator characterCreator = new CharacterCreator(null);
        character10 = (Character10) characterCreator.getCharacter(10);
        player = new Player("player");
        player.setSchool(new School(8, TowerType.BLACK, gameConstants, player));
        for (int i = 0; i < character10.getPrice(); i++) player.insertCoin();
        try {
            character10.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        for (int i = 0; i < 2; i++) {
            player.getSchool().insertEntrance(new Student(RealmType.YELLOW_GNOMES));
            try {
                player.getSchool().insertDiningRoom(new Student(RealmType.BLUE_UNICORNS));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
    }

    @Test
    void useEffectTest() {
        RealmType[] entrance = new RealmType[] {RealmType.YELLOW_GNOMES, RealmType.YELLOW_GNOMES};
        RealmType[] diningRoom = new RealmType[] {RealmType.BLUE_UNICORNS, RealmType.BLUE_UNICORNS};
        Map<String, Object> input = Map.of("EntranceStudents", entrance, "DiningRoomStudents", diningRoom);
        try {
            character10.useEffect(input);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(2, player.getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(2, player.getSchool().getStudentsEntrance(RealmType.BLUE_UNICORNS));
        Assertions.assertEquals(0, player.getSchool().getStudentsEntrance(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(0, player.getSchool().getNumStudentsDiningRoom(RealmType.BLUE_UNICORNS));
    }

    @Test
    void useEffectEntranceStudentsNotFound() {
        //entrance does not have red dragons
        RealmType[] entrance = new RealmType[] {RealmType.RED_DRAGONS, RealmType.RED_DRAGONS};
        RealmType[] diningRoom = new RealmType[] {RealmType.BLUE_UNICORNS, RealmType.BLUE_UNICORNS};
        Map<String, Object> input = Map.of("EntranceStudents", entrance, "DiningRoomStudents", diningRoom);
        try {
            character10.useEffect(input);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(2, player.getSchool().getStudentsEntrance(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(2, player.getSchool().getNumStudentsDiningRoom(RealmType.BLUE_UNICORNS));
        Assertions.assertEquals(0, player.getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(0, player.getSchool().getStudentsEntrance(RealmType.BLUE_UNICORNS));
        Assertions.assertEquals(0, player.getSchool().getStudentsEntrance(RealmType.RED_DRAGONS));
        Assertions.assertEquals(0, player.getSchool().getNumStudentsDiningRoom(RealmType.RED_DRAGONS));
    }

    @Test
    void useEffectDiningRoomStudentsNotFound() {
        //dining does not have green frogs
        RealmType[] entrance = new RealmType[] {RealmType.YELLOW_GNOMES, RealmType.YELLOW_GNOMES};
        RealmType[] diningRoom = new RealmType[] {RealmType.GREEN_FROGS, RealmType.GREEN_FROGS};
        Map<String, Object> input = Map.of("EntranceStudents", entrance, "DiningRoomStudents", diningRoom);
        try {
            character10.useEffect(input);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(2, player.getSchool().getStudentsEntrance(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(2, player.getSchool().getNumStudentsDiningRoom(RealmType.BLUE_UNICORNS));
        Assertions.assertEquals(0, player.getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(0, player.getSchool().getStudentsEntrance(RealmType.BLUE_UNICORNS));
        Assertions.assertEquals(0, player.getSchool().getStudentsEntrance(RealmType.GREEN_FROGS));
        Assertions.assertEquals(0, player.getSchool().getNumStudentsDiningRoom(RealmType.GREEN_FROGS));
    }
}