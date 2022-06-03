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

//Handles SERVER_MESSAGE messages
public class DefaultMessageHandler extends BaseMessageHandler {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    public DefaultMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
        listeners.addPropertyChangeListener("Disconnection", userInterface);
    }

    public void setTurnHandler(PropertyChangeListener turnHandler) {
        if (listeners.getPropertyChangeListeners("Error").length != 0) {
            PropertyChangeListener oldTurnHandler = listeners.getPropertyChangeListeners("Error")[0];
            this.listeners.removePropertyChangeListener("Error", oldTurnHandler);
        }
        this.listeners.addPropertyChangeListener("Error", turnHandler);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if (header.getMessageType() != ServerMessageType.SERVER_MESSAGE) {
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
        }
    }

    private void onGeneralLobbyMessage(MessagePayload payload) {
        int numGames = payload.getAttribute("NotStartedGames").getAsInt();
        Map<?, ?> gamesInfo = (Map<?, ?>) payload.getAttribute("GamesInfo").getAsObject();
        getUserInterface().displayGlobalLobby(numGames, (Map<Integer, Triplet<Integer, Boolean, String[]>>) gamesInfo);
        getConnection().reset(this);
    }

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

    private void onErrorMessage(MessagePayload payload) {
        getUserInterface().onError((ErrorMessageType) payload.getAttribute("ErrorType").getAsObject());
        listeners.firePropertyChange("Error", null, payload.getAttribute("ErrorType").getAsObject());
    }

    private void onPlayerDisconnection(MessagePayload payload) {
        listeners.firePropertyChange("Disconnection", null, payload.getAttribute("PlayerName").getAsString());
    }
}
