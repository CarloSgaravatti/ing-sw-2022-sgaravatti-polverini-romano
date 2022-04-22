package it.polimi.ingsw.messages;

import java.io.Serializable;

public class ServerMessageHeader implements Serializable {
    private final String messageName;
    //TODO: other attributes

    public ServerMessageHeader(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageName() {
        return messageName;
    }
}
