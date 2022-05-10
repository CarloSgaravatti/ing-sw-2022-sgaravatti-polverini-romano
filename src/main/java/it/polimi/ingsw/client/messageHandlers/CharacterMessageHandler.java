package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;

import java.util.List;

public class CharacterMessageHandler extends BaseMessageHandler {
    private static final List<String> messageHandled =
            List.of("CharacterPlayed", "CharacterStudents", "NoEntryTileUpdate", "StudentSwap");

    public CharacterMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if(header.getMessageType() != ServerMessageType.GAME_UPDATE && !messageHandled.contains(header.getMessageName())) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch (header.getMessageName()) {
            case "CharacterPlayed" -> onCharacterPlayed(payload);
            case "CharacterStudents" -> onCharacterStudents(payload);
            case "NoEntryTileUpdate" -> onNoEntryTileUpdate(payload);
            case "StudentSwap" -> onStudentSwap(payload);
        }
    }

    private void onCharacterPlayed(MessagePayload payload) {

    }

    private void onCharacterStudents(MessagePayload payload) {

    }

    private void onNoEntryTileUpdate(MessagePayload payload) {

    }

    private void onStudentSwap(MessagePayload payload) {

    }
}
