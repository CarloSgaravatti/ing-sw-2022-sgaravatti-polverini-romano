package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.EmptyBagException;
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
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Character7Test extends TestCase {
    Character7 character7;
    Game game;
    Player player;
    GameConstants gameConstants;

    @BeforeEach
    void setupCharacter7() {
        gameConstants = JsonUtils.constantsByNumPlayer(2);
        game = new Game(null, null, gameConstants);
        game.createAllStudentsForBag();
        CharacterCreator characterCreator = new CharacterCreator(game);
        character7 = (Character7) characterCreator.getCharacter(7);
        player = new Player("nick");
        player.setSchool(new School(8, TowerType.BLACK));
        for (int i = 0; i < character7.getPrice(); i++) player.insertCoin();
        try {
            character7.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
    }

    @Test
    void useEffectTest() {
        List<Student> studentsPresent = character7.getStudents();
        List<String> args = new ArrayList<>();
        int[] alreadyPresentInCharactersPerType = new int[RealmType.values().length];
        int[] presentAtTheEndPerType = new int[RealmType.values().length];
        int[] numFromContainerPerType = new int[RealmType.values().length];
        for (int i = 0; i < character7.getStudents().size(); i++) {
            alreadyPresentInCharactersPerType[character7.getStudents().get(i).getStudentType().ordinal()]++;
        }
        args.add(Integer.toString(3));
        for (int i = 0; i < 3; i++) {
            args.add(studentsPresent.get(i).getStudentType().getAbbreviation());
            numFromContainerPerType[character7.getStudents().get(i).getStudentType().ordinal()]++;
        }
        for (int i = 0; i < 3; i++) {
            args.add("Y");
            player.getSchool().insertEntrance(new Student(RealmType.YELLOW_GNOMES));
        }
        try {
            character7.useEffect(args);
        } catch (IllegalCharacterActionRequestedException e) {
            Assertions.fail();
        }
        for (int i = 0; i < character7.getStudents().size(); i++) {
            presentAtTheEndPerType[character7.getStudents().get(i).getStudentType().ordinal()]++;
        }
        Assertions.assertEquals(3 + alreadyPresentInCharactersPerType[RealmType.YELLOW_GNOMES.ordinal()]
                - numFromContainerPerType[RealmType.YELLOW_GNOMES.ordinal()],
                presentAtTheEndPerType[RealmType.YELLOW_GNOMES.ordinal()]);
        Assertions.assertEquals(numFromContainerPerType[RealmType.YELLOW_GNOMES.ordinal()],
                player.getSchool().getStudentsEntrance(RealmType.YELLOW_GNOMES));
        for (int i = 1; i < RealmType.values().length; i++) {
            Assertions.assertEquals(alreadyPresentInCharactersPerType[i] - numFromContainerPerType[i],
                    presentAtTheEndPerType[i]);
            Assertions.assertEquals(numFromContainerPerType[i],
                    player.getSchool().getStudentsEntrance(RealmType.values()[i]));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 7})
    void useEffectExceptionTest(int toPick) {
        List<String> args = new ArrayList<>();
        args.add(Integer.toString(toPick));
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class,
                () -> character7.useEffect(args));
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
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> character7.pickAndSwapStudents(toPick, fromEntrance));
    }
}