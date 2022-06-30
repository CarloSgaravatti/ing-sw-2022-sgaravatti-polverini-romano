package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.server.RemoteView;
import it.polimi.ingsw.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameControllerTest {
    GameController controller;
    RemoteViewStub view1;
    RemoteViewStub view2;

    public static class GameLobbyStub extends GameLobby {
        private final Map<String, RemoteViewStub> fakeViews = new HashMap<>();

        public GameLobbyStub() {
            super(1, 2, true, null, false);
        }

        public void createRemoteViews(Pair<String, RemoteViewStub> player1, Pair<String, RemoteViewStub> player2) {
            fakeViews.put(player1.getFirst(), player1.getSecond());
            fakeViews.put(player1.getFirst(), player2.getSecond());
        }

        @Override
        public void broadcast(MessageFromServer message) {
            for (RemoteViewStub remoteViewStub: fakeViews.values()) {
                remoteViewStub.sendMessage(message.getMessagePayload(), message.getServerMessageHeader().getMessageName(),
                        message.getServerMessageHeader().getMessageType());
            }
        }

        @Override
        public void setSaveGame() {/*does nothing*/}
    }

    @BeforeEach
    void setup() {
        controller = new GameController(2, true);
        try {
            controller.getInitController().initializeGameComponents();
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        controller.setGame(controller.getInitController().getGame());
        controller.getInitController().addPlayer("player1");
        controller.getInitController().addPlayer("player2");
        Player player1 = controller.getModel().getPlayerByNickname("player1");
        Player player2 = controller.getModel().getPlayerByNickname("player2");
        try {
            controller.getInitController().setupPlayerTower(player1, TowerType.BLACK);
            controller.getInitController().setupPlayerTower(player2, TowerType.WHITE);
            controller.getInitController().setupPlayerWizard(player1, WizardType.values()[0]);
            controller.getInitController().setupPlayerWizard(player2, WizardType.values()[1]);
        } catch (WizardTypeAlreadyTakenException | TowerTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        controller.initializeControllers();
        GameLobbyStub gameLobby = new GameLobbyStub();
        List<RemoteView> views = new ArrayList<>();
        RemoteView view1 = new RemoteViewStub("player1", controller, gameLobby);
        RemoteView view2 = new RemoteViewStub("player2", controller, gameLobby);
        views.add(view1);
        views.add(view2);
        this.view1 = (RemoteViewStub) view1;
        this.view2 = (RemoteViewStub) view2;
        gameLobby.createRemoteViews(new Pair<>("player1", this.view1), new Pair<>("player2", this.view2));
        controller.createListeners(views, gameLobby);
    }

    @Test
    void propertyChangeWrongTurnErrorTest() {
        String nickname = controller.getTurnController().getActivePlayer().getNickName();
        String sender = (nickname.equals("player1")) ? "player2" : "player1";
        ClientMessageHeader header = new ClientMessageHeader("EndTurn", sender, ClientMessageType.ACTION);
        MessageFromClient message = new MessageFromClient(header, new MessagePayload());
        RemoteViewStub viewNullMessage = (sender.equals("player1")) ? view2 : view1;
        RemoteViewStub viewNotNullMessage = (sender.equals("player1")) ? view1 : view2;
        controller.propertyChange(new PropertyChangeEvent(viewNotNullMessage, "ActionMessage", null, message));
        Assertions.assertNull(viewNullMessage.getMessage());
        Assertions.assertEquals("Error", viewNotNullMessage.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals(ServerMessageType.SERVER_MESSAGE,
                viewNotNullMessage.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals(ErrorMessageType.ILLEGAL_TURN,
                viewNotNullMessage.getMessage().getMessagePayload().getAttribute("ErrorType").getAsObject());
    }

    @Test
    void propertyChangeEndTurnErrorTest() {
        controller.getActionController().setTurnPhase(TurnPhase.PLAY_ASSISTANT);
        String nickname = controller.getTurnController().getActivePlayer().getNickName();
        ClientMessageHeader header = new ClientMessageHeader("EndTurn", nickname, ClientMessageType.ACTION);
        MessageFromClient message = new MessageFromClient(header, new MessagePayload());
        RemoteViewStub viewNullMessage = (nickname.equals("player1")) ? view2 : view1;
        RemoteViewStub viewNotNullMessage = (nickname.equals("player1")) ? view1 : view2;
        controller.propertyChange(new PropertyChangeEvent(viewNotNullMessage, "ActionMessage", null, message));
        Assertions.assertNull(viewNullMessage.getMessage());
        Assertions.assertEquals("Error", viewNotNullMessage.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals(ServerMessageType.SERVER_MESSAGE,
                viewNotNullMessage.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals(ErrorMessageType.TURN_NOT_FINISHED,
                viewNotNullMessage.getMessage().getMessagePayload().getAttribute("ErrorType").getAsObject());
    }

    @Test
    void propertyChangeCorrectEndTurnTest() {
        controller.getActionController().setTurnPhase(TurnPhase.TURN_ENDED);
        String turnEnder = controller.getTurnController().getActivePlayer().getNickName();
        ClientMessageHeader header = new ClientMessageHeader("EndTurn", turnEnder, ClientMessageType.ACTION);
        MessageFromClient message = new MessageFromClient(header, new MessagePayload());
        controller.propertyChange(new PropertyChangeEvent(turnEnder, "ActionMessage", null, message));
        String turnStarter = controller.getTurnController().getActivePlayer().getNickName();
        Assertions.assertEquals("EndTurn", view1.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals("EndTurn", view2.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals(ServerMessageType.GAME_UPDATE,
                view1.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals(ServerMessageType.GAME_UPDATE,
                view2.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals(turnEnder, view1.getMessage().getMessagePayload().getAttribute("TurnEnder").getAsString());
        Assertions.assertEquals(turnEnder, view2.getMessage().getMessagePayload().getAttribute("TurnEnder").getAsString());
        Assertions.assertEquals(turnStarter, view1.getMessage().getMessagePayload().getAttribute("TurnStarter").getAsString());
        Assertions.assertEquals(turnStarter, view2.getMessage().getMessagePayload().getAttribute("TurnStarter").getAsString());
    }

    @Test
    void propertyChangeTurnActionTest() {
        Player player = controller.getTurnController().getActivePlayer();
        ClientMessageHeader header = new ClientMessageHeader("PlayAssistant", player.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Assistant", 1);
        MessageFromClient message = new MessageFromClient(header, payload);
        RemoteViewStub viewWithAckMessage = (player.getNickName().equals("player1")) ? view1 : view2;
        controller.propertyChange(new PropertyChangeEvent(viewWithAckMessage, "ActionMessage", null, message));
        Assertions.assertEquals("ActionAck", viewWithAckMessage.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals(ServerMessageType.ACK_MESSAGE,
                viewWithAckMessage.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals("PlayAssistant",
                viewWithAckMessage.getMessage().getMessagePayload().getAttribute("ActionName").getAsString());
    }

    @Test
    void startTest() {
        controller.startGame();
        Assertions.assertTrue(controller.getModel().isStarted());
    }
}
