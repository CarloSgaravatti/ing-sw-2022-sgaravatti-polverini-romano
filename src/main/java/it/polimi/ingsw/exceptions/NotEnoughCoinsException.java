package it.polimi.ingsw.exceptions;

/**
 * NotEnoughCoinsException is thrown when a client tries to play a character, but he hasn't got enough coins to play it
 */
public class NotEnoughCoinsException extends Exception {
    private final int characterId;

    public NotEnoughCoinsException() {
        characterId = 0;
    }

    public NotEnoughCoinsException(int characterId) {
        this.characterId = characterId;
    }

    @Override
    public String getMessage() {
        return "You don't have enough coins to play character " + characterId;
    }
}
