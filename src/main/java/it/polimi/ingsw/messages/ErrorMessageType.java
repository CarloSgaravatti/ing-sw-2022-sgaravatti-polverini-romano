package it.polimi.ingsw.messages;

/**
 * Enum class ErrorMessageType represents all the type of errors that will be sent to clients if they make some mistakes
 * in sending messages to the server.
 */
public enum ErrorMessageType {
    DUPLICATE_NICKNAME,
    UNRECOGNIZED_MESSAGE,
    INVALID_REQUEST_GAME_ALREADY_STARTED,
    INVALID_REQUEST_GAME_NOT_FOUND,
    SETUP_ALREADY_DONE, //if a client send a setup message when he has already done setups
    CLIENT_WITHOUT_GAME, //if a client sends an action/setup message when he doesn't have a game
    ILLEGAL_TURN,
    ILLEGAL_TURN_ACTION,
    TURN_NOT_FINISHED, //if a client send EndTurn, but he has to do something more before
    WIZARD_ALREADY_TAKEN,
    TOWER_ALREADY_TAKEN,
    ILLEGAL_ARGUMENT,
    CHARACTER_ALREADY_PLAYED
}
