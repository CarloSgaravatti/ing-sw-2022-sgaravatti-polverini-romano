package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.ClientMessageHeader;
import it.polimi.ingsw.messages.ClientMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.messages.MessagePayload;

public abstract class BaseMessageHandler implements MessageHandler{
    private MessageHandler nextHandler;
    private final ConnectionToServer connection;
    private final UserInterface userInterface;
    private final ModelView modelView;

    public BaseMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        this.connection = connection;
        this.userInterface = userInterface;
        this.modelView = modelView;
    }

    @Override
    public final void setNextHandler(MessageHandler handler) {
        this.nextHandler = handler;
    }

    public MessageHandler getNextHandler() {
        return nextHandler;
    }

    public ConnectionToServer getConnection() {
        return connection;
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    public ModelView getModelView() {
        return modelView;
    }

    public void sendResponse(ClientMessageType messageType, String messageName, MessagePayload payload) {
        ClientMessageHeader header = new ClientMessageHeader(messageName, userInterface.getNickname(), messageType);
        connection.asyncWriteToServer(new MessageFromClient(header, payload));
    }
}
