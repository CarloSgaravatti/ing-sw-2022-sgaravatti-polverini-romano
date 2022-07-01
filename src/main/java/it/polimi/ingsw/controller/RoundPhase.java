package it.polimi.ingsw.controller;

import java.util.List;

/**
 * Enum class RoundPhase contains all possible phases of the round, each phase has a list of actions that can be done
 * during it.
 *
 * @see TurnPhase
 */
public enum RoundPhase {
	PLANNING(List.of(TurnPhase.PLAY_ASSISTANT)),
	ACTION(List.of(TurnPhase.MOVE_STUDENTS,
			TurnPhase.MOVE_MOTHER_NATURE,
			TurnPhase.SELECT_CLOUD,
			TurnPhase.PLAY_CHARACTER_CARD));

	private final List<TurnPhase> turnActions;

	RoundPhase(List<TurnPhase> phases) {
		this.turnActions = phases;
	}

	public List<TurnPhase> getTurnActions() {
		return turnActions;
	}
}
