package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.GameLobby;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

/**
 * EndGameListener is a PropertyChangeListener that listen events from Game when the game is finished
 *
 * @see java.beans.PropertyChangeListener
 */
public class EndGameListener implements PropertyChangeListener {
    private final GameLobby lobby;

    /**
     * Constructs an EndGameListener that will use the specified game lobby to broadcast end game events
     *
     * @param lobby the game lobby of the game
     */
    public EndGameListener(GameLobby lobby) {
        this.lobby = lobby;
    }

    /**
     * Responds to an end game event
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        List<String> winnersOrTiers = Arrays.asList((String[]) evt.getSource());
        boolean isWin = (boolean) evt.getNewValue();
        if (isWin) onWinnerEvent(winnersOrTiers.get(0));
        else onTieEvent(winnersOrTiers);
    }

    /**
     * Constructs and EndGameWinner message that will be sent in broadcast by the game lobby
     *
     * @param winner the winner of the game
     */
    private void onWinnerEvent(String winner) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Winner", winner);
        ServerMessageHeader header = new ServerMessageHeader("EndGameWinner", ServerMessageType.GAME_UPDATE);
        notifyLobby(new MessageFromServer(header, payload));
    }

    /**
     * Constructs and EndGameTied message that will be sent in broadcast by the game lobby
     *
     * @param tiers the tiers of the game
     */
    private void onTieEvent(List<String> tiers) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Tiers", tiers);
        ServerMessageHeader header = new ServerMessageHeader("EndGameTied", ServerMessageType.GAME_UPDATE);
        notifyLobby(new MessageFromServer(header, payload));
    }

    /**
     * Forward the specified message to the game lobby that will broadcast the message and that will do end game operations
     *
     * @param message the end game message
     */
    private void notifyLobby(MessageFromServer message) {
        lobby.broadcast(message);
        lobby.doEndGameOperations();
    }
}
