package it.polimi.ingsw.listeners;

import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TurnListener implements PropertyChangeListener {
    private final RemoteView view;

    public TurnListener(RemoteView view) {
        this.view = view;
    }

    public void endTurnEventPerformed(String turnEnder, String turnStarter, TurnPhase[] possibleActions) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("TurnEnder", turnEnder);
        payload.setAttribute("TurnStarter", turnStarter);
        payload.setAttribute("PossibleActions", possibleActions);
        view.sendMessage(payload, "EndTurn", ServerMessageType.GAME_UPDATE);
    }

    public void endPhaseEventPerformed(RoundPhase newPhase, String starter, TurnPhase[] possibleActions) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("NewPhase", newPhase);
        payload.setAttribute("Starter", starter);
        payload.setAttribute("PossibleActions", possibleActions);
        view.sendMessage(payload, "ChangePhase", ServerMessageType.GAME_UPDATE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "EndTurn" -> endTurnEventPerformed((String) evt.getOldValue(), (String) evt.getSource(), (TurnPhase[]) evt.getNewValue());
            case "EndPhase" -> endPhaseEventPerformed((RoundPhase) evt.getOldValue(), (String) evt.getSource(), (TurnPhase[]) evt.getNewValue());
        }
    }
}
