package it.polimi.ingsw.listeners;

import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.RemoteView;

public class TurnListener implements ModelListener{
    private final RemoteView view;

    public TurnListener(RemoteView view) {
        this.view = view;
    }

    public void endTurnEventPerformed(String turnEnder, String turnStarter) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("TurnEnder", turnEnder);
        payload.setAttribute("TurnStarter", turnStarter);
        view.sendMessage(payload, "EndTurn", ServerMessageType.GAME_UPDATE);
    }

    public void endPhaseEventPerformed(RoundPhase newPhase, String starter) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("NewPhase", newPhase);
        payload.setAttribute("Starter", starter);
        view.sendMessage(payload, "ChangePhase", ServerMessageType.GAME_UPDATE);
    }
}
