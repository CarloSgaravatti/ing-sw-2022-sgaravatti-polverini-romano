package it.polimi.ingsw.exceptions;

/**
 * WrongTurnActionRequestedException is thrown when a player tries to do an action that is not permitted in the current
 * turn state.
 */
public class WrongTurnActionRequestedException extends Exception {

    /**
     * Return the message associated to the WrongTurnActionRequestedException, this will be sent to the client in an
     * error message
     *
     * @return the message associated to the WrongTurnActionRequestedException
     */
    @Override
    public String getMessage() {
        return "You can't do this action now.";
    }
}
