package it.polimi.ingsw.model;

import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CharacterCreatorTest extends TestCase {
    CharacterCreator characterCreator;
    GameConstants gameConstants;

    @BeforeEach
    void setup() {
        gameConstants = JsonUtils.constantsByNumPlayer(3);
        Game game = new Game(null, null, gameConstants, true);
        characterCreator = new CharacterCreator(game);
        game.createAllStudentsForBag();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    void getCharacterTest(int characterId) {
        CharacterCard characterCard = characterCreator.getCharacter(characterId);
        Assertions.assertEquals(characterId, characterCard.getId());
    }
}