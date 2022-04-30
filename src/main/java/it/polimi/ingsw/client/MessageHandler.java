package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.utils.Pair;

import java.util.List;
import java.util.Map;

public class MessageHandler {
    private final ConnectionToServer connection;
    private final UserInterface view;

    public MessageHandler(ConnectionToServer connection, UserInterface view) {
        this.connection = connection;
        this.view = view;
    }

    public void handleMessage(MessageFromServer message) {
        ServerMessageType messageType = message.getServerMessageHeader().getMessageType();
        switch (messageType) {
            case SERVER_MESSAGE -> handleSetupMessage(message);
            case GAME_UPDATE -> handleGameUpdateMessage(message);
            case GAME_SETUP -> handleGameSetupMessage(message);
        }
    }

    //Solo per prova, può essere fatto meglio
    public void handleSetupMessage(MessageFromServer message) {
        String messageName = message.getServerMessageHeader().getMessageName();
        ClientMessageHeader header = null;
        MessagePayload payload = new MessagePayload();
        switch (messageName) {
            case "Error" -> System.err.println("Error"); //TODO
            case "NicknameRequest" -> {
                view.displayStringMessage(message.getMessagePayload().getAttribute("MessageInfo").getAsString());
                String nickname = view.askNickname();
                header = new ClientMessageHeader("NicknameMessage", null, null);
                payload.setAttribute("Nickname", nickname);
                connection.asyncWriteToServer(new MessageFromClient(header, payload));
            }
            case "GeneralLobby" -> {
                int numGames = message.getMessagePayload().getAttribute("NotStartedGames").getAsInt();
                Map<?, ?> gamesInfo = (Map<?, ?>) message.getMessagePayload().getAttribute("GamesInfo").getAsObject();
                view.displayGameLobby(numGames, (Map<Integer, Pair<Integer, List<String>>>) gamesInfo);
                boolean choiceMade = false;
                while (!choiceMade) {
                    Pair<String, Integer> decision = view.askGameToPlay();
                    switch (decision.getFirst()) {
                        case "NumPlayers" -> {
                            header = new ClientMessageHeader("NumPlayers", view.getNickname(),
                                    ClientMessageType.GAME_SETUP);
                            payload.setAttribute("NumPlayers", decision.getSecond());
                            choiceMade = true;
                        }
                        case "Game" -> {
                            header = new ClientMessageHeader("GameToPlay", view.getNickname(),
                                    ClientMessageType.GAME_SETUP);
                            payload.setAttribute("GameId", decision.getSecond());
                            choiceMade = true;
                        }
                        default -> view.displayStringMessage("Error: answer not valid");
                    }
                }
                connection.asyncWriteToServer(new MessageFromClient(header, payload));
            }
            case "GameLobby" -> {

            }
            //case "ConnectionClosed" ->
        }
    }

    public void handleGameUpdateMessage(MessageFromServer message) {

    }

    public void handleGameSetupMessage(MessageFromServer message) {

    }
}
