package it.polimi.ingsw.exceptions;

/**
 * TowerTypeAlreadyTakenException is thrown when a player tries to choose a tower that is already taken by another player
 */
public class TowerTypeAlreadyTakenException extends Exception {

    /**
     * Return the message associated to the TowerTypeAlreadyTakenException, this will be sent to the client in an
     * error message
     *
     * @return the message associated to the TowerTypeAlreadyTakenException
     */
    @Override
    public String getMessage() {
        return "The selected tower is already taken.";
    }
}
