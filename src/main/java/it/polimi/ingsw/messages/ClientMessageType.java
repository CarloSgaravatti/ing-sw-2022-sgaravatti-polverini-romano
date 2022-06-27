package it.polimi.ingsw.messages;

/**
 * Enum class ClientMessageType represent the type of message that can be sent by a client to the server.
 * @see ClientMessageHeader
 */
public enum ClientMessageType {
    PLAYER_SETUP, ACTION, GAME_SETUP, PING_ACK
}
