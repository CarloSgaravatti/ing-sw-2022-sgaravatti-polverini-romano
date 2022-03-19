package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;

public class CharacterCreator {
    public static CharacterCard getCharacter(int numCharacter) throws IllegalArgumentException{
        return switch (numCharacter) {
            case 1 -> Character1.getInstance();
            case 2 -> Character2.getInstance();
            case 3 -> Character3.getInstance();
            case 4 -> Character4.getInstance();
            case 5 -> Character5.getInstance();
            case 6 -> Character6.getInstance();
            case 7 -> Character7.getInstance();
            case 8 -> Character8.getInstance();
            case 9 -> Character9.getInstance();
            case 10 -> Character10.getInstance();
            case 11 -> Character11.getInstance();
            case 12 -> Character12.getInstance();
            default -> throw new IllegalArgumentException();
        };
    }
}
