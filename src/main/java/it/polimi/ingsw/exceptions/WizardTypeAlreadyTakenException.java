package it.polimi.ingsw.exceptions;

/**
 * WizardTypeAlreadyTakenException is thrown when a player tries to choose a wizard that is already taken by another player
 */
public class WizardTypeAlreadyTakenException extends Exception{

    /**
     * Return the message associated to the WizardTypeAlreadyTakenException, this will be sent to the client in an
     * error message
     *
     * @return the message associated to the WizardTypeAlreadyTakenException
     */
    @Override
    public String getMessage() {
        return "The selected wizard is already taken.";
    }
}
