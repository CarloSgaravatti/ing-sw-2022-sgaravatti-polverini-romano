package it.polimi.ingsw.exceptions;

/**
 * DuplicateNicknameException is thrown when a client chose a nickname that is already chosen by another client that is
 * already connected (and is still connected) to the server.
 */
public class DuplicateNicknameException extends Exception {

    /**
     * Return the message associated to the DuplicatedNicknameException, this will be sent to the client in an
     * error message
     * @return the message of associated to the exception
     */
    @Override
    public String getMessage() {
        return "This nickname is already taken, chose another one please.";
    }
}
