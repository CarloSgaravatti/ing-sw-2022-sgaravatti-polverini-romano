package it.polimi.ingsw.server;

public interface ClientConnection {

    void closeConnection();

    void asyncSend(final Object message);

    void addListener(RemoteView listener);

    void setSetupDone(boolean setupDone);
}
