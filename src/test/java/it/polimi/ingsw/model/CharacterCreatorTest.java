package it.polimi.ingsw.model;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCreatorTest extends TestCase {
    CharacterCreator characterCreator;

    @BeforeEach
    void setup() {
        Game game = new Game(null, null);
        characterCreator = new CharacterCreator(game);
        game.createAllStudentsForBag();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    void getCharacterTest(int characterId) {
        CharacterCard characterCard = characterCreator.getCharacter(characterId);
        Assertions.assertEquals("it.polimi.ingsw.model.characters.Character" + characterId,
                characterCard.getClass().getName());
    }
}