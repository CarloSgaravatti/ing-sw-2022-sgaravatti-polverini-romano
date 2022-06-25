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

public class AcknowledgementDispatcher implements EventListener, PropertyChangeListener {
    private static final String ACTION = "Action";
    private static final String SETUP = "Setup";
    private final List<RemoteView> views;
    private final GameLobby gameLobby;

    public AcknowledgementDispatcher(List<RemoteView> views, GameLobby gameLobby) {
        this.views = views;
        this.gameLobby = gameLobby;
    }

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

    private void dispatchAck(RemoteView view, MessagePayload payload, String messageName) {
        view.sendMessage(payload, messageName, ServerMessageType.ACK_MESSAGE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ACTION -> confirmActionPerformed((String) evt.getSource(), (String) evt.getOldValue(), (TurnPhase[]) evt.getNewValue());
            case SETUP -> confirmSetupChoice((String) evt.getOldValue(), (String) evt.getNewValue());
        }
        gameLobby.setSaveGame();
    }
}
