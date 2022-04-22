package it.polimi.ingsw.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessagePayload implements Serializable {
    private final Map<String, MessageAttribute> messageAttributes; //each message attribute is defined by a name

    public MessagePayload() {
        messageAttributes = new HashMap<>();
    }

    public void setAttribute(String name, Object value) {
        messageAttributes.put(name, new MessageAttribute(value));
    }

    public MessageAttribute getAttribute(String name) {
        return messageAttributes.get(name);
    }
}
