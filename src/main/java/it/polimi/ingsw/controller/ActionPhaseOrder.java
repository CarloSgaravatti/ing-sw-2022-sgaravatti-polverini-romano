package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ActionPhaseOrder is a strategy for the order of play calculation that is used to calculate the order at the
 * beginning of a new Action Phase.
 * @see it.polimi.ingsw.controller.PhaseOrder
 */

public class ActionPhaseOrder implements PhaseOrder {
	/**
	 * Calculates the order of play that will be based on the specified players order of the previous planning phase.
	 * To calculate the order, the values of the played assistants for each player will be used.ù
	 *
	 * @param players the players on which the order is calculated
	 * @return the order of play of the new action phase
	 */
	public Player[] calculateOrder(Player[] players) {
		List<Player> order = new ArrayList<>(Arrays.asList(players));
		order.sort((p1, p2) -> {
			int p1Precedence = p1.getTurnEffect().getOrderPrecedence();
			int p2Precedence = p2.getTurnEffect().getOrderPrecedence();
			if (p1Precedence != p2Precedence) return p1Precedence - p2Precedence;
			if (p1.getTurnEffect().isFirstPlayedAssistant()) return -1; //p1 plays first
			if (p2.getTurnEffect().isFirstPlayedAssistant()) return 1; //p2 plays first
			return 0; //if three or more players have played the same assistant
		});
		return order.toArray(new Player[0]);
	}
}
