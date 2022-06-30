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

/**
 * GameSetupMessageHandler handles all messages that have GAME_SETUP as message type
 *
 * @see it.polimi.ingsw.client.messageHandlers.MessageHandler
 * @see it.polimi.ingsw.client.messageHandlers.BaseMessageHandler
 */
public class GameSetupMessageHandler extends BaseMessageHandler{
    /**
     * Constructs a new GameSetupMessageHandler that will be associated to the specified connection to the server, user
     * interface and model view
     *
     * @param connection the connection to the server that will pass the messages
     * @param userInterface the user interface of the client
     * @param modelView the model view of the client.
     */
    public GameSetupMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    /**
     * Handles a message that have been arrived from the server
     *
     * @param message the message from the server
     * @see MessageHandler#handleMessage(MessageFromServer)
     */
    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if(header.getMessageType() != ServerMessageType.GAME_SETUP && !header.getMessageName().equals("SetupAck")) {
            System.out.println("Game setup message handler has passed message " + header.getMessageName());
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
            case "RestoredSetup" -> onRestoredSetup(payload);
            case "GameRestoredData" -> onRestoredGameInitialization(payload);
        }
    }

    /**
     * Notifies the user interface that a player have joined the game lobby
     *
     * @param payload the payload of the message
     */
    private void onPlayerJoin(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        getModelView().getPlayers().put(playerName, new PlayerView());
        getUserInterface().onPlayerJoined(playerName);
    }

    /**
     * Notifies the user interface that the game has started
     *
     * @param payload the payload of the message
     */
    private void onGameStarted(MessagePayload payload) {
        String[] gamePlayers = (String[]) payload.getAttribute("Opponents").getAsObject();
        for (String player: gamePlayers) {
            getModelView().getPlayers().put(player, new PlayerView());
        }
        getUserInterface().onGameStarted();
    }

    /**
     * Modifies the model view after someone has taken a tower
     *
     * @param payload the payload of the message
     */
    private void onTowerTaken(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        TowerType tower = (TowerType) payload.getAttribute("TowerType").getAsObject();
        getModelView().getPlayers().get(playerName).setTower(tower);
    }

    /**
     * Modifies the model view after someone has taken a wizard
     *
     * @param payload the payload of the message
     */
    private void onWizardTaken(MessagePayload payload) {
        String playerName = payload.getAttribute("PlayerName").getAsString();
        WizardType wizard = (WizardType) payload.getAttribute("WizardType").getAsObject();
        getModelView().getPlayers().get(playerName).setWizard(wizard);
    }

    /**
     * Modifies the model view after the initializations of the game have arrived, and create all remaining handlers
     * that will handle message that regard the game. Also, the method informs the user interface that the setup phase
     * has finished and the real game will start
     *
     * @param payload the payload of the message
     */
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

    /**
     * Informs the user interface that he has to choose a tower from a set of towers contained in the specified
     * message payload
     *
     * @param payload the payload of the message
     */
    private void onTowerTypeRequest(MessagePayload payload) {
        getUserInterface().askTowerChoice((TowerType[]) payload.getAttribute("FreeTowers").getAsObject());
    }

    /**
     * Informs the user interface that he has to choose a wizard from a set of wizards contained in the specified
     * message payload
     *
     * @param payload the payload of the message
     */
    private void onWizardTypeRequest(MessagePayload payload) {
        getUserInterface().askWizardChoice((WizardType[]) payload.getAttribute("FreeWizards").getAsObject());
    }

    private void onSetupAck(MessagePayload payload) {
        //TODO: decide if this message is useful
    }

    /**
     * Modifies the model view after the initializations of a restored game have arrived, and create all remaining handlers
     * that will handle message that regard the game. Also, the method informs the user interface that the real game will
     * start soon
     *
     * @param payload the payload of the message
     */
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
            Pair<Integer, Integer> assistant = player.getLastAssistant();
            getModelView().getPlayers().get(playerName).updateLastPlayedAssistant(assistant.getFirst(), assistant.getSecond());
        }
        String[] professorOwners = simpleModel.getProfessorOwners();
        for(int i = 0; i < simpleModel.getProfessorOwners().length; i++){
            fieldView.updateProfessorOwner(RealmType.values()[i], professorOwners[i]);
        }
        createAllHandlers();
        getUserInterface().onGameInitialization(getModelView());
    }

    /**
     * Create all handlers that will handle GAME_UPDATE messages from the server and all the handlers that will listen the
     * user interface during the game in order to send back messages to the server
     */
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

    /**
     * Informs the user interface that the saved game that was previously in the setup phase has been restored. The method
     * will also modify the model view with the choices that were already been made by some players
     *
     * @param payload the payload of the message
     */
    private void onRestoredSetup(MessagePayload payload) {
        SimpleModel simpleModel = (SimpleModel) payload.getAttribute("SetupInfo").getAsObject();
        Map<String, TowerType> towers = simpleModel.getTowers();
        Map<String, WizardType> wizards = simpleModel.getWizards();
        towers.keySet().forEach(playerName -> getModelView().getPlayers().get(playerName).setTower(towers.get(playerName)));
        wizards.keySet().forEach(playerName -> getModelView().getPlayers().get(playerName).setWizard(wizards.get(playerName)));
    }
}
