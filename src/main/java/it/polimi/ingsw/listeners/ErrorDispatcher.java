package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class ErrorDispatcher implements EventListener {
    private List<RemoteView> clients;

    public ErrorDispatcher(List<RemoteView> views) {
        this.clients = views;
    }

    public void onErrorEvent(ErrorMessageType error, String errorCommitter) {
        for (RemoteView view: clients) {
            if (view.getPlayerNickname().equals(errorCommitter)) {
                view.sendError(error);
                return;
            }
        }
    }
}
