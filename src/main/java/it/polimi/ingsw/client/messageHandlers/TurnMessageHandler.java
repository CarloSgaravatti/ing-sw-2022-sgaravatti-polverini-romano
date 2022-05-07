package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.TurnHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;

//Handles EndTurn, ChangePhase, EndGame and ActionAck messages
public class TurnMessageHandler extends BaseMessageHandler {
    private TurnHandler turnHandler;

    public TurnMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    public void setTurnHandler(TurnHandler turnHandler) {
        this.turnHandler = turnHandler;
    }

    @Override
    public void handleMessage(MessageFromServer message) {

    }
}
