package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.effects.GainInfluenceStrategy;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Character8Test extends TestCase {
    @Test
    void playCardTest() {
        Character8 character8 = (Character8) new CharacterCreator(null).getCharacter(8);
        Player player = new Player("player");
        for (int i = 0; i < character8.getPrice(); i++) player.insertCoin();
        try {
            character8.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof GainInfluenceStrategy);
        Assertions.assertEquals(2, player.getTurnEffect().getAdditionalInfluence());
    }
}