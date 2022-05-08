package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;

import java.util.List;

public class GameUpdateMessageHandler extends BaseMessageHandler {
    private static final List<String> messageHandled =
            List.of("AssistantPlayed", "ProfessorUpdate", "MotherNatureMovement", "SchoolDiningRoomUpdate",
                    "IslandStudentsUpdate", "IslandUnification", "IslandTowerUpdate", "PickFromCloud");

    public GameUpdateMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if(header.getMessageType() != ServerMessageType.GAME_UPDATE && !messageHandled.contains(header.getMessageName())) {
            getNextHandler().handleMessage(message);
            return;
        }
        //...
    }
}
