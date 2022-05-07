package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;

public class GameUpdateMessageHandler extends BaseMessageHandler {
    public GameUpdateMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    @Override
    public void handleMessage(MessageFromServer message) {

    }
}
