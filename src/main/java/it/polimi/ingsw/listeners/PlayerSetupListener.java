package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.enumerations.*;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PlayerSetupListener implements PropertyChangeListener {
    private static final String TOWER = "Tower";
    private static final String WIZARD = "Wizard";
    private final RemoteView remoteView;

    public PlayerSetupListener(RemoteView remoteView){
        this.remoteView = remoteView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case TOWER -> onTowerChoice((TowerType) evt.getOldValue(), (String) evt.getNewValue());
            case WIZARD -> onWizardChoice((WizardType) evt.getOldValue(), (String) evt.getNewValue());
        }
    }

    private void onTowerChoice(TowerType type, String playerName){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("towerType", type);
        messagePayload.setAttribute("playerName", playerName);
        remoteView.sendMessage(messagePayload,"TowerTaken", ServerMessageType.GAME_SETUP);
    }

    private void onWizardChoice(WizardType type, String playerName){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("wizardType", type);
        messagePayload.setAttribute("playerName", playerName);
        remoteView.sendMessage(messagePayload,"WizardTaken", ServerMessageType.GAME_SETUP);
    }
}
