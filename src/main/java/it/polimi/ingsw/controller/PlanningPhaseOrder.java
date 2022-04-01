package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.Random;

public class PlanningPhaseOrder implements PhaseOrder {
	private Assistant[] assistantPlayed;
	private int numPlayedAssistants; //number of played assistants in the round.
	private int numRound = 0; //Round number.
	private int[] order;
	private Game game;
	private CharacterCard[] characterCard;
	private TurnEffect[] turnEffect;
	private TurnController turnController;



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
			turnController.setNextPlayer(players[a],0);
			for(int i=1;i< game.getNumPlayers();i++) {

				a= (a+1)%game.getNumPlayers();
				turnController.setNextPlayer(players[a],i);

			}
            numRound++;

		}
		else {

			int min;
			int firstPlayerNumber = 0;
			int position = 1;

			min= 1000;

			for(int i=0;i<game.getNumPlayers();i++) {

				if (players[i].getCardValue() < min) {
					min = players[i].getCardValue();
					turnController.setNextPlayer(players[i], 0);
					firstPlayerNumber = i;
				}
			}
			turnController.getPosition(0).setSelected();


			for(int k=firstPlayerNumber; k<game.getNumPlayers();k++) {
				turnController.setNextPlayer(players[k], position);
				position++;
			}
			for(int k=0; k<firstPlayerNumber;k++) {
				turnController.setNextPlayer(players[k],position);
			}






		}
		return null;
	}
}
