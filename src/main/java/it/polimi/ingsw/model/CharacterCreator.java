package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;

/**
 * Class CharacterCreator is used to obtain a character card instance using
 * factory method pattern. This is a Singleton class.
 */
public class CharacterCreator {
    private Game game;
    private static CharacterCreator instance;

    //To avoid accessing to the default constructor
    private CharacterCreator() {
    }

    /**
     * Static method getInstance returns an instance of CharacterCreator, if the instance doesn't exist
     * this method creates it.
     * @return the CharacterCreator instance
     */
    public static CharacterCreator getInstance() {
        if (instance == null) instance =  new CharacterCreator();
        return instance;
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
            case 1 -> Character1.getInstance(game);
            case 2 -> Character2.getInstance();
            case 3 -> Character3.getInstance(game);
            case 4 -> Character4.getInstance();
            case 5 -> Character5.getInstance();
            case 6 -> Character6.getInstance();
            case 7 -> Character7.getInstance(game);
            case 8 -> Character8.getInstance();
            case 9 -> Character9.getInstance();
            case 10 -> Character10.getInstance();
            case 11 -> Character11.getInstance(game);
            case 12 -> Character12.getInstance(game);
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Method setGame sets the game which the CharacterCreator refers to, this is useful
     * because some character cards needs to associated to the game in order to accomplish their task
     * @param game the game to be set
     */
    public void setGame(Game game) {
        this.game = game;
    }
}
