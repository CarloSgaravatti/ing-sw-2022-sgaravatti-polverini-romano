package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.ClientMessageType;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PlayerSetupHandler implements PropertyChangeListener {
    private final ConnectionToServer connection;

    public PlayerSetupHandler(ConnectionToServer connection) {
        this.connection = connection;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "Nickname" -> onNicknameSelection((String) evt.getNewValue());
            case "NewGame" -> onNewGameDecision((Integer) evt.getOldValue(), (Boolean) evt.getNewValue());
            case "GameToPlay" -> onGameToPlayDecision((Integer) evt.getNewValue());
            case "TowerChoice" -> onTowerSelection((TowerType) evt.getNewValue());
            case "WizardChoice" -> onWizardSelection((WizardType) evt.getNewValue());
        }
    }

    private void onNicknameSelection(String nickname) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Nickname", nickname);
        connection.setNickname(nickname);
        connection.sendMessage(payload, "NicknameMessage", ClientMessageType.GAME_SETUP);
    }

    private void onNewGameDecision(int numPlayers, boolean expertGame) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("NumPlayers", numPlayers);
        payload.setAttribute("GameRules", expertGame);
        connection.sendMessage(payload, "NewGame", ClientMessageType.GAME_SETUP);
    }

    private void onGameToPlayDecision(int gameId) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("GameId", gameId);
        connection.sendMessage(payload, "GameToPlay", ClientMessageType.GAME_SETUP);
    }

    private void onTowerSelection(TowerType tower) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Tower", tower);
        connection.sendMessage(payload, "TowerChoice", ClientMessageType.PLAYER_SETUP);
    }

    private void onWizardSelection(WizardType wizard) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Wizard", wizard);
        connection.sendMessage(payload, "WizardChoice", ClientMessageType.PLAYER_SETUP);
    }
}
