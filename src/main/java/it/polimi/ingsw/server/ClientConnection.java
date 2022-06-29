package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.ErrorMessageType;

import java.beans.PropertyChangeListener;

/**
 * ClientConnection is an interface that handles the connection between client and server, by sending messages to the client
 * and receiving messages from the client
 */
public interface ClientConnection {

    /**
     * Close the connection with the client
     */
    void closeConnection();

    /**
     * Asynchronously sends a message to the client
     *
     * @param message the message that will be sent
     */
    void asyncSend(final Object message);

    /**
     * Adds a listener to this, that will listen the specified property name
     *
     * @param propertyName the name of the property
     * @param listener the listener that will listen the property
     */
    void addListener(String propertyName, PropertyChangeListener listener);

    /**
     * Set the setupDone property, that will inform the ClientConnection that the client have done all the setup stuff
     *
     * @param setupDone true if the client have completed the setup phase, otherwise false
     */
    void setSetupDone(boolean setupDone);

    /**
     * Send the specified error, with the specified description to the client
     *
     * @param error the error type that will be sent to the client
     * @param description the description of the error
     */
    void sendError(ErrorMessageType error, String description);
}
