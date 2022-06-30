package it.polimi.ingsw.listeners;

import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

/**
 * AcknowledgementDispatcher is a PropertyChangeListener that respond to events that come from a controller after a
 * player has done a correct action in order to send to the client the acknowledgement for that action. The dispatcher
 * will also be used to inform the GameLobby when an action is correctly performed, do that the game lobby can save the
 * new game state on a file
 *
 * @see java.beans.PropertyChangeListener
 */
public class AcknowledgementDispatcher implements PropertyChangeListener {
    private static final String ACTION = "Action";
    private static final String SETUP = "Setup";
    private final List<RemoteView> views;
    private final GameLobby gameLobby;

    /**
     * Constructs an AcknowledgementDispatcher that is associated to the specified remote views and game lobby. The remote
     * views will be used to send messages to clients
     *
     * @param views the remote views of the clients
     * @param gameLobby the game lobby of the game
     */
    public AcknowledgementDispatcher(List<RemoteView> views, GameLobby gameLobby) {
        this.views = views;
        this.gameLobby = gameLobby;
    }

    /**
     * Forward an ack message to the remote view of the specified client that will ack the specified setup action
     *
     * @param clientName the name of the client
     * @param setupAction the action that needs to be acknowledged
     */
    public void confirmSetupChoice(String clientName, String setupAction) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("SetupName", setupAction);
        for (RemoteView view: views) {
            if (view.getPlayerNickname().equals(clientName)) {
                dispatchAck(view, payload, "SetupAck");
                return;
            }
        }
    }

    /**
     * Forward an ack message to the remote view of the specified client that will ack the specified action. The message
     * will also contain the new possible action that the client can do before the end of the turn.
     *
     * @param clientName the name of the client
     * @param actionName the action that needs to be acknowledged
     * @param newPossibleActions the new possible actions that the client can do (can be empty)
     */
    public void confirmActionPerformed(String clientName, String actionName, TurnPhase[] newPossibleActions) {
        System.out.println("Sending an acknowledgement for action " + actionName + " to " + clientName);
        System.out.println(clientName + " can now do these actions: " + Arrays.toString(newPossibleActions));
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("ActionName", actionName);
        payload.setAttribute("NewPossibleActions", newPossibleActions);
        for (RemoteView view: views) {
            if (view.getPlayerNickname().equals(clientName)) {
                dispatchAck(view, payload, "ActionAck");
                return;
            }
        }
    }

    /**
     * Forwards the ack message that have the specified payload and the specified message name to the specified remote
     * view.
     *
     * @param view the remote view that will receive the message
     * @param payload the payload of the message
     * @param messageName the name of the message
     */
    private void dispatchAck(RemoteView view, MessagePayload payload, String messageName) {
        view.sendMessage(payload, messageName, ServerMessageType.ACK_MESSAGE);
    }

    /**
     * Responds to an event that is fired from a controller in order to acknowledge an action
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ACTION -> confirmActionPerformed((String) evt.getSource(), (String) evt.getOldValue(), (TurnPhase[]) evt.getNewValue());
            case SETUP -> confirmSetupChoice((String) evt.getOldValue(), (String) evt.getNewValue());
        }
        gameLobby.setSaveGame();
    }
}
