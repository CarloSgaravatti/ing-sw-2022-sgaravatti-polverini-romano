package it.polimi.ingsw.exceptions;

public class DuplicateNicknameException extends Exception {

    @Override
    public String getMessage() {
        return "This nickname is already taken, chose another one please.";
    }
}
