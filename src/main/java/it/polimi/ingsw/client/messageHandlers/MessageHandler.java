package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.messages.MessageFromServer;

public interface MessageHandler {

    void setNextHandler(MessageHandler handler);

    void handleMessage(MessageFromServer message);
}
