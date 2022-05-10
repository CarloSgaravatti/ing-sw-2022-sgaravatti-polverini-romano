package it.polimi.ingsw.client.messageHandlers;

import it.polimi.ingsw.client.ConnectionToServer;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.TurnHandler;
import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.util.List;
import java.util.Map;

public class TurnMessageHandler extends BaseMessageHandler {
    private TurnHandler turnHandler;
    private static final List<String> messageHandled = List.of("EndTurn", "ChangePhase", "EndGame", "ActionAck");

    public TurnMessageHandler(ConnectionToServer connection, UserInterface userInterface, ModelView modelView) {
        super(connection, userInterface, modelView);
    }

    public void setTurnHandler(TurnHandler turnHandler) {
        this.turnHandler = turnHandler;
    }

    @Override
    public void handleMessage(MessageFromServer message) {
        ServerMessageHeader header = message.getServerMessageHeader();
        if (header.getMessageType() != ServerMessageType.GAME_UPDATE && !messageHandled.contains(header.getMessageName())) {
            getNextHandler().handleMessage(message);
            return;
        }
        MessagePayload payload = message.getMessagePayload();
        switch(header.getMessageName()) {
            case "EndTurn" -> onEndTurn(payload);
            case "ChangePhase" -> onChangePhase(payload);
            case "EndGame" -> onEndGame(payload);
            case "ActionAck" -> onActionAck(payload);
        }
    }

    private void onEndTurn(MessagePayload payload) {
        String newActivePlayer = payload.getAttribute("TurnStarter").getAsString();
        String oldActivePlayer = payload.getAttribute("TurnEnder").getAsString();
        getModelView().setCurrentActivePlayer(newActivePlayer);

        //TODO
        checkClientTurn(newActivePlayer);
    }

    private void onChangePhase(MessagePayload payload) {
        String starter = payload.getAttribute("Starter").getAsString();
        RoundPhase newPhase = (RoundPhase) payload.getAttribute("NewPhase").getAsObject();
        getModelView().setCurrentActivePlayer(starter);
        getModelView().setCurrentPhase(newPhase);
        if (newPhase == RoundPhase.ACTION) {
            Map<?, ?> newClouds = (Map<?, ?>) payload.getAttribute("CloudsRefill").getAsObject();
            for (Object o: newClouds.keySet()) {
                getModelView().getField().updateCloudStudents((Integer) o, (RealmType[]) newClouds.get(o));
            }
        }

        //TODO
        checkClientTurn(starter);
    }

    private void onEndGame(MessagePayload payload) {
        boolean isWin = payload.getAttribute("IsWinOrTie").getAsBoolean();
        String[] winnersOrTiers = (String[]) payload.getAttribute("WinnersOrTiers").getAsObject();

        //TODO
    }

    private void onActionAck(MessagePayload payload) {
        //TODO (notify turn handler)
    }

    private void checkClientTurn(String turnStarter) {
        if (turnStarter.equals(getUserInterface().getNickname())) {
            //TODO: notify turn handler
        }
    }
}
