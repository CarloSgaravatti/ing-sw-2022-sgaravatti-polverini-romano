package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Character4Test extends TestCase {
    Character4 character4;
    Player player;

    @BeforeEach
    void setupCharacter4() {
        character4 = (Character4) new CharacterCreator(null).getCharacter(4);
        player = new Player("player");
        for (int i = 0; i < character4.getPrice(); i++) player.insertCoin();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void playCardTest(int motherNatureMovement) {
        player.getTurnEffect().incrementMotherNatureMovement(motherNatureMovement);
        try {
            character4.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(motherNatureMovement + 2, player.getTurnEffect().getMotherNatureMovement());
    }
}