package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.effects.GainInfluenceStrategy;
import it.polimi.ingsw.model.effects.NoTowerInfluenceStrategy;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Class CharacterCreator is used to create a character card  instance using
 * factory method pattern.
 */
public class CharacterCreator {
    private final Game game;
    private static final int MOTHER_NATURE_INCREMENT = 2;
    private static final int ADDITIONAL_INFLUENCE = 2;

    /**
     * Construct a CharacterCreator that is associated to the specified game
     * @param game the game on which the CharacterCreator will create the character card
     */
    public CharacterCreator(Game game) {
        this.game = game;
    }

    /**
     * Method getCharacter is the factory method which returns the character card that have the
     * specified character id.
     * @param characterId the character card id
     * @return the character card instance of the specified id
     * @throws IllegalArgumentException if it doesn't exist a character card with the specified id
     */
    public CharacterCard getCharacter(int characterId) throws IllegalArgumentException {
        return switch (characterId) {
            case 2, 4, 6, 8 -> getCharacterWithoutInput(characterId);
            case 1 -> new Character1(game);
            case 3 -> new Character3(game);
            case 5 -> new Character5(game);
            case 7 -> new Character7(game);
            case 9 -> new Character9();
            case 10 -> new Character10();
            case 11 -> new Character11(game);
            case 12 -> new Character12(game);
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Creates a character with the specified id that doesn't require any input from a player to be played in the turn
     * (these characters are 2, 4, 6 and 8). The method define also the effect (in base of the character id) that the created
     * character will have.
     * @param characterId the id of the character that will be created
     * @return the created character
     * @throws IllegalArgumentException if the id is not valid (if it isn't 2, 4, 6 or 8)
     */
    private CharacterWithoutInput getCharacterWithoutInput(int characterId) throws IllegalArgumentException {
        Consumer<Player> action = getCharacterWithNoInputAction(characterId);
        return switch (characterId) {
            case 2 -> new CharacterWithoutInput(2, 2, action);
            case 4 -> new CharacterWithoutInput(1, 4, action);
            case 6 -> new CharacterWithoutInput(3, 6, action);
            case 8 -> new CharacterWithoutInput(2, 8, action);
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Returns the character action that is associated to a character with no input that have the specified id. This can
     * be used both to create the character and to deserialize correctly a character.
     *
     * @param characterId the id of the character with no input
     * @return the character action that is associated to a character with no input that have the specified id
     */
    public Consumer<Player> getCharacterWithNoInputAction(int characterId) {
        return switch (characterId) {
            case 2 -> player -> player.getTurnEffect().setProfessorPrecedence(true);
            case 4 -> player -> player.getTurnEffect()
                        .incrementMotherNatureMovement(MOTHER_NATURE_INCREMENT, true);
            case 6 -> player -> player.getTurnEffect()
                        .setInfluenceStrategy(new NoTowerInfluenceStrategy(player.getTurnEffect().getInfluenceStrategy()));
            case 8 -> player -> {
                    player.getTurnEffect()
                            .setInfluenceStrategy(new GainInfluenceStrategy(player.getTurnEffect().getInfluenceStrategy()));
                    player.getTurnEffect().setAdditionalInfluence(ADDITIONAL_INFLUENCE);
            };
            default -> throw new IllegalArgumentException();
        };
    }
}
