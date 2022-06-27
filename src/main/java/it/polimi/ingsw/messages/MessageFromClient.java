package it.polimi.ingsw.messages;

import java.io.Serializable;

/**
 * MessageFromClient is the object that a client send to the server in the socket output stream. The message contains a
 * header (of type ClientMessageHeader) and a payload (of type MessagePayload).
 * @see ClientMessageHeader
 * @see MessagePayload
 */
public class MessageFromClient implements Serializable {
    private final ClientMessageHeader clientMessageHeader;
    private final MessagePayload messagePayload;

    /**
     * Constructs a new MessageFromClient that has the specified header and the specified payload
     *
     * @param clientMessageHeader the header of the message
     * @param messagePayload the payload of the message
     */
    public MessageFromClient(ClientMessageHeader clientMessageHeader, MessagePayload messagePayload) {
        this.clientMessageHeader = clientMessageHeader;
        this.messagePayload = messagePayload;
    }

    /**
     * Returns the header of the message
     *
     * @return the header of the message
     */
    public ClientMessageHeader getClientMessageHeader() {
        return clientMessageHeader;
    }

    /**
     * Returns the payload of the message
     *
     * @return the payload of the message
     */
    public MessagePayload getMessagePayload() {
        return messagePayload;
    }
}
