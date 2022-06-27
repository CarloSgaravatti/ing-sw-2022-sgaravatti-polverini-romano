package it.polimi.ingsw.messages;

import java.io.Serializable;

/**
 * ClientMessageHeader represent the header of a MessageFromClient and contains all information that will help the
 * ClientConnection that handle the connection with the client in forwarding the message to the correct handler of the
 * message. The header has a message name (to distinguish messages), a message type (to distinguish groups of
 * messages) and the nickname of the sender
 * @see MessageFromClient
 * @see ClientMessageType
 */
public class ClientMessageHeader implements Serializable {
    private final String messageName;
    private final ClientMessageType messageType;
    private final String nicknameSender;

    /**
     * Constructs a new ClientMessageHeader that has the specified message name, message type and nickname of the sender
     *
     * @param messageName the name of the message
     * @param nicknameSender the sender of the message
     * @param messageType the type of the message
     */
    public ClientMessageHeader(String messageName, String nicknameSender, ClientMessageType messageType) {
        this.messageName = messageName;
        this.nicknameSender = nicknameSender;
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
     * Returns the nickname of the sender of the message
     *
     * @return the nickname of the sender of the message
     */
    public String getNicknameSender() {
        return nicknameSender;
    }

    /**
     * Returns the type of message
     *
     * @return the type of message
     */
    public ClientMessageType getMessageType() {
        return messageType;
    }
}
