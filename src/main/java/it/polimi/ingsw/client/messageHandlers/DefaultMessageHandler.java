package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.TurnHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.utils.Pair;

import java.util.List;
import java.util.Map;

//Handles SERVER_MESSAGE messages
public class DefaultMessageHandler extends BaseMessageHandler {
    private TurnHandler turnHandler; //For error messages

    public DefaultMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    public void setTurnHandler(TurnHandler turnHandler) {
        this.turnHandler = turnHandler;
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if (header.getMessageType() != ServerMessageType.SERVER_MESSAGE) {
            getNextHandler().handleMessage(message);
            return;
        }
        switch (header.getMessageName()) {
            case "NicknameRequest" -> handleNicknameRequest(message);
            case "GeneralLobby" -> handleGeneralLobby(message);
            case "GameLobby" -> handleGameLobby(message);
            case "Error" -> handleError(message);
        }
    }

    private void handleNicknameRequest(MessageFromServer message) {
        getUserInterface().displayStringMessage(message.getMessagePayload().getAttribute("MessageInfo").getAsString());
        String nickname = getUserInterface().askNickname();
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Nickname", nickname);
        sendResponse(ClientMessageType.GAME_SETUP, "NicknameMessage", payload);
    }

    private void handleGeneralLobby(MessageFromServer message) {
        int numGames = message.getMessagePayload().getAttribute("NotStartedGames").getAsInt();
        MessagePayload payload = new MessagePayload();
        Map<?, ?> gamesInfo = (Map<?, ?>) message.getMessagePayload().getAttribute("GamesInfo").getAsObject();
        getUserInterface().displayGameLobby(numGames, (Map<Integer, Pair<Integer, List<String>>>) gamesInfo);
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

    private void handleGameLobby(MessageFromServer message) {
        //TODO
        //...

        getConnection().addFirstMessageHandler(new GameSetupMessageHandler(getConnection(), getUserInterface(), getModelView()));
    }

    private void handleError(MessageFromServer message) {
        //display error and notify turn handler
    }
}
