package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.server.GameLobby;

import java.util.EventListener;
import java.util.List;

public class EndGameListener implements EventListener {
    private final GameLobby lobby;

    public EndGameListener(GameLobby lobby) {
        this.lobby = lobby;
    }

    //Se qualcuno vince
    public void onWinnerEvent(String winner) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Winner", winner);
        ServerMessageHeader header = new ServerMessageHeader("EndGameWinner", ServerMessageType.GAME_UPDATE);
        notifyLobby(new MessageFromServer(header, payload));
    }

    //Se non ci sono vincitori ma il gioco finisce
    public void onTieEvent(List<String> tiers) {
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
