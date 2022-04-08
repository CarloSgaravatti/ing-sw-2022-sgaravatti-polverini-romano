package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Player;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Character2Test extends TestCase {
    Character2 character2;

    @BeforeEach
    void setupCharacter2() {
        character2 = (Character2) new CharacterCreator(null).getCharacter(2);
    }

    @Test
    void playCardTest() {
        Player player = new Player("player");
        for (int i = 0; i < character2.getPrice(); i++) player.insertCoin();
        try {
            character2.playCard(player);
        } catch (NotEnoughCoinsException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(player.getTurnEffect().isProfessorPrecedence());
    }
}