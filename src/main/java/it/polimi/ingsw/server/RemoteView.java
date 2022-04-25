package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Player;

import java.util.EventListener;

public class RemoteView extends View implements EventListener {
    private final ClientConnection connection;
    private final int gameId;
    private final Player player;

    public RemoteView(ClientConnection connection, int gameId, Player player, GameController gameController) {
        super(gameController);
        this.connection = connection;
        this.gameId = gameId;
        this.player = player;
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
            case PLAYER_SETUP -> fireSetupMessageEvent(message);
            case ACTION -> fireActionMessageEvent(message);
        }
    }
}
