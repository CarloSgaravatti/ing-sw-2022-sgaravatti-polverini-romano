package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.Arrays;

/**
 * Class TurnController is used to maintain the order of play during the turn. The order is calculated using the Strategy
 * pattern between round phase (action phase and planning phase).
 * @see PhaseOrder
 */
public class TurnController {
	private RoundPhase currentPhase;
	private Player[] playerOrder;
	private boolean orderCalculated;
	private int activePlayerIndex;
	private PhaseOrder currPhaseOrder;
	private final PhaseOrder[] phaseOrderStrategy;

	/**
	 * Construct a TurnController that will save the order of play of the specified players. The specified game is used
	 * to construct PhaseOrder instances
	 * @param players the players of the game (in clockwise order)
	 * @param game the game of the players
	 */
	public TurnController(Player[] players, Game game) {
		playerOrder = Arrays.copyOf(players, players.length);
		orderCalculated = false;
		currentPhase = RoundPhase.PLANNING;
		phaseOrderStrategy = new PhaseOrder[RoundPhase.values().length];
		phaseOrderStrategy[0] = new PlanningPhaseOrder(game);
		phaseOrderStrategy[1] = new ActionPhaseOrder();
		currPhaseOrder = phaseOrderStrategy[0];
	}

	/**
	 * Calculate the order of play in the new RoundPhase using the correct PhaseOrder way of calculating it
	 */
	private void calculateOrder() {
		playerOrder = currPhaseOrder.calculateOrder(playerOrder);
		activePlayerIndex = 0;
		orderCalculated = true;
	}

	/**
	 * Switch the current RoundPhase: if the previous one was 'planning phase' the new one is 'action phase' and
	 * vice-versa
	 */
	public void changePhase() {
		currentPhase = RoundPhase.values()[(currentPhase.ordinal() + 1) % RoundPhase.values().length];
	}

	/**
	 * Do all operations that need to be done at the end of a player turn (changing the active player and eventually
	 * change the current RoundPhase)
	 * @return true if the current phase is finished, otherwise false
	 */
	public boolean endTurn() {
		if (currentPhase == RoundPhase.ACTION) {
			playerOrder[activePlayerIndex].resetTurnEffect();
		}
		if (activePlayerIndex == playerOrder.length - 1) {
			if (currentPhase == RoundPhase.PLANNING) {
				currPhaseOrder = phaseOrderStrategy[1];
			} else {
				currPhaseOrder = phaseOrderStrategy[0];
			}
			changePhase();
			orderCalculated = false;
			return true;
		}
		activePlayerIndex++;
		return false;
	}

	/**
	 * Return the current active player of the RoundPhase
	 * @return the active player
	 */
	public Player getActivePlayer() {
		if (!orderCalculated) calculateOrder();
		return playerOrder[activePlayerIndex];
	}

	/**
	 * Return the current RoundPhase of the round
	 * @return the current phase
	 */
	public RoundPhase getCurrentPhase() {
		return currentPhase;
	}

	/**
	 * Returns true if the order is calculated, otherwise false
	 * @return true if the order is calculated, otherwise false
	 */
	public boolean isOrderCalculated() {
		return orderCalculated;
	}

	/**
	 * Returns the current RoundPhase order of play
	 * @return the order of play
	 */
	public Player[] getPlayerOrder() {
		return playerOrder;
	}
}



