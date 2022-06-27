package it.polimi.ingsw.messages;

/**
 * Enum class ClientMessageType represent the type of message that can be sent by a client to the server.
 * @see ServerMessageHeader
 */
public enum ServerMessageType {
    SERVER_MESSAGE, GAME_SETUP, GAME_UPDATE, ACK_MESSAGE, PING_MESSAGE
}