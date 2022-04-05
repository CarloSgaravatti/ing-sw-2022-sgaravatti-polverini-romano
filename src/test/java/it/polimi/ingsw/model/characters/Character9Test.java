package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.RealmType;
import it.polimi.ingsw.model.effects.NoStudentInfluenceStrategy;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class Character9Test extends TestCase {
    Character9 character9;

    @BeforeEach
    void setupCharacter9() {
        character9 = (Character9) CharacterCreator.getInstance().getCharacter(9);
    }

    @ParameterizedTest
    @EnumSource(RealmType.class)
    void chooseStudentTypeTest(RealmType realmType) {
        Player player = new Player("player");
        for (int i = 0; i < character9.getPrice(); i++) player.insertCoin();
        try {
            character9.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        character9.chooseStudentType(realmType);
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof NoStudentInfluenceStrategy);
        NoStudentInfluenceStrategy noStudent = (NoStudentInfluenceStrategy) player.getTurnEffect().getInfluenceStrategy();
        Assertions.assertEquals(realmType, noStudent.getStudentType());
    }
}