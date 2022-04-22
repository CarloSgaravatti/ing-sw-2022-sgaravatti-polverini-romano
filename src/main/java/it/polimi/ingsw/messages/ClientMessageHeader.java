package it.polimi.ingsw.messages;

import java.io.Serializable;

public class ClientMessageHeader implements Serializable {
    private final String messageName;
    private final String nicknameSender;
    //TODO: other attributes

    public ClientMessageHeader(String messageName, String nicknameSender) {
        this.messageName = messageName;
        this.nicknameSender = nicknameSender;
    }

    public String getMessageName() {
        return messageName;
    }

    public String getNicknameSender() {
        return nicknameSender;
    }
}
