package it.polimi.ingsw.exceptions;

public class WizardTypeAlreadyTakenException extends Exception{

    @Override
    public String getMessage() {
        return "The selected wizard is already taken.";
    }
}
