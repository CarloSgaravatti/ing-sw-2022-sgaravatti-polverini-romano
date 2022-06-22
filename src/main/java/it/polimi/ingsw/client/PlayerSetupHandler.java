package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.ClientMessageType;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * PlayerSetupHandler is a PropertyChangeListener that will receive events from the user interface during the setup phase
 * of the client and of the game (from the nickname choice to the wizard choice in the game). PlayerSetupHandler will
 * parse the request and create the message for the server, then it will pass it to the ConnectionToServer.
 */
public class PlayerSetupHandler implements PropertyChangeListener {
    private final ConnectionToServer connection;

    /**
     * Constructs a new instance of PlayerSetupHandler that is associated to the specified ConnectionToServer
     *
     * @param connection the connection from the client to the server that will send messages
     */
    public PlayerSetupHandler(ConnectionToServer connection) {
        this.connection = connection;
    }

    /**
     * Responds to an event fired by the UserInterface.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "Nickname" -> onNicknameSelection((String) evt.getNewValue());
            case "NewGame" -> onNewGameDecision((Integer) evt.getOldValue(), (Boolean) evt.getNewValue());
            case "GameToPlay" -> onGameToPlayDecision((Integer) evt.getNewValue());
            case "TowerChoice" -> onTowerSelection((TowerType) evt.getNewValue());
            case "WizardChoice" -> onWizardSelection((WizardType) evt.getNewValue());
            case "RefreshLobby" -> connection.sendMessage(new MessagePayload(), "RefreshGlobalLobby", ClientMessageType.GAME_SETUP);
        }
    }

    /**
     * Create a new NicknameMessage that contains the specified nickname that will be sent to the server
     *
     * @param nickname the nickname chosen by the client
     */
    private void onNicknameSelection(String nickname) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Nickname", nickname);
        connection.setNickname(nickname);
        connection.sendMessage(payload, "NicknameMessage", ClientMessageType.GAME_SETUP);
    }

    /**
     * Create a new NewGame message that informs the server that the client wants to create a new game with the
     * specified number of players and the specified rules type.
     *
     * @param numPlayers the number of players of the game
     * @param expertGame the rules of the game
     */
    private void onNewGameDecision(int numPlayers, boolean expertGame) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("NumPlayers", numPlayers);
        payload.setAttribute("GameRules", expertGame);
        connection.sendMessage(payload, "NewGame", ClientMessageType.GAME_SETUP);
    }

    /**
     * Create a new GameToPlay message that contains the specified id of the game the clients wants to play
     *
     * @param gameId the id of the game chosen by the client
     */
    private void onGameToPlayDecision(int gameId) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("GameId", gameId);
        connection.sendMessage(payload, "GameToPlay", ClientMessageType.GAME_SETUP);
    }

    /**
     * Create a new TowerChoice message that contains the tower chosen by the client
     *
     * @param tower the tower chosen by the client
     */
    private void onTowerSelection(TowerType tower) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Tower", tower);
        connection.sendMessage(payload, "TowerChoice", ClientMessageType.PLAYER_SETUP);
    }

    /**
     * Create a new WizardChoice message that contains the tower chosen by the client
     *
     * @param wizard the wizard chosen by the client
     */
    private void onWizardSelection(WizardType wizard) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Wizard", wizard);
        connection.sendMessage(payload, "WizardChoice", ClientMessageType.PLAYER_SETUP);
    }
}
