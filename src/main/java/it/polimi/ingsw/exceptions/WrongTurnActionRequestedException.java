package it.polimi.ingsw.exceptions;

public class WrongTurnActionRequestedException extends Exception {

    @Override
    public String getMessage() {
        return "You can't do this action now.";
    }
}
