package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.model.enumerations.RealmType;

public class FullDiningRoomException extends Exception {
    private final RealmType realmType;

    public FullDiningRoomException(RealmType realmType) {
        this.realmType = realmType;
    }

    @Override
    public String getMessage() {
        return "The selected school have the dining room full for students of type " + realmType
                + " does not have any students";
    }
}
