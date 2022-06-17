package it.polimi.ingsw.exceptions;

public class IllegalCharacterActionRequestedException extends Exception{

    public IllegalCharacterActionRequestedException(int characterId) {
        super("There was an error in the character " + characterId + " action request");
    }

    public IllegalCharacterActionRequestedException(Exception e) {
        super(e.getMessage());
    }

    public IllegalCharacterActionRequestedException(String customDescription) {
        super(customDescription);
    }
}
