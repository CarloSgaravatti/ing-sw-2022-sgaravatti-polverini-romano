package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ActionMessageConstructor;
import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ActionInputParser;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

//Handles SERVER_MESSAGE messages
public class DefaultMessageHandler extends BaseMessageHandler {
    private final PropertyChangeSupport turnHandler = new PropertyChangeSupport(this);

    public DefaultMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    public void setTurnHandler(PropertyChangeListener turnHandler) {
        this.turnHandler.addPropertyChangeListener("Error", turnHandler);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if (header.getMessageType() != ServerMessageType.SERVER_MESSAGE) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch (header.getMessageName()) {
            case "NicknameRequest" -> onNicknameRequest(payload);
            case "GlobalLobby" -> onGeneralLobbyMessage(payload);
            case "GameLobby" -> onGameLobbyMessage(payload);
            case "Error" -> onErrorMessage(payload);
        }
    }

    private void onNicknameRequest(MessagePayload payload) {
        getUserInterface().displayStringMessage(payload.getAttribute("MessageInfo").getAsString());
        getUserInterface().askNickname();
    }

    private void onGeneralLobbyMessage(MessagePayload payload) {
        int numGames = payload.getAttribute("NotStartedGames").getAsInt();
        Map<?, ?> gamesInfo = (Map<?, ?>) payload.getAttribute("GamesInfo").getAsObject();
        getUserInterface().displayGlobalLobby(numGames, (Map<Integer, Pair<Integer, String[]>>) gamesInfo);
        getUserInterface().askLobbyDecision();
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
        //TODO: display error on user interface
        getUserInterface().displayStringMessage("Received an error");
        turnHandler.firePropertyChange("Error", null, payload.getAttribute("ErrorType").getAsObject());
    }
}
