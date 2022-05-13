package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.TurnHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;

//Handles SERVER_MESSAGE messages
public class DefaultMessageHandler extends BaseMessageHandler {
    private PropertyChangeSupport turnHandler = new PropertyChangeSupport(this);

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
        String nickname = getUserInterface().askNickname();
        MessagePayload responsePayload = new MessagePayload();
        responsePayload.setAttribute("Nickname", nickname);
        sendResponse(ClientMessageType.GAME_SETUP, "NicknameMessage", responsePayload);
    }

    private void onGeneralLobbyMessage(MessagePayload payload) {
        int numGames = payload.getAttribute("NotStartedGames").getAsInt();
        Map<?, ?> gamesInfo = (Map<?, ?>) payload.getAttribute("GamesInfo").getAsObject();
        getUserInterface().displayGlobalLobby(numGames, (Map<Integer, Pair<Integer, List<String>>>) gamesInfo);
        boolean choiceMade = false;
        while (!choiceMade) {
            Pair<String, Integer> decision = getUserInterface().askGameToPlay();
            switch (decision.getFirst()) {
                case "NumPlayers" -> {
                    payload.setAttribute("NumPlayers", decision.getSecond());
                    choiceMade = true;
                    sendResponse(ClientMessageType.GAME_SETUP, "NumPlayers", payload);
                }
                case "Game" -> {
                    payload.setAttribute("GameId", decision.getSecond());
                    choiceMade = true;
                    sendResponse(ClientMessageType.GAME_SETUP, "GameToPlay", payload);
                }
                default -> getUserInterface().displayStringMessage("Error: answer not valid");
            }
        }
    }

    private void onGameLobbyMessage(MessagePayload payload) {
        //TODO
        //...

        int numPlayers = payload.getAttribute("NumPlayers").getAsInt();
        boolean rules = payload.getAttribute("Rules").getAsBoolean();
        String[] playersWaiting = (String[]) payload.getAttribute("WaitingPlayers").getAsObject();
        setModelView(new ModelView(rules));
        getConnection().addFirstMessageHandler(new GameSetupMessageHandler(getConnection(), getUserInterface(), getModelView()));
        getUserInterface().displayLobbyInfo(numPlayers, rules, playersWaiting);
    }

    private void onErrorMessage(MessagePayload payload) {
        //display error on user interface
        turnHandler.firePropertyChange("Error", null, payload.getAttribute("ErrorType").getAsObject());
    }
}
