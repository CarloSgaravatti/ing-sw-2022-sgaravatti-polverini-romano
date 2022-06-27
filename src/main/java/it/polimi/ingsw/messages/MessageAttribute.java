package it.polimi.ingsw.messages;

import java.io.Serializable;

/**
 * MessageAttribute is an attribute of the MessagePayload, that is used for both MessageFromServer and MessageFromClient.
 * The attribute can be an object of every type.
 */
public class MessageAttribute implements Serializable {
    private final Object attribute;

    /**
     * Constructs a new attribute that is bounded to the specified object
     *
     * @param attribute the object bounded to the attribute
     */
    public MessageAttribute(Object attribute) {
        this.attribute = attribute;
    }

    /**
     * Returns the attribute as an object
     *
     * @return the attribute as an object
     */
    public Object getAsObject() {
        return attribute;
    }

    /**
     * Returns the attribute as an integer
     *
     * @return the attribute as an integer
     * @throws ClassCastException if the attribute isn't an integer
     */
    public int getAsInt() throws ClassCastException {
        return (Integer) attribute;
    }

    /**
     * Returns the attribute as a string
     *
     * @return the attribute as a string
     * @throws ClassCastException if the attribute isn't a string
     */
    public String getAsString() throws ClassCastException {
        return (String) attribute;
    }

    /**
     * Returns the attribute as a boolean
     *
     * @return the attribute as a boolean
     * @throws ClassCastException if the attribute isn't a boolean
     */
    public boolean getAsBoolean() throws ClassCastException {
        return (Boolean) attribute;
    }
}
