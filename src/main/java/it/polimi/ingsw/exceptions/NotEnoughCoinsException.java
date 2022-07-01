package it.polimi.ingsw.exceptions;

/**
 * NotEnoughCoinsException is thrown when a client tries to play a character, but he hasn't got enough coins to play it
 */
public class NotEnoughCoinsException extends Exception {
    private final int characterId;

    /**
     * Constructs a NotEnoughCoinsException that is not associated to any character
     */
    public NotEnoughCoinsException() {
        characterId = 0;
    }

    /**
     * Constructs a NotEnoughCoinsException that is associated to the specified character
     */
    public NotEnoughCoinsException(int characterId) {
        this.characterId = characterId;
    }

    /**
     * Return the message associated to the NotEnoughCoinsException, this will be sent to the client in an
     * error message. The message depends on the character that is associated to the exception
     *
     * @return the message associated to the NotEnoughCoinsException
     */
    @Override
    public String getMessage() {
        return "You don't have enough coins to play character " + characterId;
    }
}
