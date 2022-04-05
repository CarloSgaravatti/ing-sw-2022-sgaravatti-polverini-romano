package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TurnControllerTest extends TestCase {
    TurnController turnController;
    GameController gameController;

    @BeforeEach
    void setup() {
        gameController = new GameController();
        Game game = new Game(null, null);
        game.addPlayer("player1");
        game.addPlayer("player2");
        game.addPlayer("player3");
        gameController.setGame(game);
        turnController = new TurnController(game.getPlayers().toArray(new Player[0]), game);
    }

    @Test
    void changePhaseTest() {
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
        turnController.changePhase();
        Assertions.assertEquals(RoundPhase.ACTION, turnController.getCurrentPhase());
        turnController.changePhase();
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
    }

    @Test
    void endTurnWithChangePhaseTest() {
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
        turnController.endTurn();
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
    }

    @Test
    void endTurnWithoutChangePhaseTest() {
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
        turnController.endTurn();
        turnController.endTurn();
        turnController.endTurn();
        Assertions.assertEquals(RoundPhase.ACTION, turnController.getCurrentPhase());
    }

    @Test
    //TODO: to run this test phase order needs to be finished
    void getActivePlayerTest() {
        //This is just to skip the first round planning phase order
        Player[] players = new Player[3];
        for (int i = 0; i < 3; i++) {
            players[i] = turnController.getActivePlayer();
            try {
                players[i].playAssistant(i, new ArrayList<>());
            } catch (NoSuchAssistantException e) {
                Assertions.fail();
            }
            Assertions.assertTrue(gameController.getModel().getPlayers().contains(players[i]));
            turnController.endTurn();
        }
        //At this moment the order for the action phase has to be: players[0], players[1], players[2]
        Assertions.assertEquals(players[0], turnController.getActivePlayer());
        turnController.endTurn();
        Assertions.assertEquals(players[1], turnController.getActivePlayer());
        turnController.endTurn();
        Assertions.assertEquals(players[2], turnController.getActivePlayer());
        turnController.endTurn();
    }
}