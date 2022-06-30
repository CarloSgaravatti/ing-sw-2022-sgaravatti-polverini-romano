package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.enumerations.*;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * PlayerSetupListener is a PropertyChangeListener that listen to the InitController in order to inform clients that a
 * player has chosen a tower or a wizard in the setup phase.
 *
 * @see java.beans.PropertyChangeListener
 */
public class PlayerSetupListener implements PropertyChangeListener {
    private static final String TOWER = "Tower";
    private static final String WIZARD = "Wizard";
    private final RemoteView remoteView;

    /**
     * Constructs a new PlayerSetupListener that will be associated to the specified remote view
     *
     * @param remoteView the remote view to which messages will be forwarded
     */
    public PlayerSetupListener(RemoteView remoteView){
        this.remoteView = remoteView;
    }

    /**
     * Responds to an InitController event that contains a player setup choice
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case TOWER -> onTowerChoice((TowerType) evt.getOldValue(), (String) evt.getNewValue());
            case WIZARD -> onWizardChoice((WizardType) evt.getOldValue(), (String) evt.getNewValue());
        }
    }

    /**
     * Forwards a TowerTaken message to the remote view that will inform clients that the specified player has chosen
     * the specified tower
     *
     * @param type the tower that was chosen
     * @param playerName the name of the player
     */
    private void onTowerChoice(TowerType type, String playerName){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("TowerType", type);
        messagePayload.setAttribute("PlayerName", playerName);
        remoteView.sendMessage(messagePayload,"TowerTaken", ServerMessageType.GAME_SETUP);
    }

    /**
     * Forwards a WizardTaken message to the remote view that will inform clients that the specified player has chosen
     * the specified wizard
     *
     * @param type the wizard that was chosen
     * @param playerName the name of the player
     */
    private void onWizardChoice(WizardType type, String playerName){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("WizardType", type);
        messagePayload.setAttribute("PlayerName", playerName);
        remoteView.sendMessage(messagePayload,"WizardTaken", ServerMessageType.GAME_SETUP);
    }
}
