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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

class Character7Test extends TestCase {
    Character7 character7;
    Character1Test.CharacterGameStub game;
    Player player;

    @BeforeEach
    void setupCharacter7() {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(2);
        game = new Character1Test.CharacterGameStub(null, gameConstants);
        game.createAllStudentsForBag();
        CharacterCreator characterCreator = new CharacterCreator(game);
        character7 = (Character7) characterCreator.getCharacter(7);
        player = new Player("nick");
        player.setSchool(new School(8, TowerType.BLACK, gameConstants, player));
        for (int i = 0; i < character7.getPrice(); i++) player.insertCoin();
        try {
            character7.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void useEffect2Test(int numStudents) {
        RealmType[] fromEntrance = new RealmType[numStudents];
        RealmType[] toPickFromContainer = new RealmType[numStudents];
        int[] numFromContainerPerType = new int[RealmType.values().length];
        int[] numFromEntrancePerType = new int[RealmType.values().length];
        int[] alreadyPresentInCharactersPerType = new int[RealmType.values().length];
        int[] presentAtTheEndPerType = new int[RealmType.values().length];
        for (int i = 0; i < character7.getStudents().size(); i++) {
            alreadyPresentInCharactersPerType[character7.getStudents().get(i).getStudentType().ordinal()]++;
        }
        try {
            for (int i = 0; i < numStudents; i++) {
                Student student1 = character7.getStudents().get(i);
                Student student2 = game.getBag().pickStudent();
                toPickFromContainer[i] = student1.getStudentType();
                fromEntrance[i] = student2.getStudentType();
                player.getSchool().insertEntrance(new Student(fromEntrance[i]));
                numFromContainerPerType[toPickFromContainer[i].ordinal()]++;
                numFromEntrancePerType[fromEntrance[i].ordinal()]++;
            }
            Map<String, Object> input = Map.of("CharacterStudents", toPickFromContainer, "EntranceStudents", fromEntrance);
            character7.useEffect(input);
        } catch (EmptyBagException | IllegalCharacterActionRequestedException e) {
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
    void useEffectStudentOnCharacterNotFoundTest() {
        RealmType[] entranceStudents = new RealmType[] {RealmType.GREEN_FROGS, RealmType.YELLOW_GNOMES};
        //character contains only red dragons
        RealmType[] characterStudents = new RealmType[] {RealmType.BLUE_UNICORNS, RealmType.YELLOW_GNOMES};
        Map<String, Object> input = Map.of("CharacterStudents", characterStudents, "EntranceStudents", entranceStudents);
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class, () -> character7.useEffect(input));
        //character must contain all students that were there before
        character7.getStudents().forEach(s -> Assertions.assertEquals(RealmType.RED_DRAGONS, s.getStudentType()));
    }
}