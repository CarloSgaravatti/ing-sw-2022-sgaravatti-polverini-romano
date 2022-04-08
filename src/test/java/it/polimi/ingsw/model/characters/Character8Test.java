package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.effects.GainInfluenceStrategy;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Character8Test extends TestCase {
    Character8 character8;
    Player player;

    @BeforeEach
    void setup() {
        character8 = (Character8) new CharacterCreator(null).getCharacter(8);
        player = new Player("player");
        for (int i = 0; i < character8.getPrice(); i++) player.insertCoin();
    }

    @Test
    void playCardTest() {
        try {
            character8.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof GainInfluenceStrategy);
        Assertions.assertEquals(2, player.getTurnEffect().getAdditionalInfluence());
    }
}