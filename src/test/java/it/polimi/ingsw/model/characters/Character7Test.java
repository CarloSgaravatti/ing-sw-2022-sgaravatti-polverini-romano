package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Character7Test extends TestCase {
    Character7 character7;
    Game game;

    @BeforeEach
    void setupCharacter7() {
        game = new Game(null, null);
        game.createAllStudentsForBag();
        CharacterCreator characterCreator = new CharacterCreator(game);
        character7 = (Character7) characterCreator.getCharacter(7);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void pickAndSwapStudentsTest(int numStudents) {
        RealmType[] fromEntrance = new RealmType[numStudents];
        RealmType[] toPickFromContainer = new RealmType[numStudents];
        int[] numFromContainerPerType = new int[RealmType.values().length];
        int[] numFromEntrancePerType = new int[RealmType.values().length];
        int[] alreadyPresentInCharactersPerType = new int[RealmType.values().length];
        int[] presentAtTheEndPerType = new int[RealmType.values().length];
        Player player = new Player("nick");
        player.setSchool(new School(8, TowerType.BLACK));
        for (int i = 0; i < character7.getStudents().size(); i++) {
            alreadyPresentInCharactersPerType[character7.getStudents().get(i).getStudentType().ordinal()]++;
        }
        for (int i = 0; i < numStudents; i++) {
            try {
                Student student1 = character7.getStudents().get(i);
                Student student2 = game.getBag().pickStudent();
                toPickFromContainer[i] = student1.getStudentType();
                fromEntrance[i] = student2.getStudentType();
                player.getSchool().insertEntrance(new Student(fromEntrance[i]));
                numFromContainerPerType[toPickFromContainer[i].ordinal()]++;
                numFromEntrancePerType[fromEntrance[i].ordinal()]++;
            } catch (EmptyBagException e) {
                Assertions.fail();
            }
        }
        for (int i = 0; i < character7.getPrice(); i++) player.insertCoin();
        try {
            character7.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        try {
            character7.pickAndSwapStudents(toPickFromContainer, fromEntrance);
        } catch (StudentNotFoundException e) {
            Assertions.fail();
        }
        for (int i = 0; i < character7.getStudents().size(); i++) {
            presentAtTheEndPerType[character7.getStudents().get(i).getStudentType().ordinal()]++;
        }
        for (RealmType r: RealmType.values()) {
            int index = r.ordinal();
            Assertions.assertEquals(alreadyPresentInCharactersPerType[index] - numFromContainerPerType[index]
                    + numFromEntrancePerType[index],
                    presentAtTheEndPerType[r.ordinal()]);
            Assertions.assertEquals(numFromContainerPerType[index], player.getSchool().getStudentsEntrance(r));
        }
    }

    @Test
    void pickAndSwapStudentsArgumentsExceptionTest() {
        RealmType[] fromEntrance = new RealmType[5];
        RealmType[] toPick = new RealmType[3];
        Random rnd = new Random();
        for (int i = 0; i < 3; i++) toPick[i] = RealmType.values()[rnd.nextInt(RealmType.values().length)];
        for (int i = 0; i < 5; i++) fromEntrance[i] = RealmType.values()[rnd.nextInt(RealmType.values().length)];
        try {
            character7.pickAndSwapStudents(toPick, fromEntrance);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            //test passed
        } catch (StudentNotFoundException e) {
            Assertions.fail();
        }
    }
}