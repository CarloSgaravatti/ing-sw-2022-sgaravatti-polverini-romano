package it.polimi.ingsw.messages;

import java.io.Serializable;

public class MessageFromServer implements Serializable {
    private final ServerMessageHeader serverMessageHeader;
    private final MessagePayload messagePayload;

    public MessageFromServer(ServerMessageHeader serverMessageHeader, MessagePayload messagePayload) {
        this.serverMessageHeader = serverMessageHeader;
        this.messagePayload = messagePayload;
    }

    public ServerMessageHeader getServerMessageHeader() {
        return serverMessageHeader;
    }

    public MessagePayload getMessagePayload() {
        return messagePayload;
    }
}
