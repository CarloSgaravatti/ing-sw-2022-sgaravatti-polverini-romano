package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;

/**
 * Abstract class CharacterCard provides all the behaviour that are common to all characters in the game.
 * ... TODO
 */
public abstract class CharacterCard {
	private final int id;
	private int coinPrice;
	private boolean coinPresent;
	private transient Player playerActive;
	private final transient PropertyChangeSupport listeners;

	/**
	 * Construct a CharacterCard which has the specified id and the specified price
	 * @param coinPrice character price
	 * @param id character id
	 */
	public CharacterCard (int coinPrice, int id){
		this.coinPrice = coinPrice;
		this.id = id;
		coinPresent = false;
		listeners = new PropertyChangeSupport(this);
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
		boolean areCoinsUpdated = false;
		if (!coinPresent) {
			areCoinsUpdated = true;
			putCoin();
		}
		firePropertyChange(new PropertyChangeEvent(this.id, "PlayCharacter", areCoinsUpdated, playerActive.getNickName()));
	}

	//TODO: all the IllegalActionRequestedException must have a message

	/**
	 * Method useEffect provides a way on which a player can use the character effect during the turn
	 * after the time he has played the character (only for characters that have some inputs). The specified arguments
	 * represent an encoding of the parameters that a character card needs to perform his action.
	 * @param arguments the character parameters
	 * @throws IllegalCharacterActionRequestedException if the parameters are not correct, or if the character does not
	 * provide any effect that can be used after the player plays the card
	 */
	public abstract void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException;

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

	public void addListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void firePropertyChange(PropertyChangeEvent evt) {
		listeners.firePropertyChange(evt);
	}

	//TODO: after characters re implementation, make this abstract
	public boolean requiresInput() {
		return true;
	}
}
