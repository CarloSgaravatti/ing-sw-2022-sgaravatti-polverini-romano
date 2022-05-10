package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class ErrorDispatcher implements PropertyChangeListener {
    private final List<RemoteView> clients;

    public ErrorDispatcher(List<RemoteView> views) {
        this.clients = views;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ErrorMessageType error = (ErrorMessageType) evt.getOldValue();
        String errorCommitter = (String) evt.getNewValue();
        for (RemoteView view: clients) {
            if (view.getPlayerNickname().equals(errorCommitter)) {
                view.sendError(error);
                return;
            }
        }
    }
}
