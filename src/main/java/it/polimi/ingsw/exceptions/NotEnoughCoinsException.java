package it.polimi.ingsw.exceptions;

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
