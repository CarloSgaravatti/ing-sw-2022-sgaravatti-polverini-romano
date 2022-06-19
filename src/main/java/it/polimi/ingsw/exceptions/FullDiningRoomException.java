package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.model.enumerations.RealmType;

/**
 * FullDiningRoomException is thrown when a client tries to insert a student to the dining room when the corresponding
 * student dining room of the student is full.
 */
public class FullDiningRoomException extends Exception {
    private final RealmType realmType;

    /**
     * Constructs a FullDiningRoomException which has been thrown when trying to insert a student of the specified
     * student type in the dining room
     * @param realmType the student that was trying to be inserted
     */
    public FullDiningRoomException(RealmType realmType) {
        this.realmType = realmType;
    }

    /**
     * Return the message associated to the exception, which is used to notify which student type have caused the error
     * @return the message of the exception
     */
    @Override
    public String getMessage() {
        return "The selected school have the dining room full for students of type " + realmType
                + " does not have any students";
    }
}
