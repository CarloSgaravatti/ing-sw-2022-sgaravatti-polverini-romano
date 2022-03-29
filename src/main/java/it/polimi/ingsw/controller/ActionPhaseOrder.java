package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

public class ActionPhaseOrder implements PhaseOrder {
	private Game game;
	private TurnController turnController;

	public Player[] calculateOrder(Player[] players) {
		int[] min = new int[0];
		for(int k=0; k<game.getNumPlayers();k++) {


			min[k] = 1000;

			for(int i=0;i<game.getNumPlayers();i++) {
				if(!players[i].getSelected()) {

					if (players[i].getCardValue() < min[k]) {
						min[k] = players[i].getCardValue();
						turnController.setNextPlayer(players[i], k);
					}
					turnController.getPosition(k).setSelected();
				}
			}


		}

		return null;
	}

}
