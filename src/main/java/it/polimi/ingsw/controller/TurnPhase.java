package it.polimi.ingsw.controller;

/**
 * Enum Class TurnPhase contains all possible states of a round.
 */
public enum TurnPhase {
	FILL_CLOUDS("", ""),
	PLAY_ASSISTANT("Play an assistant card from your deck", "PlayAssistant"),
	MOVE_STUDENTS("Move students from the entrance of your school", "MoveStudents"),
	MOVE_MOTHER_NATURE("Move mother nature", "MoveMotherNature"), //can be better
	SELECT_CLOUD("Select a cloud to pick students from it", "PickFromCloud"),
	PLAY_CHARACTER_CARD("Select a character card to play", "PlayCharacter"),
	TURN_ENDED("", "");

	private final String actionDescription;
	private final String actionCommand;

	TurnPhase(String actionDescription, String actionCommand) {
		this.actionDescription = actionDescription;
		this.actionCommand = actionCommand;
	}

	public String getActionDescription() {
		return actionDescription;
	}

	public String getActionCommand() {
		return actionCommand;
	}
}
