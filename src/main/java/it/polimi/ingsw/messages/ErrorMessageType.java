package it.polimi.ingsw.messages;

public enum ErrorMessageType {
    DUPLICATE_NICKNAME,
    UNRECOGNIZE_MESSAGE,
    INVALID_REQUEST_GAME_ALREADY_STARTED,
    INVALID_REQUEST_GAME_NOT_FOUND,
    SETUP_ALREADY_DONE, //if a client send a setup message when he has already done setups
    CLIENT_WITHOUT_GAME, //if a client sends an action/setup message when he doesn't have a game
    ILLEGAL_TURN,
    ILLEGAL_TURN_ACTION,
    WIZARD_ALREADY_TAKEN,
    TOWER_ALREADY_TAKEN //...
}
