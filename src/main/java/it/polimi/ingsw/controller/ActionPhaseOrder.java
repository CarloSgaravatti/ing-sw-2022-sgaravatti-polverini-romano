package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ActionPhaseOrder implements PhaseOrder {
	//Doesn't need attributes
	private Game game;
	private TurnController turnController;
	//private Player[] order;
	private final List<Player> players;
	private final int numPlayers;

	public ActionPhaseOrder (Game game){
		players = game.getPlayers();
		numPlayers = game.getNumPlayers();
	}

	public Player[] calculateOrder(Player[] players) {
		/*//int[] min = new int[0]; //?
		int min;
		Player[] order = new Player[numPlayers];
		for(int k = 0; k < numPlayers; k++) {
			min = 1000;
			for(int i = 0;i < numPlayers; i++) {
				if(!players[i].getSelected()) {
					if (players[i].getCardValue() < min) {
						min = players[i].getCardValue();
						order[k] = players[i];//turnController.setNextPlayer(players[i], k);
					}
					turnController.getPosition(k).setSelected();
				}
			}
		}
        return order;*/
		List<Player> order = new ArrayList<>(Arrays.asList(players));
		//Comparator.comparingInt is equal to do
		//(p1, p2) -> p1.getTurnEffect().getOrderPrecedence() - p2.getTurnEffect().getOrderPrecedence()
		order.sort(Comparator.comparingInt(p -> p.getTurnEffect().getOrderPrecedence()));
		return order.toArray(new Player[0]);
	}

}
