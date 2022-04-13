package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlanningPhaseOrder implements PhaseOrder {
	//These two attributes are not useful
	private Assistant[] assistantPlayed;
	private int numPlayedAssistants; //number of played assistants in the round.
	private int numRound = 0; //Round number.
	private TurnController turnController; //TODO: eliminate this
	//private Player[] order; //TODO: this field is better if it is a local variable
	private final List<Player> playersClockOrder; //TODO: use this
	private final int numPlayers;

    public PlanningPhaseOrder(Game game) {
		playersClockOrder = game.getPlayers();
		numPlayers = game.getNumPlayers();
	}

	//TODO: better naming for variables
	//I have replaced game.getNumPlayers with numPlayers
	//There was a misunderstanding, players array is the last action phase order (with the exception of round 0)
	public Player[] calculateOrder(Player[] players) {
		Random r;
		int a;
		Player[] order = new Player[numPlayers]; //This field is better if it is a local variable
		if (numRound == 0) {
			//Random generator
			r = new Random();
			a = r.nextInt(numPlayers - 1);
			//end Random generator
			order[0] = players[a];//turnController.setNextPlayer(players[a],0);
			for(int i = 1; i < numPlayers; i++) {
				a = (a + 1) % numPlayers;
				order[i] = players[a];//turnController.setNextPlayer(players[a],i);
			}
            numRound++;
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
