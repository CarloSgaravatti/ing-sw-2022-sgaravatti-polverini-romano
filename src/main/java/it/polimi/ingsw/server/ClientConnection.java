package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.ErrorMessageType;

import java.beans.PropertyChangeListener;

public interface ClientConnection {

    void closeConnection();

    void asyncSend(final Object message);

    void addListener(String propertyName, PropertyChangeListener listener);

    void setSetupDone(boolean setupDone);

    void sendError(ErrorMessageType error);
}
