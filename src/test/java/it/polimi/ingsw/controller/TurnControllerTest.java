package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TurnControllerTest extends TestCase {
    TurnController turnController;
    GameController gameController;

    public static class InitControllerStub extends InitController {
        private final GameController gameController;
        private final Game game;

        public InitControllerStub(int numPlayers, boolean isExpertGame, GameController gameController) {
            super(numPlayers, isExpertGame);
            this.gameController = gameController;
            this.game = new Game(null, null, JsonUtils.constantsByNumPlayer(3), true);
        }

        @Override
        public void initializeGameComponents() {
            game.setNumPlayers(3);
            game.addPlayer("player1");
            game.addPlayer("player2");
            game.addPlayer("player3");
            gameController.setGame(game);
        }
    }

    @BeforeEach
    void setup() {
        gameController = new GameController(3, true);
        new InitControllerStub(3, true, gameController).initializeGameComponents();
        turnController = new TurnController(gameController.getModel().getPlayers()
                .toArray(new Player[0]), gameController.getModel());
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
    void endTurnWithoutChangePhaseTest() {
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
        turnController.endTurn();
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
    }

    @Test
    void endTurnWithChangePhaseTest() {
        Assertions.assertEquals(RoundPhase.PLANNING, turnController.getCurrentPhase());
        turnController.endTurn();
        turnController.endTurn();
        turnController.endTurn();
        Assertions.assertEquals(RoundPhase.ACTION, turnController.getCurrentPhase());
    }

    @Test
    void getActivePlayerTest() {
        //This is just to skip the first round planning phase order
        Player[] players = new Player[3];
        for (int i = 0; i < 3; i++) {
            players[i] = turnController.getActivePlayer();
            gameController.getModel().assignDeck(players[i], WizardType.values()[i]);
            try {
                players[i].playAssistant(i + 1, new ArrayList<>());
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