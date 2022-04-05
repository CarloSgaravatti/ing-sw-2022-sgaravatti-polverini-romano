package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class Character4Test extends TestCase {
    Character4 character4;

    @BeforeEach
    void setupCharacter4() {
        character4 = (Character4) CharacterCreator.getInstance().getCharacter(4);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void playCardTest(int motherNatureMovement) {
        Player player = new Player("player");
        player.getTurnEffect().incrementMotherNatureMovement(motherNatureMovement);
        for (int i = 0; i < character4.getPrice(); i++) player.insertCoin();
        try {
            character4.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(motherNatureMovement + 2, player.getTurnEffect().getMotherNatureMovement());
    }
}