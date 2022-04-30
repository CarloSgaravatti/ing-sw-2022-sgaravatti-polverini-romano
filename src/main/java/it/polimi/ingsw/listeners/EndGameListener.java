package it.polimi.ingsw.listeners;

import it.polimi.ingsw.server.RemoteView;

import java.util.EventListener;
import java.util.List;

public class EndGameListener implements EventListener {
    List<RemoteView> clients;

    public EndGameListener(List<RemoteView> clients) {
        this.clients = clients;
    }

    //Se qualcuno vince
    public void onWinnerEvent() {

    }

    //Se non ci sono vincitori ma il gioco finisce
    public void onTieEvent() {

    }
}
