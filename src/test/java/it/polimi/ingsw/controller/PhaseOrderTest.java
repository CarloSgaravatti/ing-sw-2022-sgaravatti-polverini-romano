package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.WizardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//TODO: these classes need more accurate tests
class PhaseOrderTest {
    PlanningPhaseOrder planningPhaseOrder;
    ActionPhaseOrder actionPhaseOrder;
    Game game;

    @BeforeEach
    void setUp() {
        game = new Game(null, null);
        for (int i = 0; i < 3; i++) {
            game.addPlayer("generic player");
            game.assignDeck(game.getPlayers().get(i), WizardType.values()[i]);
            try {
                game.getPlayers().get(i).playAssistant(i+1,new ArrayList<>());
            } catch (NoSuchAssistantException e) {
                fail();
            }
        }
        planningPhaseOrder = new PlanningPhaseOrder(game);
        actionPhaseOrder = new ActionPhaseOrder(game);
    }

    @Test
    void calculateOrderPlanningPhase() {
        //skip first round random order
        Player[] order = planningPhaseOrder.calculateOrder(game.getPlayers().toArray(new Player[0]));
        order = planningPhaseOrder.calculateOrder(game.getPlayers().toArray(new Player[0]));
        for(int i = 0; i < 3; i++) {
            assertEquals(game.getPlayers().get(i),order[i]);
        }
    }

    @Test
    void calculateOrderActionPhase(){
        Player[] order = actionPhaseOrder.calculateOrder(game.getPlayers().toArray(new Player[0]));
        for(int i = 0; i < 3; i++) {
            assertEquals(game.getPlayers().get(i), order[i]);
        }
    }


}