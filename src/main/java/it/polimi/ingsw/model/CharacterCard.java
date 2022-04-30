package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;

import java.util.List;

//TODO: all additional methods in subclasses has to be private (after change in tests)

/**
 * Abstract class CharacterCard provides all the behaviour that are common to all characters in the game.
 * ... TODO
 */
public abstract class CharacterCard {
	private final int id;
	private int coinPrice;
	private boolean coinPresent;
	private Player playerActive;
	public static final int NUM_CHARACTERS = 12;

	/**
	 * Construct a CharacterCard which has the specified id and the specified price
	 * @param coinPrice character price
	 * @param id character id
	 */
	public CharacterCard (int coinPrice, int id){
		this.coinPrice = coinPrice;
		this.id = id;
		coinPresent = false;
	}

	/**
	 * Returns the coin price that a player have to pay in order to play the character
	 * @return the character coin price
	 */
	public int getPrice(){
		return coinPrice;
	}

	/**
	 * Checks if someone has already played this character during the game (the first player that plays
	 * the character have to put a coin on the character, so the coin price is incremented by one)
	 * @return true if the coin is present, otherwise false
	 */
	public boolean isCoinPresent() {
		return coinPresent;
	}

	/**
	 * Puts a coin in the character card, this can happen only the first time the character is played
	 */
	private void putCoin() {
		coinPresent = true;
		coinPrice++;
	}

	/**
	 * Method playCard set the current player that want to play this character (if he has enough coins)
	 * and puts a coin in the character if it isn't present. This method provides a default implementation
	 * that is valid for many character, but not fall all characters (these have to override it).
	 * @param player the player who wants to play the character
	 * @throws NotEnoughCoinsException if the player doesn't have enough coins to play the character
	 */
	public void playCard(Player player) throws NotEnoughCoinsException {
		player.removeCoins(this.coinPrice);
		playerActive = player;
		if (!coinPresent) {
			putCoin();
		}
	}

	//TODO: all the IllegalActionRequestedException must have a message

	/**
	 * Method useEffect provides a way on which a player can use the character effect during the turn
	 * after the time he has played the character (playing a character and using the effect are two operations
	 * that can be done in separated times). This is a default implementation that is valid all for a few character
	 * (in particular characters 2,4,6 and 8) and it throws all the time the exception. The specified arguments
	 * represent an encoding of the parameters that a character card needs to perform his action.
	 * @param args the encoding of the character parameters
	 * @throws IllegalCharacterActionRequestedException if the parameters are not correct, or if the character does not
	 * provide any effect that can be used after the player plays the card
	 */
	public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
		throw new IllegalCharacterActionRequestedException();
	}

	/**
	 * Returns the last player that has played this character (even if he has already terminated his turn)
	 * @return the last player that has played the character
	 */
	public Player getPlayerActive() {
		return playerActive;
	}

	/**
	 * Returns the character card id
	 * @return the character card id
	 */
	public int getId() {
		return id;
	}
}
