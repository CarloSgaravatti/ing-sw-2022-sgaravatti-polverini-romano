package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.effects.NoStudentInfluenceStrategy;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Character9Test extends TestCase {
    Character9 character9;
    Player player;

    @BeforeEach
    void setupCharacter9() {
        character9 = (Character9) new CharacterCreator(null).getCharacter(9);
        player = new Player("player");
        for (int i = 0; i < character9.getPrice(); i++) player.insertCoin();
        try {
            character9.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void useEffect2Test(RealmType studentType) {
        Map<String, Object> input = Map.of("Student", studentType);
        character9.useEffect(input);
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof NoStudentInfluenceStrategy);
        NoStudentInfluenceStrategy noStudent = (NoStudentInfluenceStrategy) player.getTurnEffect().getInfluenceStrategy();
        Assertions.assertEquals(studentType, noStudent.getStudentType());
    }
}