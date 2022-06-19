package it.polimi.ingsw.exceptions;

/**
 * IllegalCharacterActionRequestException is thrown when a client tries to play a character, but it has made some
 * mistakes (for example the client request contains a character input that is not correct for the selected character
 * or because the client has already played a character in the turn)
 */
public class IllegalCharacterActionRequestedException extends Exception{

    /**
     * Construct a IllegalCharacterActionRequestedException which was thrown when playing a character with the
     * specified id
     * @param characterId the character that have thrown the exception
     */
    public IllegalCharacterActionRequestedException(int characterId) {
        super("There was an error in the character " + characterId + " action request");
    }

    /**
     * Construct a IllegalCharacterActionRequestedException associated to an exception that was thrown when
     * trying to play a character. The new exception will have the same message of the previous exception
     * @param e the exception that was previously thrown
     */
    public IllegalCharacterActionRequestedException(Exception e) {
        super(e.getMessage());
    }

    /**
     * Construct a IllegalCharacterActionRequestedException which will have the specified string as message
     * @param customDescription the message of the exception
     */
    public IllegalCharacterActionRequestedException(String customDescription) {
        super(customDescription);
    }
}
