package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.TurnHandler;
import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleField;
import it.polimi.ingsw.messages.simpleModel.SimplePlayer;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.util.Arrays;
import java.util.Map;

//Handles GAME_SETUP and SetupAck messages
public class GameSetupMessageHandler extends BaseMessageHandler{

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
            case "GameStarted" -> onGameStarted(payload);
            case "TowerTaken" -> onTowerTaken(payload);
            case "WizardTaken" -> onWizardTaken(payload);
            case "GameInitializations" -> onGameInitializationMessage(payload);
            case "TowerTypeRequest" -> onTowerTypeRequest(payload);
            case "WizardTypeRequest" -> onWizardTypeRequest(payload);
            case "SetupAck" -> onSetupAck(payload);
        }
    }

    private void onPlayerJoin(MessagePayload payload) {
        String playerName = payload.getAttribute("Nickname").getAsString();
        getModelView().getPlayers().put(playerName, new PlayerView());
        getUserInterface().displayStringMessage(playerName + " has joined the game");
    }

    private void onGameStarted(MessagePayload payload) {
        String[] gamePlayers = (String[]) payload.getAttribute("Opponents").getAsObject();
        for (String player: gamePlayers) {
            getModelView().getPlayers().put(player, new PlayerView());
        }
        //Temporary
        getUserInterface().displayStringMessage("Game has started, these are the players: " + Arrays.toString(gamePlayers));
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
        SimplePlayer[] playersInfo = (SimplePlayer[]) payload.getAttribute("PlayersInfo").getAsObject();
        for (SimplePlayer player: playersInfo) {
            String playerName = player.getNickname();
            getModelView().getPlayers().get(playerName).updateEntrance(player.getEntrance(), true);
            if (playerName.equals(getUserInterface().getNickname())) {
                getModelView().setClientPlayerAssistants(player.getAssistants());
            }
        }
        TurnHandler turnHandler = new TurnHandler(getModelView().isExpert(), getConnection(), getUserInterface());
        ((DefaultMessageHandler)getNextHandler()).setTurnHandler(turnHandler);
        if (getModelView().isExpert()) {
            getConnection().addFirstMessageHandler(new CharacterMessageHandler(getConnection(), getUserInterface(), getModelView()));
        }
        TurnMessageHandler turnMessageHandler = new TurnMessageHandler(getConnection(), getUserInterface(), getModelView());
        getConnection().addFirstMessageHandler(turnMessageHandler);
        turnMessageHandler.setTurnHandler(turnHandler);
        getConnection().addFirstMessageHandler(new GameUpdateMessageHandler(getConnection(), getUserInterface(), getModelView()));

        //TODO: notify user (information will be printed on screen)
    }

    private void onTowerTypeRequest(MessagePayload payload) {
        getUserInterface().askTowerChoice((TowerType[]) payload.getAttribute("FreeTowers").getAsObject());
    }

    private void onWizardTypeRequest(MessagePayload payload) {
        getUserInterface().askWizardChoice((WizardType[]) payload.getAttribute("FreeWizards").getAsObject());
    }

    private void onSetupAck(MessagePayload payload) {
        //TODO
    }
}
