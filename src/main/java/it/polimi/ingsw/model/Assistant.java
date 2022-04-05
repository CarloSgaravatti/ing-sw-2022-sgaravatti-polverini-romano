package it.polimi.ingsw.model;

/**
 * Class Assistant represent an assistant card that is assigned to a player at
 * the beginning of the game.
 */
public class Assistant {
	private WizardType wizard;//Is this useful? If yes, it has to be final
	private final int cardValue;
	private final int motherNatureMovement;
	private boolean played;//Is it useful?
	public static final int NUM_ASSISTANTS = 10;

	/**
	 * Creates an assistant with the specified card value and mother nature movement
	 * @param cardValue the value of the card
	 * @param motherNatureMovement maximum mother nature movement
	 */
	public Assistant(int cardValue, int motherNatureMovement, WizardType wizard){
		this.cardValue = cardValue;
		this.motherNatureMovement = motherNatureMovement;
		played = false;
		this.wizard = wizard;
	}

	/**
	 * Method getCardValue returns the value of the assistant card that is used to
	 * determine the order of play during a round when the assistant is played
	 * @return the value of the card
	 */
	public int getCardValue() {
		return cardValue;
	}

	/**
	 * Method getMotherNatureValue returns the maximum movement of mother nature that
	 * a player can perform after he plays the card
	 * @return the maximum mother nature movement
	 */
	public int getMotherNatureMovement() {
		return motherNatureMovement;
	}

	public boolean getPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}
}
