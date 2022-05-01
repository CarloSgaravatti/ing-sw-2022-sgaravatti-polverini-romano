package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.util.EventListener;
import java.util.List;

public class AcknowledgementDispatcher implements EventListener {
    List<RemoteView> views;

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

    public void confirmActionPerformed(String clientName, String actionName) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("ActionName", actionName);
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
}
