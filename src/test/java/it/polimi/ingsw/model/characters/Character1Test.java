package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Character1Test extends TestCase {
    Game game;
    CharacterCreator characterCreator;
    Character1 character1;

    @BeforeEach
    void setup() {
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < 12; i++) islands.add(new SingleIsland());
        game = new Game(islands, null); //clouds are not important for this class
        for (int i = 0; i < 120; i++) game.getBag().insertStudent(new Student(RealmType.values()[i /24]));
        characterCreator = new CharacterCreator(game);
        character1 = (Character1) characterCreator.getCharacter(1);
    }

    @Test
    void creationTest() {
        Assertions.assertEquals(1, character1.getId());
        Assertions.assertEquals(1, character1.getPrice());
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void useEffectTest(RealmType studentType) {
        int islandIndex = new Random().nextInt(game.getIslands().size());
        Island island = game.getIslands().get(islandIndex);
        int previousStudents = island.getNumStudentsOfType(studentType);
        String realmTypeAbbreviation = studentType.getAbbreviation();
        List<String> args = new ArrayList<>();
        args.add(realmTypeAbbreviation);
        args.add(Integer.toString(islandIndex));
        try {
            character1.useEffect(args);
            Assertions.assertEquals(previousStudents + 1, island.getNumStudentsOfType(studentType));
        } catch (IllegalCharacterActionRequestedException e) {
            //it's ok, because the student container initialization is random
        }
    }

    @Test
    void useEffectWithExceptionTest() {
        List<String> args = new ArrayList<>();
        args.add("A"); //Not a valid realm type abbreviation
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class,
                () -> character1.useEffect(args));
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void pickAndSendToIslandTest(RealmType studentType) {
        Island island = game.getIslands().get((new Random()).nextInt(game.getIslands().size()));
        int previousStudents = island.getNumStudentsOfType(studentType);
        try {
            character1.pickAndSendToIsland(studentType, island);
            Assertions.assertEquals(previousStudents + 1, island.getNumStudentsOfType(studentType));
        } catch (StudentNotFoundException e) {
            //it's ok, because the student container initialization is random
        }
    }


}