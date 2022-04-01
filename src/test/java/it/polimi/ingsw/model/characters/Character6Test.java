package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCreator;
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

    @BeforeEach
    void setupCharacter4() {
        character6 = (Character6) CharacterCreator.getInstance().getCharacter(6);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void playCard(int motherNatureMovement) {
        Player player = new Player("player");
        player.getTurnEffect().incrementMotherNatureMovement(motherNatureMovement);
        character6.playCard(player);
        Assertions.assertTrue(player.getTurnEffect().getInfluenceStrategy() instanceof NoTowerInfluenceStrategy);
    }
}