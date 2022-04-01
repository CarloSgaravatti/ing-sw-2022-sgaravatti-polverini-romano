package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;

public class CharacterCreator {
    private Game game;
    private static CharacterCreator instance;

    //To avoid accessing to the default constructor
    private CharacterCreator() {
    }

    public static CharacterCreator getInstance() {
        if (instance == null) instance =  new CharacterCreator();
        return instance;
    }

    public CharacterCard getCharacter(int numCharacter) throws IllegalArgumentException {
        return switch (numCharacter) {
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

    public void setGame(Game game) {
        this.game = game;
    }
}
