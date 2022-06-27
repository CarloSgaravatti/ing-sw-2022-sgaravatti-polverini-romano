package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PlanningPhaseOrder is a strategy for the order of play calculation that is used to calculate the order at the
 * beginning of a new Planning Phase.
 * @see it.polimi.ingsw.controller.PhaseOrder
 */
public class PlanningPhaseOrder implements PhaseOrder {
	private boolean isFirstRound = true;
	private final List<Player> playersClockOrder;
	private final int numPlayers;

	/**
	 * Construct a new PlanningPhaseOrder instance that will calculate the order of play for the specified game during the
	 * planning phase.
	 *
	 * @param game the game that will be associated to the new instance
	 */
    public PlanningPhaseOrder(Game game) {
		playersClockOrder = game.getPlayers();
		numPlayers = game.getNumPlayers();
	}

	public boolean isFirstRound() {
		return isFirstRound;
	}

	public void restore(boolean isFirstRound) {
		this.isFirstRound = isFirstRound;
	}

	/**
	 * Calculates the order of play that will be based on the specified players order of the previous action phase.
	 *
	 * @param players the players on which the order is calculated, expressed as the previous action phase order
	 * @return the new planning phase order
	 */
	public Player[] calculateOrder(Player[] players) {
		Random rnd;
		int a;
		Player[] order = new Player[numPlayers];
		if (isFirstRound) {
			//Random generator
			rnd = new Random();
			a = rnd.nextInt(numPlayers - 1);
			//end Random generator
			order[0] = players[a];
			for(int i = 1; i < numPlayers; i++) {
				a = (a + 1) % numPlayers;
				order[i] = players[a];
			}
            isFirstRound = false;
			return order;
		}
		else {
			List<Player> orderList = new ArrayList<>();
			orderList.add(players[0]);
			int firstIndexOrderClockwise = playersClockOrder.indexOf(players[0]);
			for (int i = 1; i < numPlayers; i++) {
				orderList.add(playersClockOrder.get((i + firstIndexOrderClockwise) % numPlayers));
			}
        	return orderList.toArray(new Player[0]);
		}
	}
}
