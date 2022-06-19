package it.polimi.ingsw.exceptions;

/**
 * NoSuchAssistantException is thrown when a client tries to play an assistant that was not found on his deck
 */
public class NoSuchAssistantException extends Exception {
    private final int assistantId;

    /**
     * Constructs a NoSuchAssistantException that was thrown when the specified assistant was trying to be played
     * @param assistantId the id of the assistant that was unsuccessfully played
     */
    public NoSuchAssistantException(int assistantId) {
        this.assistantId = assistantId;
    }

    /**
     * Returns the message that is associated with the exception
     * @return the message of the exception
     */
    @Override
    public String getMessage() {
        return "There isn't any assistant with id = " + assistantId + " in your deck";
    }
}
