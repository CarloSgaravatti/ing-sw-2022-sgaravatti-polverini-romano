package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;

//Handles GAME_SETUP and SetupAck messages
public class GameSetupMessageHandler extends BaseMessageHandler{
    public GameSetupMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    @Override
    public void handleMessage(MessageFromServer message) {

    }
}
