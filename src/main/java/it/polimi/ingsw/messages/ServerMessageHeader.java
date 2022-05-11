package it.polimi.ingsw.messages;

import java.io.Serializable;

public class ServerMessageHeader implements Serializable {
    private final String messageName;
    private final ServerMessageType messageType;
    private int gameId;

    public ServerMessageHeader(String messageName, ServerMessageType messageType) {
        this.messageName = messageName;
        this.messageType = messageType;
    }

    public ServerMessageHeader(String messageName, ServerMessageType messageType, int gameId) {
        this(messageName, messageType);
        this.gameId = gameId;
    }

    public String getMessageName() {
        return messageName;
    }

    public ServerMessageType getMessageType() {
        return messageType;
    }

    public int getGameId() {
        return gameId;
    }
}
