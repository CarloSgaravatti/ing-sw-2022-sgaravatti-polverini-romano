package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.PlayerSetupHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;

//Handles GAME_SETUP and SetupAck messages
public class GameSetupMessageHandler extends BaseMessageHandler{
    private final PlayerSetupHandler handler = new PlayerSetupHandler();

    public GameSetupMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if(header.getMessageType() != ServerMessageType.GAME_SETUP && !header.getMessageName().equals("SetupAck")) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch (header.getMessageName()) {
            case "PlayerJoined" -> onPlayerJoin(payload);
            case "TowerTaken" -> onTowerTaken(payload);
            case "WizardTaken" -> onWizardTaken(payload);
            case "GameInitializations" -> onGameInitializationMessage(payload);
            case "TowerTypeRequest", "WizardTypeRequest" -> onRequestMessage(message);
            case "SetupAck" -> onSetupAck(payload);
        }
    }

    private void onPlayerJoin(MessagePayload payload) {

    }

    private void onTowerTaken(MessagePayload payload) {

    }

    private void onWizardTaken(MessagePayload payload) {

    }

    private void onGameInitializationMessage(MessagePayload payload) {
        //... (initialize model view)

        if (getModelView().isExpert()) {
            getConnection().addFirstMessageHandler(new CharacterMessageHandler(getConnection(), getUserInterface(), getModelView()));
        }
        getConnection().addFirstMessageHandler(new TurnMessageHandler(getConnection(), getUserInterface(), getModelView()));
        getConnection().addFirstMessageHandler(new GameUpdateMessageHandler(getConnection(), getUserInterface(), getModelView()));
    }

    private void onRequestMessage(MessageFromServer message) {
        //notify player setup handler
    }

    private void onSetupAck(MessagePayload payload) {

    }
}
