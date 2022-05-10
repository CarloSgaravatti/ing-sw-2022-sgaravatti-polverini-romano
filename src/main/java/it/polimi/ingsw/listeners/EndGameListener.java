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

public class EndGameListener implements PropertyChangeListener {
    private final GameLobby lobby;

    public EndGameListener(GameLobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        List<String> winnersOrTiers = Arrays.asList((String[]) evt.getSource());
        boolean isWin = (boolean) evt.getNewValue();
        if (isWin) onWinnerEvent(winnersOrTiers.get(0));
        else onTieEvent(winnersOrTiers);
    }

    //Se qualcuno vince
    private void onWinnerEvent(String winner) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Winner", winner);
        ServerMessageHeader header = new ServerMessageHeader("EndGameWinner", ServerMessageType.GAME_UPDATE);
        notifyLobby(new MessageFromServer(header, payload));
    }

    //Se non ci sono vincitori ma il gioco finisce
    private void onTieEvent(List<String> tiers) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Tiers", tiers);
        ServerMessageHeader header = new ServerMessageHeader("EndGameTied", ServerMessageType.GAME_UPDATE);
        notifyLobby(new MessageFromServer(header, payload));
    }

    private void notifyLobby(MessageFromServer message) {
        lobby.broadcast(message);
        lobby.doEndGameOperations();
    }
}
