package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.ErrorMessageType;

public interface ClientConnection {

    void closeConnection();

    void asyncSend(final Object message);

    void addListener(RemoteView listener);

    void setSetupDone(boolean setupDone);

    void sendError(ErrorMessageType error);
}
