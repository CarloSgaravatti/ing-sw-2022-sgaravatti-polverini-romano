package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlanningPhaseOrder implements PhaseOrder {
	private boolean isFirstRound = true; //Round number.
	private final List<Player> playersClockOrder;
	private final int numPlayers;

    public PlanningPhaseOrder(Game game) {
		playersClockOrder = game.getPlayers();
		numPlayers = game.getNumPlayers();
	}

	//TODO: better naming for variables
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
