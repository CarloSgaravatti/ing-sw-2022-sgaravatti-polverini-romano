package it.polimi.ingsw.model;
import java.util.Random;

public class Round {
	private Assistant[] assistantPlayed;
	private int numPlayedAssistants; //number of played assistants in the round.
	private int n; //Round number.
	private int[] order;
	private Game game;

	private CharacterCard[] characterCard;

	public Round(int n) {
		this.n = n;
	}


	public Assistant[] getAssistantPlayed() {

		return assistantPlayed;
	}


	public void insertAssistant(Assistant a) {
		numPlayedAssistants++;
		assistantPlayed[numPlayedAssistants]= a;

	}

	public void calculateStarter(Player[] player) { //da controllare il tipo del metodo.
		int o;
		Random r;
		int a;
		if(n==0){
			//Random generator
			r = new Random();
			o = game.getNumPlayer()- 1;
			a = r.nextInt() % o;
			//end Random generator
			order[0] = a + 1;
			for(int k=1; k<=o;k++){
				if(order[k-1]==o){
					order[k] = 0;
				}
				else{
				order[k] = order[k-1] + 1;
			    }
		    }


		}
		else { //da completare con i metodi carte assistenti e i giocatori.
			int min = assistantPlayed[0].getCardValue();
			order[0] = 0;
			for (int z = 1; z < game.getNumPlayer(); z++) {

				if (assistantPlayed[z].getCardValue() < min) {
					min = assistantPlayed[z].getCardValue();
					order[0] = z;
				}
			}
			for (int k = 1; k < game.getNumPlayer(); k++) {
				if (order[k-1] == game.getNumPlayer() - 1) {
					order[k] = 0;
				} else {
					order[k] = order[k-1] + 1;
				}


			}
		}

	}

	public void planningPhase(Player[] player) {


		for(int k=0;k<game.getNumPlayer();k++){

			for(int j=0;j<game.getNumPlayer();j++){

				if(game.getClouds()[j].getStudentsNumber() == 0) {
				player[k].chooseCloud(game.getClouds()[j]);
			}
		}

		}

	}

	public void actionPhase() {







	}





}
