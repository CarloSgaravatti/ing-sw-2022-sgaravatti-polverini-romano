package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.effects.GainInfluenceStrategy;
import it.polimi.ingsw.model.effects.NoTowerInfluenceStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CharacterWithoutInputTest {
    Player player;

    @BeforeEach
    void setup () {
        player = new Player("player");
        for (int i = 0; i < 5; i++) player.insertCoin(); //5 will cover all character prices
    }

    @Test
    void character2Test() {
        CharacterWithoutInput character2 = (CharacterWithoutInput) new CharacterCreator(null).getCharacter(2);
        try {
            character2.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(player.getTurnEffect().isProfessorPrecedence());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void character4Test(int motherNatureMovement) {
        CharacterWithoutInput character4 = (CharacterWithoutInput) new CharacterCreator(null).getCharacter(4);
        player.getTurnEffect().incrementMotherNatureMovement(motherNatureMovement, false);
        try {
            character4.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(motherNatureMovement + 2, player.getTurnEffect().getMotherNatureMovement());
    }

    @Test
    void character6Test() {
        CharacterWithoutInput character6 = (CharacterWithoutInput) new CharacterCreator(null).getCharacter(6);
        try {
            character6.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof NoTowerInfluenceStrategy);
    }

    @Test
    void character8Test() {
        CharacterWithoutInput character8 = (CharacterWithoutInput) new CharacterCreator(null).getCharacter(8);
        try {
            character8.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof GainInfluenceStrategy);
        Assertions.assertEquals(2, player.getTurnEffect().getAdditionalInfluence());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6, 8})
    void useEffectTest(int characterId) {
        CharacterWithoutInput character = (CharacterWithoutInput) new CharacterCreator(null).getCharacter(characterId);
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class, () -> character.useEffect(Map.of()));
    }
}