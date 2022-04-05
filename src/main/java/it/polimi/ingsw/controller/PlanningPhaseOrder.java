package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.List;
import java.util.Random;

public class PlanningPhaseOrder implements PhaseOrder {
	private Assistant[] assistantPlayed;
	private int numPlayedAssistants; //number of played assistants in the round.
	private int numRound = 0; //Round number.
	private TurnController turnController;
	private Game game;
	private Player[] order;
	private List<Player> players;




    public PlanningPhaseOrder(Game game) {

		players = game.getPlayers();



	}
	public Player[] calculateOrder(Player[] players) {
		int o;
		Random r;
		int a;


		if (numRound == 0) {
			//Random generator
			r = new Random();
			o = game.getNumPlayers() - 1;
			a = r.nextInt() % o;
			//end Random generator
			order[0] = players[a];//turnController.setNextPlayer(players[a],0);
			for(int i=1;i< game.getNumPlayers();i++) {

				a= (a+1)%game.getNumPlayers();
				order[i] = players[a];//turnController.setNextPlayer(players[a],i);

			}
            numRound++;
			return order;

		}
		else {

			int min;
			int firstPlayerNumber = 0;
			int position = 1;

			min= 1000;

			for(int i=0;i<game.getNumPlayers();i++) {

				if (players[i].getCardValue() < min) {
					min = players[i].getCardValue();
					order[0] = players[i];//turnController.setNextPlayer(players[i], 0);
					firstPlayerNumber = i;
				}
			}
			turnController.getPosition(0).setSelected();


			for(int k=firstPlayerNumber; k<game.getNumPlayers();k++) {
				order[position] = players[k];//turnController.setNextPlayer(players[k], position);
				position++;
			}
			for(int k=0; k<firstPlayerNumber;k++) {
				order[position] = players[k];//turnController.setNextPlayer(players[k],position);
			}





        return order;
		}

	}
}
