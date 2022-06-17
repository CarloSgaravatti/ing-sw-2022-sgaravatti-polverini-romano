package it.polimi.ingsw.exceptions;

public class EmptyCloudException extends Exception {

    @Override
    public String getMessage() {
        return "The selected cloud does not have any students";
    }
}
