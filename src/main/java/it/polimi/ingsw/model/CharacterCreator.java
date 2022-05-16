package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.effects.GainInfluenceStrategy;
import it.polimi.ingsw.model.effects.NoTowerInfluenceStrategy;

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
            //case 2 -> new Character2();
            case 3 -> new Character3(game);
            //case 4 -> new Character4();
            case 5 -> new Character5(game);
            //case 6 -> new Character6();
            case 7 -> new Character7(game);
            //case 8 -> new Character8();
            case 9 -> new Character9();
            case 10 -> new Character10();
            case 11 -> new Character11(game);
            case 12 -> new Character12(game);
            default -> throw new IllegalArgumentException();
        };
    }

    private CharacterWithoutInput getCharacterWithoutInput(int characterId) throws IllegalArgumentException {
        Consumer<Player> action;
        return switch (characterId) {
            case 2 -> {
                action = player -> player.getTurnEffect().setProfessorPrecedence(true);
                yield new CharacterWithoutInput(2, 2, action);
            }
            case 4 -> {
                action = player -> player.getTurnEffect()
                        .incrementMotherNatureMovement(MOTHER_NATURE_INCREMENT);
                yield new CharacterWithoutInput(1, 4, action);
            }
            case 6 -> {
                action = player -> player.getTurnEffect()
                        .setInfluenceStrategy(new NoTowerInfluenceStrategy(player.getTurnEffect().getInfluenceStrategy()));
                yield new CharacterWithoutInput(3, 6, action);
            }
            case 8 -> {
                action = player -> {
                    player.getTurnEffect()
                            .setInfluenceStrategy(new GainInfluenceStrategy(player.getTurnEffect().getInfluenceStrategy()));
                    player.getTurnEffect().setAdditionalInfluence(ADDITIONAL_INFLUENCE);
                };
                yield new CharacterWithoutInput(2, 8, action);
            }
            default -> throw new IllegalArgumentException();
        };
    }
}
