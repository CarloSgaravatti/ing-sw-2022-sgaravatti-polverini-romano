package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.server.RemoteView;

public class RemoteViewStub extends RemoteView {
    MessageFromServer message;

    public RemoteViewStub(int gameId, String playerNickname, GameController controller) {
        super(null, gameId, playerNickname, null, controller);
    }

    @Override
    public void sendMessage(MessagePayload payload, String messageName, ServerMessageType messageType) {
        ServerMessageHeader header = new ServerMessageHeader(messageName, messageType);
        message = new MessageFromServer(header, payload);
    }

    @Override
    public void sendError(ErrorMessageType error) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("ErrorType", error);
        sendMessage(payload, "Error", ServerMessageType.SERVER_MESSAGE);
    }

    public MessageFromServer getMessage() {
        return message;
    }
}
