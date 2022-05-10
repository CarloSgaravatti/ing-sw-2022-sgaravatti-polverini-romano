package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.messages.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RemoteView extends View implements PropertyChangeListener {
    private final ClientConnection connection;
    private final int gameId;
    private final String playerNickname;
    private final GameLobby gameLobby;

    public RemoteView(ClientConnection connection, int gameId, String playerNickname, GameLobby gameLobby, GameController controller) {
        super(controller);
        this.connection = connection;
        connection.addListener("RemoteView", this);
        this.gameId = gameId;
        this.playerNickname = playerNickname;
        this.gameLobby = gameLobby;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public void sendMessage(MessagePayload payload, String messageName, ServerMessageType messageType) {
        ServerMessageHeader messageHeader = new ServerMessageHeader(messageName, messageType, gameId);
        MessageFromServer messageFromServer = new MessageFromServer(messageHeader, payload);
        connection.asyncSend(messageFromServer);
    }

    public void sendError(ErrorMessageType error) {
        connection.sendError(error);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        MessageFromClient message = (MessageFromClient) evt.getNewValue();
        ClientMessageType messageType = message.getClientMessageHeader().getMessageType();
        //Message type will always be different from GAME_SETUP
        switch (messageType) {
            case PLAYER_SETUP -> {
                fireSetupMessageEvent(message); //Need to call also setup in GameLobby
                gameLobby.notifySetupChanges();
            }
            case ACTION -> fireActionMessageEvent(message);
        }
    }
}
