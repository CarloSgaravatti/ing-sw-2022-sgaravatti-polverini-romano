package it.polimi.ingsw.model;
import java.util.Random;

public class Round {
	private Assistant[] assistantPlayed;
	private int numPlayedAssistants; //number of played assistants in the round.
	private int numRound; //Round number.
	private int[] order;
	private Game game;
	private CharacterCard[] characterCard;

	public Round(int numRound, Game game) {
		this.numRound = numRound;
	}


	public Assistant[] getAssistantPlayed() {

		return assistantPlayed;
	}


	public void insertAssistant(Assistant a) {
		numPlayedAssistants++;
		assistantPlayed[numPlayedAssistants]= a;

	}

	public void calculateStarter() { //da controllare il tipo del metodo.

		int o;
		Random r;
		int a;
		Player[] players =game.getPlayers();

		if(numRound==0){
			//Random generator
			r = new Random();
			o = game.getNumPlayers()- 1;
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
			for (int z = 1; z < game.getNumPlayers(); z++) {

				if (assistantPlayed[z].getCardValue() < min) {
					min = assistantPlayed[z].getCardValue();
					order[0] = z;
				}
			}
			for (int k = 1; k < game.getNumPlayers(); k++) {
				if (order[k-1] == game.getNumPlayers() - 1) {
					order[k] = 0;
				} else {
					order[k] = order[k-1] + 1;
				}


			}
		}

	}

	public void planningPhase() {
		Player[] players =game.getPlayers();
		for(int k=0;k<game.getNumPlayers();k++){

			for(int j=0;j<game.getNumPlayers();j++){

				if(game.getClouds()[j].getStudentsNumber() == 0) {
				players[k].chooseCloud(game.getClouds()[j]);
				}
			}
		}
	}

	public void actionPhase() {
		int i;
		Player[] players =game.getPlayers();
		for (i = 0; i < game.getNumPlayers(); i++) {
			players[order[i]].doActionPhase();
		}

	}
	//check if a player have already playied the same card assistant
	public int checkPlayedCardForAllPlayer(int ass, String nickname){
		Player[] players =game.getPlayers();
		int i;

		for(i=0;i<game.getNumPlayers();i++) {
			Assistant[] assistants = players[i].getAssistants();
			if(assistants[ass].getPlayed() && !players[i].getNickName().equals(nickname)){
				return i;
			}
		}

		return 0;
	}





}
