package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.Arrays;

public class TurnController {
	private RoundPhase currentPhase;
	private int numPlayersDone;
	private Player[] playerOrder;
	private boolean orderCalculated;
	private int activePlayerIndex;
	private boolean turnActive;
	private PhaseOrder currPhaseOrder;
	private final PhaseOrder[] phaseOrderStrategy;

	public TurnController(Player[] players, Game game) {
		playerOrder = Arrays.copyOf(players, players.length);
		orderCalculated = false;
		currentPhase = RoundPhase.PLANNING;
		phaseOrderStrategy = new PhaseOrder[RoundPhase.values().length];
		phaseOrderStrategy[0] = new PlanningPhaseOrder(game);
		phaseOrderStrategy[1] = new ActionPhaseOrder();
		currPhaseOrder = phaseOrderStrategy[0];
	}

	private void calculateOrder() {
		playerOrder = currPhaseOrder.calculateOrder(playerOrder);
		activePlayerIndex = 0;
		orderCalculated = true;
	}

	public void changePhase() {
		currentPhase = RoundPhase.values()[(currentPhase.ordinal() + 1) % RoundPhase.values().length];
	}

	public boolean endTurn() {
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

	public Player getActivePlayer() {
		if (!orderCalculated) calculateOrder();
		return playerOrder[activePlayerIndex];
	}

	//Don't know if it is useful
	/*public Player[] getPlayerOrder() {
		return playerOrder;
	}*/

	public RoundPhase getCurrentPhase() {
		return currentPhase;
	}
}



