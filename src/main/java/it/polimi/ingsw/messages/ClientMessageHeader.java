package it.polimi.ingsw.messages;

import java.io.Serializable;

public class ClientMessageHeader implements Serializable {
    private final String messageName;
    private final ClientMessageType messageType;
    private final String nicknameSender;

    public ClientMessageHeader(String messageName, String nicknameSender, ClientMessageType messageType) {
        this.messageName = messageName;
        this.nicknameSender = nicknameSender;
        this.messageType = messageType;
    }

    public String getMessageName() {
        return messageName;
    }

    public String getNicknameSender() {
        return nicknameSender;
    }

    public ClientMessageType getMessageType() {
        return messageType;
    }
}
