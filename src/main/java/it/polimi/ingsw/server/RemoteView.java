package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Player;

import java.util.EventListener;

public class RemoteView extends View implements EventListener {
    private final ClientConnection connection;
    private final int gameId;
    private final String playerNickname;
    private final GameLobby gameLobby;

    public RemoteView(ClientConnection connection, int gameId, String playerNickname, GameLobby gameLobby) {
        super(gameLobby.getGameController());
        this.connection = connection;
        this.gameId = gameId;
        this.playerNickname = playerNickname;
        this.gameLobby = gameLobby;
    }

    public void sendMessage(MessagePayload payload, String messageName, ServerMessageType messageType) {
        ServerMessageHeader messageHeader = new ServerMessageHeader(messageName, messageType, gameId);
        MessageFromServer messageFromServer = new MessageFromServer(messageHeader, payload);
        connection.asyncSend(messageFromServer);
    }

    public void eventPerformed(MessageFromClient message) {
        ClientMessageType messageType = message.getClientMessageHeader().getMessageType();
        //Message type will always be different from GAME_SETUP
        switch (messageType) {
            case PLAYER_SETUP -> {
                try {
                    fireSetupMessageEvent(message); //Need to call also setup in GameLobby
                    gameLobby.notifySetupChanges();
                } catch (WizardTypeAlreadyTakenException e) {
                    connection.sendError(ErrorMessageType.WIZARD_ALREADY_TAKEN);
                } catch (TowerTypeAlreadyTakenException e) {
                    connection.sendError(ErrorMessageType.TOWER_ALREADY_TAKEN);
                }
            }
            case ACTION -> fireActionMessageEvent(message);
        }
    }
}
