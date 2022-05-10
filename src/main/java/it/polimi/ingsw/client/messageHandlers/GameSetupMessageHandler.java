package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.PlayerSetupHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleField;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.util.Map;

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
            case "TowerTypeRequest", "WizardTypeRequest" -> onRequestMessage(message); //maybe two separate messages
            case "SetupAck" -> onSetupAck(payload);
        }
    }

    private void onPlayerJoin(MessagePayload payload) {
        String playerName = payload.getAttribute("Nickname").getAsString();
        getModelView().getPlayers().put(playerName, new PlayerView());

        //TODO
    }

    private void onTowerTaken(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        TowerType tower = (TowerType) payload.getAttribute("TowerType").getAsObject();
        getModelView().getPlayers().get(playerName).setTower(tower);

        //TODO
    }

    private void onWizardTaken(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        WizardType wizard = (WizardType) payload.getAttribute("WizardType").getAsObject();
        getModelView().getPlayers().get(playerName).setWizard(wizard);

        //TODO
    }

    private void onGameInitializationMessage(MessagePayload payload) {
        //...
        FieldView fieldView = new FieldView((SimpleField) payload.getAttribute("Field").getAsObject());
        getModelView().setField(fieldView);
        Map<?,?> schoolEntrances = (Map<?, ?>) payload.getAttribute("Schools").getAsObject();
        for (String player: getModelView().getPlayers().keySet()) {
            getModelView().getPlayers().get(player).getSchoolStudents().setFirst((Integer[]) schoolEntrances.get(player));
        }
        if (getModelView().isExpert()) {
            getConnection().addFirstMessageHandler(new CharacterMessageHandler(getConnection(), getUserInterface(), getModelView()));
        }
        getConnection().addFirstMessageHandler(new TurnMessageHandler(getConnection(), getUserInterface(), getModelView()));
        getConnection().addFirstMessageHandler(new GameUpdateMessageHandler(getConnection(), getUserInterface(), getModelView()));
    }

    private void onRequestMessage(MessageFromServer message) {
        //notify player setup handler
        //TODO
    }

    private void onSetupAck(MessagePayload payload) {
        //TODO
    }
}
