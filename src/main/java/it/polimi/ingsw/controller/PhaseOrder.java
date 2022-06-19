package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

/**
 * PhaseOrder uses a Strategy pattern to calculate the current RoundPhase order. The order is calculated in base of the
 * current phase, the previous order and the players turn effects
 * @see TurnController
 * @see it.polimi.ingsw.model.TurnEffect
 */
public interface PhaseOrder {

	/**
	 * Calculates the order of the specified players.
	 * @param players the players on which the order is calculated
	 * @return the order of play
	 */
	Player[] calculateOrder(Player[] players);

}
