package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;
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

class Character1Test extends TestCase {
    CharacterGameStub game;
    Character1 character1;

    public static class CharacterGameStub extends Game {

        public CharacterGameStub(List<Island> islands, GameConstants gameConstants) {
            super(islands, null, gameConstants, true);
        }

        @Override
        public void updateStudentContainer(StudentContainer studentContainer) {
            studentContainer.insertStudent(new Student(RealmType.RED_DRAGONS));
        }
    }

    @BeforeEach
    void setup() {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(3);
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < 12; i++) islands.add(new SingleIsland());
        game = new CharacterGameStub(islands, gameConstants);
        //for (int i = 0; i < 120; i++) game.getBag().insertStudent(new Student(RealmType.values()[i /24]));
        CharacterCreator characterCreator = new CharacterCreator(game);
        character1 = (Character1) characterCreator.getCharacter(1);
    }

    @ParameterizedTest
    @EnumSource(value = RealmType.class, names = {"RED_DRAGONS"})
    void useEffectTest(RealmType studentType) {
        Island island = game.getIslands().get(new Random().nextInt(game.getIslands().size()));
        int previousStudents = island.getNumStudentsOfType(studentType);
        Map<String, Object> input = new HashMap<>();
        input.put("Student", studentType);
        input.put("Island", island);
        try {
            character1.useEffect(input);
            Assertions.assertEquals(previousStudents + 1, island.getNumStudentsOfType(studentType));
        } catch (IllegalCharacterActionRequestedException e) {
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @EnumSource(value = RealmType.class, names = {"YELLOW_GNOMES", "BLUE_UNICORNS", "GREEN_FROGS", "PINK_FAIRES"})
    void useEffectStudentNotFoundTest(RealmType studentType) {
        Island island = game.getIslands().get(new Random().nextInt(game.getIslands().size()));
        Map<String, Object> input = new HashMap<>();
        input.put("Student", studentType);
        input.put("Island", island);
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class, () -> character1.useEffect(input));
    }
}