package it.polimi.ingsw.exceptions;

/**
 * EmptyCloudException is thrown when a client tries to pick students from a cloud that doesn't have students (for
 * example because someone else has already picked them from it).
 */
public class EmptyCloudException extends Exception {

    /**
     * Return the message associated to the EmptyCloudException
     * @return the message of the exception
     */
    @Override
    public String getMessage() {
        return "The selected cloud does not have any students";
    }
}
