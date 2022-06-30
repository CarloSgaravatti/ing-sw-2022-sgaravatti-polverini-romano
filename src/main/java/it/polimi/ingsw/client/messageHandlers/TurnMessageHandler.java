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

/**
 * ExpertGameMessageHandler handles all messages that have GAME_UPDATE as message type and that regard turns messages
 * that notify the end of a turn or of a phase. The handler will handle also end games messages and acknowledgments.
 *
 * @see it.polimi.ingsw.client.messageHandlers.MessageHandler
 * @see it.polimi.ingsw.client.messageHandlers.BaseMessageHandler
 */
public class TurnMessageHandler extends BaseMessageHandler {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private static final List<String> messageHandled = List.of("EndTurn", "ChangePhase", "EndGameWinner", "EndGameTied", "ActionAck");

    /**
     * Constructs a new TurnMessageHandler that will be associated to the specified connection to the server, user
     * interface and model view
     *
     * @param connection the connection to the server that will pass the messages
     * @param userInterface the user interface of the client
     * @param modelView the model view of the client.
     */
    public TurnMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
        this.listeners.addPropertyChangeListener("NewTurn", userInterface);
        this.listeners.addPropertyChangeListener("Winner", userInterface);
        this.listeners.addPropertyChangeListener("Loser", userInterface);
        this.listeners.addPropertyChangeListener("TieLoser", userInterface);
        this.listeners.addPropertyChangeListener("Tie", userInterface);
    }

    /**
     * Sets the value of the turn handler that will be informed when an acknowledgment will arrive from the server or
     * when it is the client turn.
     *
     * @param turnHandler the turn handler that will be associated to the message handler
     */
    public void setTurnHandler(PropertyChangeListener turnHandler) {
        this.listeners.addPropertyChangeListener("ClientTurn", turnHandler);
        this.listeners.addPropertyChangeListener("ActionAck", turnHandler);
    }

    /**
     * Handles a message that have been arrived from the server
     *
     * @param message the message from the server
     * @see MessageHandler#handleMessage(MessageFromServer)
     */
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

    /**
     * Notify the user interface that the current active player, contained in the payload of the message have changed,
     * and notifies the turn handler if the new active player is the client
     *
     * @param payload the payload of the message
     */
    private void onEndTurn(MessagePayload payload) {
        String newActivePlayer = payload.getAttribute("TurnStarter").getAsString();
        String oldActivePlayer = payload.getAttribute("TurnEnder").getAsString(); //TODO: decide if this information is useful
        getModelView().setCurrentActivePlayer(newActivePlayer);
        checkClientTurn(newActivePlayer, (TurnPhase[]) payload.getAttribute("PossibleActions").getAsObject());
    }

    /**
     * Notify the user interface that the current active player and the current phase, contained in the payload of the
     * message have changed, and notifies the turn handler if the new active player is the client
     *
     * @param payload the payload of the message
     */
    private void onChangePhase(MessagePayload payload) {
        String starter = payload.getAttribute("Starter").getAsString();
        RoundPhase newPhase = (RoundPhase) payload.getAttribute("NewPhase").getAsObject();
        getModelView().setCurrentActivePlayer(starter);
        getModelView().setCurrentPhase(newPhase);
        checkClientTurn(starter, (TurnPhase[]) payload.getAttribute("PossibleActions").getAsObject());
    }

    /**
     * Notify the user interface after the game is finished to inform the user of the winner of the game
     *
     * @param payload the payload of the message
     */
    private void onWinner(MessagePayload payload) {
        String winner = (String) payload.getAttribute("Winner").getAsObject();
        if (winner.equals(getUserInterface().getNickname())) {
            listeners.firePropertyChange("Winner", null, null);
        } else {
            listeners.firePropertyChange("Loser", null, winner);
        }
    }

    /**
     * Notify the user interface after the game is finished with a tie to inform the user of the tiers of the game
     *
     * @param payload the payload of the message
     */
    private void onTie(MessagePayload payload) {
        String[] tiers = (String[]) payload.getAttribute("Tiers").getAsObject();
        if (Arrays.asList(tiers).contains(getUserInterface().getNickname())) {
            listeners.firePropertyChange("Tie", null, tiers);
        } else {
            listeners.firePropertyChange("TieLoser", null, tiers);
        }
    }

    /**
     * Informs the turn handler about the acknowledgement that have arrived from the server
     *
     * @param payload the payload of the message
     */
    private void onActionAck(MessagePayload payload) {
        listeners.firePropertyChange("ActionAck",
                payload.getAttribute("ActionName").getAsString(), payload.getAttribute("NewPossibleActions").getAsObject());
    }

    /**
     * Check if it is the client turn, if so it notifies the turn handler
     *
     * @param turnStarter the nickname of the starter of the turn
     * @param possibleActions the actions that the starter can do
     */
    private void checkClientTurn(String turnStarter, TurnPhase[] possibleActions) {
        if (turnStarter.equals(getUserInterface().getNickname())) {
            getUserInterface().displayStringMessage("Now is your turn");
            listeners.firePropertyChange("ClientTurn", null, possibleActions);
        } else {
            listeners.firePropertyChange("NewTurn", null, turnStarter);
        }
    }
}
