package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.RemoteView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class GameControllerTest {
    GameController controller;
    RemoteViewStub view1;
    RemoteViewStub view2;

    @BeforeEach
    void setup() {
        controller = new GameController(1, 2, true);
        try {
            controller.getInitController().initializeGameComponents();
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        controller.setGame();
        controller.getInitController().addPlayer("player1");
        controller.getInitController().addPlayer("player2");
        List<RemoteView> views = new ArrayList<>();
        RemoteView view1 = new RemoteViewStub(1, "player1", controller);
        RemoteView view2 = new RemoteViewStub(1, "player2", controller);
        views.add(view1);
        views.add(view2);
        controller.initializeControllers();
        controller.createListeners(views, null);
        this.view1 = (RemoteViewStub) view1;
        this.view2 = (RemoteViewStub) view2;
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

    //TODO: end turn when is not an error (after adding a listener)
}
