package it.polimi.ingsw.listeners;

import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 * TurnListener is a PropertyChangeLister that listen the game controller to catch events that regard the end of a turn
 * the end of a round phase. Also, th listener listen to events that are fired always at the beginning of round phase, these
 * are the cloud refill and the assistants update at the beginning of a planning phase
 */
public class TurnListener implements PropertyChangeListener {
    private final RemoteView view;

    /**
     * Construct a new TurnListener that will forward messages to the specified remote view
     *
     * @param view the remote view
     */
    public TurnListener(RemoteView view) {
        this.view = view;
    }

    /**
     * Forwards an EndTurn message to the remote view in order to inform the client that a turn is ended, and it is the turn
     * of the specified turn starter
     *
     * @param turnEnder the one who has ended the turn
     * @param turnStarter the one who will start the turn
     * @param possibleActions the actions that the one that will start the turn can do
     */
    public void endTurnEventPerformed(String turnEnder, String turnStarter, TurnPhase[] possibleActions) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("TurnEnder", turnEnder);
        payload.setAttribute("TurnStarter", turnStarter);
        payload.setAttribute("PossibleActions", possibleActions);
        view.sendMessage(payload, "EndTurn", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards a ChangePhase message that will inform clients that a new round phase is started and that the specified
     * turn started will start the turn
     *
     * @param newPhase the new round phase
     * @param starter the starter of the phase
     * @param possibleActions the actions that the starter can do during the turn
     */
    public void endPhaseEventPerformed(RoundPhase newPhase, String starter, TurnPhase[] possibleActions) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("NewPhase", newPhase);
        payload.setAttribute("Starter", starter);
        payload.setAttribute("PossibleActions", possibleActions);
        view.sendMessage(payload, "ChangePhase", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Forwards an AssistantsUpdate to the remote view that will contain all the assistants that the client can play
     *
     * @param nickname the name of the client, if it is the name associated to the remote view the message will be
     *                 forwarded, otherwise no
     * @param values the assistants' ids that the client can play
     * @param motherNatureMovements the assistants mother nature movements, associated with the ids
     */
    public void sendAssistantUpdate(String nickname, Integer[] values, Integer[] motherNatureMovements) {
        if (nickname.equals(view.getPlayerNickname())) {
            MessagePayload payload = new MessagePayload();
            payload.setAttribute("Values", values);
            payload.setAttribute("MotherNatureMovements", motherNatureMovements);
            view.sendMessage(payload, "AssistantsUpdate", ServerMessageType.GAME_UPDATE);
        }
    }

    /**
     * Forwards a CloudsRefill to the remote view in order to inform the client of the students that are contained in the
     * clouds after a refill at the beginning of a new planning phase
     *
     * @param cloudsStudents the students of each cloud, the element i contains students for the cloud i
     */
    public void sendCloudRefill(RealmType[][] cloudsStudents) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CloudsStudents", cloudsStudents);
        view.sendMessage(payload, "CloudsRefill", ServerMessageType.GAME_UPDATE);
    }

    /**
     * Responds to an event that come from the game controller
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "EndTurn" -> endTurnEventPerformed((String) evt.getOldValue(), (String) evt.getSource(), (TurnPhase[]) evt.getNewValue());
            case "EndPhase" -> endPhaseEventPerformed((RoundPhase) evt.getOldValue(), (String) evt.getSource(), (TurnPhase[]) evt.getNewValue());
            case "AssistantsUpdate" -> sendAssistantUpdate((String) evt.getSource(), (Integer[]) evt.getOldValue(), (Integer[]) evt.getNewValue());
            case "CloudsRefill" -> sendCloudRefill((RealmType[][]) evt.getNewValue());
        }
    }
}
