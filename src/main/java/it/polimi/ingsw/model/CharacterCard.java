package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Map;

/**
 * Abstract class CharacterCard provides all the behaviour that are common to all characters in the game.
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
	public void putCoin() {
		coinPresent = true;
		coinPrice++;
	}

	/**
	 * Method playCard set the current player that want to play this character (if he has enough coins)
	 * and puts a coin in the character if it isn't present. This method provides a default implementation
	 * that is valid for many character, but not fall all characters (these have to override it).
	 *
	 * @param player the player who wants to play the character
	 * @throws NotEnoughCoinsException if the player doesn't have enough coins to play the character
	 */
	public void playCard(Player player) throws NotEnoughCoinsException {
		try {
			player.removeCoins(this.coinPrice);
		} catch (NotEnoughCoinsException e) {
			throw new NotEnoughCoinsException(id);
		}
		playerActive = player;
		boolean areCoinsUpdated = false;
		if (!coinPresent) {
			areCoinsUpdated = true;
			putCoin();
		}
		firePropertyChange(new PropertyChangeEvent(this.id, "PlayCharacter", areCoinsUpdated, playerActive.getNickName()));
	}

	/**
	 * Method useEffect provides a way on which a player can use the character effect during the turn
	 * after the time he has played the character (only for characters that have some inputs). The specified arguments
	 * represent an encoding of the parameters that a character card needs to perform his action. The CharacterController
	 * will make sure that all the arguments that are passed to the character
	 *
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

	/**
	 * Binds the specified PropertyChangeListener to a character card
	 * @param listener the listener (a CharacterListener)
	 * @see it.polimi.ingsw.listeners.CharacterListener
	 */
	public void addListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * Fire a property change event
	 * @param evt the event that is fired
	 */
	public void firePropertyChange(PropertyChangeEvent evt) {
		listeners.firePropertyChange(evt);
	}

	/**
	 * Returns true if the character requires an input to be played, otherwise false.
	 *
	 * @return true if the character requires an input to be played, otherwise false.
	 */
	public boolean requiresInput() {
		return true;
	}

	/**
	 * Restore the character after the game was restored from persistence data
	 *
	 * @param game the restored game
	 */
	public abstract void restoreCharacter(Game game);

	/**
	 * Restore the active player of the character after the game was restored from persistence data
	 *
	 * @param player the active player of the character
	 */
	public void restoreActivePlayer(Player player) {
		this.playerActive = player;
	}
}
