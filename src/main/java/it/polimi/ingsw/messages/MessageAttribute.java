package it.polimi.ingsw.messages;

import java.io.Serializable;

public class MessageAttribute implements Serializable {
    private final Object attribute;

    public MessageAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public Object getAsObject() {
        return attribute;
    }

    public int getAsInt() throws ClassCastException {
        return (Integer) attribute;
    }

    public String getAsString() throws ClassCastException {
        return (String) attribute;
    }

    public boolean getAsBoolean() throws ClassCastException {
        return (Boolean) attribute;
    }
}
