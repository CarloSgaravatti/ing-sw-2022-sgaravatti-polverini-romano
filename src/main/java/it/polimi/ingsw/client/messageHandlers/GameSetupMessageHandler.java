package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleField;
import it.polimi.ingsw.messages.simpleModel.SimpleModel;
import it.polimi.ingsw.messages.simpleModel.SimplePlayer;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

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
            System.out.println("Game setup message handler has passed message " + header.getMessageName());
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        System.out.println("Received " + header.getMessageName());
        switch (header.getMessageName()) {
            case "PlayerJoined" -> onPlayerJoin(payload);
            case "GameStarted" -> onGameStarted(payload);
            case "TowerTaken" -> onTowerTaken(payload);
            case "WizardTaken" -> onWizardTaken(payload);
            case "GameInitializations" -> onGameInitializationMessage(payload);
            case "TowerTypeRequest" -> onTowerTypeRequest(payload);
            case "WizardTypeRequest" -> onWizardTypeRequest(payload);
            case "SetupAck" -> onSetupAck(payload);
            case "RestoredSetup" -> onRestoredSetup(payload);
            case "GameRestoredData" -> onRestoredGameInitialization(payload);
        }
    }

    private void onPlayerJoin(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
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
        FieldView fieldView = new FieldView((SimpleField) payload.getAttribute("Field").getAsObject());
        getModelView().setField(fieldView);
        SimplePlayer[] playersInfo = (SimplePlayer[]) payload.getAttribute("PlayersInfo").getAsObject();
        for (SimplePlayer player : playersInfo) {
            String playerName = player.getNickname();
            getModelView().getPlayers().get(playerName).resetStudentsTo(player.getEntrance(), new RealmType[0]);
            getModelView().getPlayers().get(playerName).updateNumTowers(player.getNumTowers());
            getModelView().getPlayers().get(playerName).updateCoins(player.getNumCoins());
        }
        createAllHandlers();
        getUserInterface().onGameInitialization(getModelView());
    }

    private void onTowerTypeRequest(MessagePayload payload) {
        getUserInterface().askTowerChoice((TowerType[]) payload.getAttribute("FreeTowers").getAsObject());
    }

    private void onWizardTypeRequest(MessagePayload payload) {
        getUserInterface().askWizardChoice((WizardType[]) payload.getAttribute("FreeWizards").getAsObject());
    }

    private void onSetupAck(MessagePayload payload) {
        //TODO: decide if this message is useful
    }

    private void onRestoredGameInitialization(MessagePayload payload){
        SimpleModel simpleModel = (SimpleModel) payload.getAttribute("SimpleModel").getAsObject();
        FieldView fieldView = new FieldView(simpleModel.getField());
        getModelView().setField(fieldView);
        for (SimplePlayer player : simpleModel.getSchools()) {
            String playerName = player.getNickname();
            getModelView().getPlayers().get(playerName).resetStudentsTo(player.getEntrance(), player.getDiningRoom());
            getModelView().getPlayers().get(playerName).updateNumTowers(player.getNumTowers());
            getModelView().getPlayers().get(playerName).updateCoins(player.getNumCoins());
            getModelView().getPlayers().get(playerName).setTower(simpleModel.getTowers().get(playerName));
            getModelView().getPlayers().get(playerName).setWizard(simpleModel.getWizards().get(playerName));
            Pair<Integer, Integer> assistants = player.getLastAssistant();
            getModelView().getPlayers().get(playerName).updateLastPlayedAssistant(assistants.getFirst(), assistants.getSecond());
        }
        String[] professorOwners = simpleModel.getProfessorOwners();
        for(int i = 0; i < simpleModel.getProfessorOwners().length; i++){
            fieldView.updateProfessorOwner(RealmType.values()[i], professorOwners[i]);
        }
        createAllHandlers();
        getUserInterface().onGameInitialization(getModelView());
    }

    private void createAllHandlers() {
        TurnHandler turnHandler = new TurnHandler(getConnection(), getUserInterface());
        ((DefaultMessageHandler) getNextHandler()).setTurnHandler(turnHandler);
        if (getModelView().isExpert()) {
            getConnection().addFirstMessageHandler(new ExpertGameMessageHandler(getConnection(), getUserInterface(), getModelView()));
        }
        TurnMessageHandler turnMessageHandler = new TurnMessageHandler(getConnection(), getUserInterface(), getModelView());
        getConnection().addFirstMessageHandler(turnMessageHandler);
        turnMessageHandler.setTurnHandler(turnHandler);
        getConnection().addFirstMessageHandler(new GameUpdateMessageHandler(getConnection(), getUserInterface(), getModelView()));
        ActionMessageConstructor messageConstructor = new ActionMessageConstructor(getConnection());
        ActionInputParser inputParser = new ActionInputParser(messageConstructor, turnHandler, getUserInterface(), getModelView());
        getUserInterface().addListener(inputParser, "MoveStudents");
        getUserInterface().addListener(inputParser, "MoveMotherNature");
        getUserInterface().addListener(inputParser, "PickFromCloud");
        getUserInterface().addListener(inputParser, "PlayCharacter");
        getUserInterface().addListener(inputParser, "PlayAssistant");
        getUserInterface().addListener(messageConstructor, "EndTurn");
    }

    private void onRestoredSetup(MessagePayload payload) {
        SimpleModel simpleModel = (SimpleModel) payload.getAttribute("SetupInfo").getAsObject();
        Map<String, TowerType> towers = simpleModel.getTowers();
        Map<String, WizardType> wizards = simpleModel.getWizards();
        towers.keySet().forEach(playerName -> getModelView().getPlayers().get(playerName).setTower(towers.get(playerName)));
        wizards.keySet().forEach(playerName -> getModelView().getPlayers().get(playerName).setWizard(wizards.get(playerName)));
    }
}
