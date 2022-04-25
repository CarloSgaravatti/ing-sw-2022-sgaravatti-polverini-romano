package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.model.Assistant;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerations.WizardType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

//TODO: these classes need more accurate tests
class PhaseOrderTest {
    PlanningPhaseOrder planningPhaseOrder;
    ActionPhaseOrder actionPhaseOrder;
    Game game;

    @BeforeEach
    void setUp() {
        game = new Game(null, null);
        game.setNumPlayers(3);
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
        //skip first round random order
        Player[] order = planningPhaseOrder.calculateOrder(game.getPlayers().toArray(new Player[0]));
    }

    @Test
    void calculateOrderPlanningPhaseBasicTest() {
        Player[] order = planningPhaseOrder.calculateOrder(game.getPlayers().toArray(new Player[0]));
        for(int i = 0; i < 3; i++) {
            assertEquals(game.getPlayers().get(i),order[i]);
        }
    }

    @Test
    void calculateOrderActionPhaseBasicTest(){
        Player[] order = actionPhaseOrder.calculateOrder(game.getPlayers().toArray(new Player[0]));
        for(int i = 0; i < 3; i++) {
            assertEquals(game.getPlayers().get(i), order[i]);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(it.polimi.ingsw.controller.PlayerOrderArgumentProvider.class)
    //Assistant to play are already in the order of the planning phase order (not in the game.getPlayers() order)
    void calculateOrderAdvancedTests(int[] previousActionOrder, int[] assistantsToPlay, int[] planningOrder, int[] actionOrder) {
        Player[] initialActionPhaseOrder = new Player[3];
        List<Integer> assistantAlreadyPlayed = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            initialActionPhaseOrder[i] = game.getPlayers().get(previousActionOrder[i]);
        }
        Player[] order = planningPhaseOrder.calculateOrder(initialActionPhaseOrder);
        for(int i = 0; i < 3; i++) {
            assertEquals(game.getPlayers().get(planningOrder[i]), order[i]);
        }
        for (int i = 0; i < 3; i++) {
            try {
                boolean res = order[i].playAssistant(assistantsToPlay[i], assistantAlreadyPlayed);
                //To play an assistant that has already been played, we have to eliminate all other assistants
                //of the player (just for the test) in order to make that assistant "playable" from him
                if (!res) {
                    boolean found = false;
                    for (int j = 0; j < order[i].getAssistants().size() && !found; j++) {
                        if (order[i].getAssistants().get(j).getCardValue() == assistantsToPlay[i]) {
                            Assistant assistant = order[i].getAssistants().get(j);
                            order[i].getAssistants().clear();
                            order[i].getAssistants().add(assistant);
                            found = true;
                        }
                    }
                    order[i].playAssistant(assistantsToPlay[i], assistantAlreadyPlayed);
                }
            } catch (NoSuchAssistantException e) {
                Assertions.fail();
            }
            assistantAlreadyPlayed.add(assistantsToPlay[i]);
        }
        order = actionPhaseOrder.calculateOrder(order);
        for(int i = 0; i < 3; i++) {
            assertEquals(game.getPlayers().get(actionOrder[i]), order[i]);
        }
    }
}

class PlayerOrderArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(new int[]{0, 1, 2}, new int[]{5, 7, 10}, new int[]{0, 1, 2}, new int[]{0, 1, 2}),
                Arguments.of(new int[]{2, 1, 0}, new int[]{6, 5, 4}, new int[]{2, 0, 1}, new int[]{1, 0, 2}),
                Arguments.of(new int[]{0, 2, 1}, new int[]{9, 9, 8}, new int[]{0, 1, 2}, new int[]{2, 0, 1}),
                Arguments.of(new int[]{1, 0, 2}, new int[]{9, 8, 9}, new int[]{1, 2, 0}, new int[]{2, 1, 0})
        );
    }
}