package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;

/**
 * BaseMessageHandler is an abstract class that implements all functions that are needed for each MessageHandler (to reuse
 * code).
 * @see it.polimi.ingsw.client.messageHandlers.MessageHandler
 */
public abstract class BaseMessageHandler implements MessageHandler{
    private MessageHandler nextHandler;
    private final ConnectionToServer connection;
    private final UserInterface userInterface;
    private ModelView modelView;

    /**
     * Construct a base message handler that have the specified ConnectionToServer (that will pass messages to the
     * message handlers, and it will be used to send responses), the specified UserInterface (to which all updates are
     * sent) and the specified ModelView (that will be modified with the updates that come from the server).
     * @param connection the connection to the server
     * @param userInterface the user interface (can be CLI or GUI)
     * @param modelView the model vie used in the game
     */
    public BaseMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        this.connection = connection;
        this.userInterface = userInterface;
        this.modelView = modelView;
    }

    /**
     * Sets the next handler in the chain of responsibility
     *
     * @param handler the next handler of the chain
     * @see MessageHandler#setNextHandler(MessageHandler)
     */
    @Override
    public final void setNextHandler(MessageHandler handler) {
        this.nextHandler = handler;
    }

    /**
     * Returns the next handler in the chain
     *
     * @return the next handler in the chain
     */
    public MessageHandler getNextHandler() {
        return nextHandler;
    }

    /**
     * Returns the connection to the server associated to the message handler
     *
     * @return the connection to the server associated to the message handler
     */
    public ConnectionToServer getConnection() {
        return connection;
    }

    /**
     * Returns the user interface associated to the message handler
     *
     * @return the user interface associated to the message handler
     */
    public UserInterface getUserInterface() {
        return userInterface;
    }

    /**
     * Returns the model view associated to the message handler
     *
     * @return the model view associated to the message handler
     */
    public ModelView getModelView() {
        return modelView;
    }

    /**
     * Sets the value of the model view associated to the message handler
     */
    public void setModelView(ModelView modelView) {
        this.modelView = modelView;
    }
}
