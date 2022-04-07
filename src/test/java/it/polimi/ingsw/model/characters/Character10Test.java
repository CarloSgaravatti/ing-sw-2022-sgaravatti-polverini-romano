package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Character10Test extends TestCase {
    Character10 character10;

    @BeforeEach
    void setupCharacter10() {
        CharacterCreator characterCreator = new CharacterCreator(null);
        character10 = (Character10) characterCreator.getCharacter(10);
    }

    @Test
    void swapTest() {
        Player player = new Player("player");
        player.setSchool(new School(8, TowerType.BLACK));
        RealmType[] entrance = new RealmType[2];
        RealmType[] diningRoom = new RealmType[2];
        for (int i = 0; i < 2; i++) {
            entrance[i] = RealmType.YELLOW_GNOMES;
            diningRoom[i] = RealmType.BLUE_UNICORNS;
            player.getSchool().insertEntrance(new Student(RealmType.YELLOW_GNOMES));
            try {
                player.getSchool().insertDiningRoom(new Student(RealmType.BLUE_UNICORNS));
            } catch (FullDiningRoomException e) {
                Assertions.fail();
            }
        }
        for (int i = 0; i < character10.getPrice(); i++) player.insertCoin();
        try {
            character10.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        try {
            character10.swap(entrance, diningRoom);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(2, player.getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(2, player.getSchool().getStudentsEntrance(RealmType.BLUE_UNICORNS));
        Assertions.assertEquals(0, player.getSchool().getStudentsEntrance(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(0, player.getSchool().getNumStudentsDiningRoom(RealmType.BLUE_UNICORNS));
    }

    @Test
    void swapDifferentLengthExceptionTest() {
        RealmType[] entrance = new RealmType[2];
        RealmType[] diningRoom = new RealmType[1];
        Arrays.fill(entrance, RealmType.BLUE_UNICORNS);
        Arrays.fill(diningRoom, RealmType.YELLOW_GNOMES);
        try {
            character10.swap(entrance, diningRoom);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            //Test passed
        } catch (StudentNotFoundException | FullDiningRoomException e) {
            Assertions.fail();
        }
    }

    @Test
    void swapWrongLengthExceptionTest() {
        RealmType[] entrance = new RealmType[5];
        RealmType[] diningRoom = new RealmType[5];
        Arrays.fill(entrance, RealmType.BLUE_UNICORNS);
        Arrays.fill(diningRoom, RealmType.YELLOW_GNOMES);
        try {
            character10.swap(entrance, diningRoom);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            //Test passed
        } catch (StudentNotFoundException | FullDiningRoomException e) {
            Assertions.fail();
        }
    }
}