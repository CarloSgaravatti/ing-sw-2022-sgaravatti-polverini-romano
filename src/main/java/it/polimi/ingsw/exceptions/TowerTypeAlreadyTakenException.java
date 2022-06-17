package it.polimi.ingsw.exceptions;

public class TowerTypeAlreadyTakenException extends Exception {

    @Override
    public String getMessage() {
        return "The selected tower is already taken.";
    }
}
