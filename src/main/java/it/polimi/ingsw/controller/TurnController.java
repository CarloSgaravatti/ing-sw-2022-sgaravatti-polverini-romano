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
	private PhaseOrder[] phaseOrderStrategy;
	private final Game game;

	public TurnController(Player[] players, Game game) {
		playerOrder = Arrays.copyOf(players, players.length);
		orderCalculated = false;
		currentPhase = RoundPhase.PLANNING;
		phaseOrderStrategy = new PhaseOrder[RoundPhase.values().length];
		phaseOrderStrategy[0] = new PlanningPhaseOrder(game);
		phaseOrderStrategy[1] = new ActionPhaseOrder(game);
		currPhaseOrder = phaseOrderStrategy[0];
		this.game = game;
	}

	private void calculateOrder() {
		playerOrder = currPhaseOrder.calculateOrder(playerOrder);
		activePlayerIndex = 0;
		orderCalculated = true;
	}

	public void changePhase() {
		currentPhase = RoundPhase.values()[(currentPhase.ordinal() + 1) % RoundPhase.values().length];
	}

	public void endTurn() {
		if (activePlayerIndex == playerOrder.length - 1) {
			if (currentPhase == RoundPhase.PLANNING) {
				currPhaseOrder = phaseOrderStrategy[1];
			} else {
				currPhaseOrder = phaseOrderStrategy[0];
			}
			changePhase();
			orderCalculated = false;
			return;
		}
		activePlayerIndex++;
	}

	public Player getActivePlayer() {
		if (!orderCalculated) calculateOrder();
		return playerOrder[activePlayerIndex];
	}

	public Player[] getPlayerOrder() {
		return playerOrder;
	}

	public RoundPhase getCurrentPhase() {
		return currentPhase;
	}

	public Player getPosition(int i) {
		return playerOrder[i];
	}

	public void setNextPlayer(Player player, int i) {
		this.playerOrder[i] = player;
	}
}



