package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.utils.Triplet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

/**
 * DefaultMessageHandler handles all messages that have SERVER_MESSAGE as message type
 *
 * @see it.polimi.ingsw.client.messageHandlers.MessageHandler
 * @see it.polimi.ingsw.client.messageHandlers.BaseMessageHandler
 */
public class DefaultMessageHandler extends BaseMessageHandler {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    /**
     * Constructs a new DefaultMessageHandler that will be associated to the specified connection to the server, user
     * interface and model view
     *
     * @param connection the connection to the server that will pass the messages
     * @param userInterface the user interface of the client
     * @param modelView the model view of the client.
     */
    public DefaultMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
        listeners.addPropertyChangeListener("Disconnection", userInterface);
        listeners.addPropertyChangeListener("GameDeleted", userInterface);
    }

    /**
     * Sets the value of the turn handler that will be informed when an error will arrive from the server
     *
     * @param turnHandler the turn handler that will be associated to the message handler
     */
    public void setTurnHandler(PropertyChangeListener turnHandler) {
        if (listeners.getPropertyChangeListeners("Error").length != 0) {
            PropertyChangeListener oldTurnHandler = listeners.getPropertyChangeListeners("Error")[0];
            this.listeners.removePropertyChangeListener("Error", oldTurnHandler);
        }
        this.listeners.addPropertyChangeListener("Error", turnHandler);
    }

    /**
     * Handles a message that have been arrived from the server
     *
     * @param message the message from the server
     * @see MessageHandler#handleMessage(MessageFromServer)
     */
    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if (header.getMessageType() != ServerMessageType.SERVER_MESSAGE && getNextHandler() != null) {
            System.out.println(header.getMessageName());
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch (header.getMessageName()) {
            case "NicknameRequest" -> getUserInterface().askNickname();
            case "GlobalLobby" -> onGeneralLobbyMessage(payload);
            case "GameLobby" -> onGameLobbyMessage(payload);
            case "Error" -> onErrorMessage(payload);
            case "PlayerDisconnected" -> onPlayerDisconnection(payload);
            case "PreviousGameChoice" -> onResumeGame(payload);
            case "DeletedGame" -> onDeleteGame(payload);
        }
    }

    /**
     * Handles a GlobalLobby message arrived from the server
     *
     * @param payload the payload of the message
     */
    private void onGeneralLobbyMessage(MessagePayload payload) {
        int numGames = payload.getAttribute("NotStartedGames").getAsInt();
        Map<?, ?> gamesInfo = (Map<?, ?>) payload.getAttribute("GamesInfo").getAsObject();
        getUserInterface().displayGlobalLobby(numGames, (Map<Integer, Triplet<Integer, Boolean, String[]>>) gamesInfo);
        getConnection().reset(this);
    }

    /**
     * Handles a GameLobby message arrived from the server
     *
     * @param payload the payload of the message
     */
    private void onGameLobbyMessage(MessagePayload payload) {
        int numPlayers = payload.getAttribute("GameNumPlayers").getAsInt();
        boolean rules = payload.getAttribute("Rules").getAsBoolean();
        String[] playersWaiting = (String[]) payload.getAttribute("WaitingPlayers").getAsObject();
        setModelView(new ModelView(rules));
        for (String player: playersWaiting) {
            getModelView().getPlayers().put(player, new PlayerView());
        }
        getConnection().addFirstMessageHandler(new GameSetupMessageHandler(getConnection(), getUserInterface(), getModelView()));
        getUserInterface().displayLobbyInfo(numPlayers, rules, playersWaiting);
    }

    /**
     * Handles an Error message arrived from the server by informing both the turn handler and the user interface
     *
     * @param payload the payload of the message
     */
    private void onErrorMessage(MessagePayload payload) {
        ErrorMessageType errorMessageType = (ErrorMessageType) payload.getAttribute("ErrorType").getAsObject();
        String errorInfo = payload.getAttribute("ErrorInfo").getAsString();
        getUserInterface().onError(errorMessageType, errorInfo);
        listeners.firePropertyChange("Error", null, errorMessageType);
    }

    /**
     * Informs the user interface that a player have been disconnected from the game after a PlayerDisconnected message
     * arrived from the server
     *
     * @param payload the payload of the message
     */
    private void onPlayerDisconnection(MessagePayload payload) {
        listeners.firePropertyChange("Disconnection", null, payload.getAttribute("PlayerName").getAsString());
    }

    /**
     * Informs the user interface that he can resume a previous saved game after a PreviousGameChoice message arrived from
     * the server
     *
     * @param payload the payload of the message
     */
    private void onResumeGame(MessagePayload payload) {
        int numPlayers = payload.getAttribute("NumPlayers").getAsInt();
        boolean rules = payload.getAttribute("Rules").getAsBoolean();
        String[] participants = (String[]) payload.getAttribute("Participants").getAsObject();
        getUserInterface().onResumeGame(numPlayers, rules, participants);
    }

    /**
     * Informs the user interface that a player has decided to not resume and game and therefore the game has been deleted
     *
     * @param payload the payload of the message
     */
    private void onDeleteGame(MessagePayload payload) {
        listeners.firePropertyChange("GameDeleted", null, payload.getAttribute("ChoiceMaker").getAsString());
    }
}
