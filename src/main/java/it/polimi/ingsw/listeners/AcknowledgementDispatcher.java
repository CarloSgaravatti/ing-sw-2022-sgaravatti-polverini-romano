package it.polimi.ingsw.listeners;

import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import java.util.List;

public class AcknowledgementDispatcher implements EventListener, PropertyChangeListener {
    private static final String ACTION = "Action";
    private static final String SETUP = "Setup";
    private final List<RemoteView> views;

    public AcknowledgementDispatcher(List<RemoteView> views) {
        this.views = views;
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
    }
}
