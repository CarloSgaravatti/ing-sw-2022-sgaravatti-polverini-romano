package it.polimi.ingsw.model.characters;

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
        character2 = (Character2) CharacterCreator.getInstance().getCharacter(2);
    }

    @Test
    void playCard() {
        Player player = new Player("player");
        character2.playCard(player);
        Assertions.assertTrue(player.getTurnEffect().isProfessorPrecedence());
    }
}