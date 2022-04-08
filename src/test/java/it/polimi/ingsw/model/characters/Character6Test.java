package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.effects.NoTowerInfluenceStrategy;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class Character6Test extends TestCase {
    Character6 character6;
    Player player;

    @BeforeEach
    void setupCharacter6() {
        character6 = (Character6) new CharacterCreator(null).getCharacter(6);
        player = new Player("player");
        for (int i = 0; i < character6.getPrice(); i++) player.insertCoin();
    }

    @Test
    void playCardTest() {
        try {
            character6.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof NoTowerInfluenceStrategy);
    }
}