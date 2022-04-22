package it.polimi.ingsw.messages;

import java.io.Serializable;

public class MessageFromClient implements Serializable {
    private final ClientMessageHeader clientMessageHeader;
    private final MessagePayload messagePayload;

    public MessageFromClient(ClientMessageHeader clientMessageHeader, MessagePayload messagePayload) {
        this.clientMessageHeader = clientMessageHeader;
        this.messagePayload = messagePayload;
    }

    public ClientMessageHeader getClientMessageHeader() {
        return clientMessageHeader;
    }

    public MessagePayload getMessagePayload() {
        return messagePayload;
    }
}
