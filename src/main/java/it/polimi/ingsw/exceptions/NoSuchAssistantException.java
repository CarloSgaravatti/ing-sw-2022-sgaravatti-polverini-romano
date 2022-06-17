package it.polimi.ingsw.exceptions;

public class NoSuchAssistantException extends Exception {
    private final int assistantId;

    public NoSuchAssistantException(int assistantId) {
        this.assistantId = assistantId;
    }

    @Override
    public String getMessage() {
        return "There isn't any assistant with id = " + assistantId + " in your deck";
    }
}
