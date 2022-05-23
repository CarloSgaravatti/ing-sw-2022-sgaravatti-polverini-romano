package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TurnMessageHandler extends BaseMessageHandler {
    private final PropertyChangeSupport turnHandler = new PropertyChangeSupport(this);
    private static final List<String> messageHandled = List.of("EndTurn", "ChangePhase", "EndGame", "ActionAck");

    public TurnMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    public void setTurnHandler(PropertyChangeListener turnHandler) {
        this.turnHandler.addPropertyChangeListener(turnHandler);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if (header.getMessageType() != ServerMessageType.GAME_UPDATE && !messageHandled.contains(header.getMessageName())) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch(header.getMessageName()) {
            case "EndTurn" -> onEndTurn(payload);
            case "ChangePhase" -> onChangePhase(payload);
            case "EndGame" -> onEndGame(payload);
            case "ActionAck" -> onActionAck(payload);
        }
    }

    private void onEndTurn(MessagePayload payload) {
        String newActivePlayer = payload.getAttribute("TurnStarter").getAsString();
        String oldActivePlayer = payload.getAttribute("TurnEnder").getAsString(); //TODO: decide if this information is useful
        getModelView().setCurrentActivePlayer(newActivePlayer);
        checkClientTurn(newActivePlayer, (TurnPhase[]) payload.getAttribute("PossibleActions").getAsObject());
    }

    private void onChangePhase(MessagePayload payload) {
        String starter = payload.getAttribute("Starter").getAsString();
        RoundPhase newPhase = (RoundPhase) payload.getAttribute("NewPhase").getAsObject();
        getModelView().setCurrentActivePlayer(starter);
        getModelView().setCurrentPhase(newPhase);
        checkClientTurn(starter, (TurnPhase[]) payload.getAttribute("PossibleActions").getAsObject());
    }

    private void onEndGame(MessagePayload payload) {
        boolean isWin = payload.getAttribute("IsWinOrTie").getAsBoolean();
        String[] winnersOrTiers = (String[]) payload.getAttribute("WinnersOrTiers").getAsObject();

        //TODO
    }

    private void onActionAck(MessagePayload payload) {
        System.out.println("Ack");
        turnHandler.firePropertyChange("ActionAck",
                payload.getAttribute("ActionName").getAsString(), payload.getAttribute("NewPossibleActions").getAsObject());
    }

    private void checkClientTurn(String turnStarter, TurnPhase[] possibleActions) {
        if (turnStarter.equals(getUserInterface().getNickname())) {
            getUserInterface().displayStringMessage("Now is your turn");
            getUserInterface().displayStringMessage(Arrays.toString(possibleActions)); //temporary
            turnHandler.firePropertyChange("ClientTurn", null, possibleActions);
        } else {
            getUserInterface().displayStringMessage("Now is " + turnStarter + "'s turn");
        }
    }
}
