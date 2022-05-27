package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

public class TurnMessageHandler extends BaseMessageHandler {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private static final List<String> messageHandled = List.of("EndTurn", "ChangePhase", "EndGameWinner", "EndGameTied", "ActionAck");

    public TurnMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
        this.listeners.addPropertyChangeListener("NewTurn", userInterface);
        this.listeners.addPropertyChangeListener("Winner", userInterface);
        this.listeners.addPropertyChangeListener("Loser", userInterface);
        this.listeners.addPropertyChangeListener("TieLoser", userInterface);
        this.listeners.addPropertyChangeListener("Tie", userInterface);
    }

    public void setTurnHandler(PropertyChangeListener turnHandler) {
        this.listeners.addPropertyChangeListener("ClientTurn", turnHandler);
        this.listeners.addPropertyChangeListener("ActionAck", turnHandler);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if (!messageHandled.contains(header.getMessageName())) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch(header.getMessageName()) {
            case "EndTurn" -> onEndTurn(payload);
            case "ChangePhase" -> onChangePhase(payload);
            case "EndGameWinner" -> onWinner(payload);
            case "EndGameTied" -> onTie(payload);
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

    private void onWinner(MessagePayload payload) {
        String winner = (String) payload.getAttribute("Winner").getAsObject();
        if (winner.equals(getUserInterface().getNickname())) {
            listeners.firePropertyChange("Winner", null, null);
        } else {
            listeners.firePropertyChange("Loser", null, winner);
        }
    }

    private void onTie(MessagePayload payload) {
        String[] tiers = (String[]) payload.getAttribute("Tiers").getAsObject();
        if (Arrays.asList(tiers).contains(getUserInterface().getNickname())) {
            listeners.firePropertyChange("Tie", null, tiers);
        } else {
            listeners.firePropertyChange("TieLoser", null, tiers);
        }
    }

    private void onActionAck(MessagePayload payload) {
        listeners.firePropertyChange("ActionAck",
                payload.getAttribute("ActionName").getAsString(), payload.getAttribute("NewPossibleActions").getAsObject());
    }

    private void checkClientTurn(String turnStarter, TurnPhase[] possibleActions) {
        if (turnStarter.equals(getUserInterface().getNickname())) {
            getUserInterface().displayStringMessage("Now is your turn");
            getUserInterface().displayStringMessage(Arrays.toString(possibleActions)); //temporary
            listeners.firePropertyChange("ClientTurn", null, possibleActions);
        } else {
            listeners.firePropertyChange("NewTurn", null, turnStarter);
        }
    }
}
