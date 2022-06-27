package it.polimi.ingsw.messages;

import java.io.Serializable;

/**
 * MessageFromServer is the object that a client send to the server in the socket output stream. The message contains a
 * header (of type ServerMessageHeader) and a payload (of type MessagePayload).
 * @see ServerMessageHeader
 * @see MessagePayload
 */
public class MessageFromServer implements Serializable {
    private final ServerMessageHeader serverMessageHeader;
    private final MessagePayload messagePayload;

    /**
     * Constructs a new MessageFromServer that has the specified header and the specified payload
     *
     * @param serverMessageHeader the header of the message
     * @param messagePayload the payload of the message
     */
    public MessageFromServer(ServerMessageHeader serverMessageHeader, MessagePayload messagePayload) {
        this.serverMessageHeader = serverMessageHeader;
        this.messagePayload = messagePayload;
    }

    /**
     * Returns the header of the message
     *
     * @return the header of the message
     */
    public ServerMessageHeader getServerMessageHeader() {
        return serverMessageHeader;
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
