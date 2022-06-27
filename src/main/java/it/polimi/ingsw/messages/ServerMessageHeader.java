package it.polimi.ingsw.messages;

import java.io.Serializable;

/**
 * ServerMessageHeader represent the header of a MessageFromServer and contains all information that will help the
 * ConnectionToServer that handle the connection with the server in forwarding the message to the correct handler of the
 * message. The header has a message name (to distinguish messages), a message type (to distinguish groups of
 * messages) and can have the id of the game
 * @see MessageFromServer
 * @see ServerMessageType
 */
public class ServerMessageHeader implements Serializable {
    private final String messageName;
    private final ServerMessageType messageType;

    /**
     * Constructs a new ServerMessageHeader that has the specified message name and message type
     *
     * @param messageName the name of the message
     * @param messageType the type of the message
     */
    public ServerMessageHeader(String messageName, ServerMessageType messageType) {
        this.messageName = messageName;
        this.messageType = messageType;
    }

    /**
     * Returns the name of the message
     *
     * @return the name of the message
     */
    public String getMessageName() {
        return messageName;
    }

    /**
     * Returns the type of the message
     *
     * @return the type of the message
     */
    public ServerMessageType getMessageType() {
        return messageType;
    }
}
