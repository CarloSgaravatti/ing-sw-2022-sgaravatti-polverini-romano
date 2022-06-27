package it.polimi.ingsw.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MessagePayload contains all the message attributes that are contained in a message (from both client and server). Each
 * attribute is identified by a string (its name) and can be set in this way: <CODE>payload.setAttribute(name, attribute)</CODE>;
 * in this way the message attribute can be obtained by doing <CODE>payload.getAttribute(name)</CODE>. Attributes must have
 * different names
 */
public class MessagePayload implements Serializable {
    private final Map<String, MessageAttribute> messageAttributes; //each message attribute is defined by a name

    /**
     * Constructs a new empty MessagePayload that has no attributes in it
     */
    public MessagePayload() {
        messageAttributes = new HashMap<>();
    }

    /**
     * Insert the specified attribute with the speed name in the payload. Attribute must have a name that is not already
     * present in the payload, otherwise the previous value is replaced.
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(String name, Object value) {
        messageAttributes.put(name, new MessageAttribute(value));
    }

    /**
     * Returns the attribute that have the specified name
     *
     * @param name the name of the attribute
     * @return the attribute with the specified name
     */
    public MessageAttribute getAttribute(String name) {
        return messageAttributes.get(name);
    }
}
