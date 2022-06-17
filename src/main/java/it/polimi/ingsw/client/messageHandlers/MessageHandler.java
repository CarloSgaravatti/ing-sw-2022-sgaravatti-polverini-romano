package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.messages.MessageFromServer;

/**
 * A message handler a client class that handles messages from the server. There are many message handlers that handle different
 * type of message: from the message header, the correct message handler that will handle the message is selected. Each message handler
 * have to modify (if needed) the ModelView and have to notify the UserInterface of the message content (if there is something that the
 * UserInterface must know).
 *
 * Message handlers implements the Chain of Responsibility pattern: a message handler can have a next handler that will handle
 * the received message if the current handler don't know how to deal with the message. In this way, the handlers chain can mutate
 * during the game: the first message handler which the message is passed is the message handler that will have the higher
 * probability of knowing how to deal with the message (for example, at the beginning there is a message handler that deals with
 * setup messages, but during the game the first message handler will be the one that reads the game updates)
 */
public interface MessageHandler {

    /**
     * Set that next handler that will handle the message if the current handler cannot do that
     * @param handler the next handler
     */
    void setNextHandler(MessageHandler handler);

    /**
     * If possible, handle the message that arrived from the server; otherwise it passes it to the next handler in the chain
     * @param message the message from the server
     */
    void handleMessage(MessageFromServer message);
}
