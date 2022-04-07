package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;

/**
 * Class CharacterCreator is used to obtain a character card instance using
 * factory method pattern.
 */
public class CharacterCreator {
    private final Game game;

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
            case 1 -> new Character1(game);
            case 2 -> new Character2();
            case 3 -> new Character3(game);
            case 4 -> new Character4();
            case 5 -> new Character5(game);
            case 6 -> new Character6();
            case 7 -> new Character7(game);
            case 8 -> new Character8();
            case 9 -> new Character9();
            case 10 -> new Character10();
            case 11 -> new Character11(game);
            case 12 -> new Character12(game);
            default -> throw new IllegalArgumentException();
        };
    }
}
