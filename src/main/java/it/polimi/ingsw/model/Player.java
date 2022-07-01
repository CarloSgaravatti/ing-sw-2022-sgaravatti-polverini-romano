package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.model.gameConstants.GameConstants;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Player contains the information of a player in the game
 *
 */
public class Player implements PropertyChangeListener {
	private int numCoins;
	private String nickName;
	private School school;
	private List<Assistant> assistants;
	private final TurnEffect turnEffect;
	private WizardType wizardType;
	private final transient PropertyChangeSupport listeners;

	/**
	 * constructor that initializes the player with the nickname
	 *
	 * @param nickName nickname of the player
	 */

	public Player(String nickName) {
		this();
		this.nickName = nickName;
	}

	/**
	 * constructor that initializes the player with the turnEffect, list of assistant, number of coins and links the listeners
	 *
	 */

	public Player() {
		turnEffect = new TurnEffect(this);
		assistants = new ArrayList<>();
		numCoins = 1;
		listeners = new PropertyChangeSupport(this);
	}

	/**
	 * method addListener links the listener to the player
	 *
	 * @param listener listener linked
	 */

	public void addListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * method pickFromCloud allows the player to pick students form the cloud chosen
	 *
	 * @param cloud cloud chosen
	 * @throws EmptyCloudException exception thrown if the cloud chosen is empty
	 */

	public void pickFromCloud(Cloud cloud) throws EmptyCloudException{
		school.insertEntrance(cloud.pickStudents());
	}

	/**
	 * method setSchool links the school to the player
	 *
	 * @param s school linked to the player
	 */

	public void setSchool(School s){
		this.school = s;
	}

	/**
	 * method getSchool gets the school of the player
	 *
	 * @return returns the school of the player
	 */

	public School getSchool() {
		return school;
	}

	/**
	 * method playAssistant allows the player to pick and play an assistant
	 *
	 * @param assistant assistant chosen
	 * @param assistantPlayed assistant already played
	 * @return returns boolean true or false if the assistant chosen can be played or not
	 * @throws NoSuchAssistantException thrown if the assistant chosen doesn't exist
	 */

	public boolean playAssistant(int assistant, List<Integer> assistantPlayed) throws NoSuchAssistantException {
		boolean isFirstPlayedAssistant = true;
		if (assistantPlayed.contains(assistant)) {
			isFirstPlayedAssistant = false;
			for (Assistant a: assistants) {
				if (!assistantPlayed.contains(a.getCardValue())) return false;
			}
		}
		for (int i = 0; i < assistants.size(); i++) {
			if (assistants.get(i).getCardValue() == assistant) {
				turnEffect.setOrderPrecedence(assistant);
				turnEffect.incrementMotherNatureMovement(assistants.get(i).getMotherNatureMovement(), false);
				turnEffect.setFirstPlayedAssistant(isFirstPlayedAssistant);
				Assistant removed = assistants.remove(i);
				listeners.firePropertyChange(
						new PropertyChangeEvent(nickName, "Assistant", null, removed)
				);
				return true;
			}
		}
		throw new NoSuchAssistantException(assistant);
	}

	/**
	 * method setAssistants links the assistant to the player
	 *
	 * @param assistants list of assistant present in the game
	 */

	public void setAssistants(List<Assistant> assistants){
		this.assistants = assistants;
	}

	/**
	 * method getNumCoins gets the number of coins of the player
	 *
	 * @return returns the number of coins of the player
	 */

	public int getNumCoins() {
		return numCoins;
	}

	/**
	 * method getAssistant gets the assistant linked to the player
	 *
	 * @return returns the assistant linked to the player
	 */

	public List<Assistant> getAssistants() {
		return assistants;
	}

	/**
	 * method getNickname gets the player's nickname
	 *
	 * @return returns the player's nickname
	 */

	public String getNickName() {
		return nickName;
	}

	/**
	 * method getTurnEffect gets the turn effect of the player
	 *
	 * @return returns the turn effect of the player
	 */

	public TurnEffect getTurnEffect() {
		return turnEffect;
	}

	/**
	 * method resetTurnEffect resets the turn effect of the player
	 *
	 */

	public void resetTurnEffect() {
		turnEffect.reset();
	}

	/**
	 * method insertCoin adds a coins to the number of player's coins
	 *
	 */

	public void insertCoin () {
		int oldCoins = numCoins;
		numCoins++;
		listeners.firePropertyChange(new PropertyChangeEvent(nickName, "Coins", oldCoins, numCoins));
	}

	/**
	 * method removeCoins removes coins from the number of player's coins
	 *
	 * @param coins number of coins removed
	 * @throws NotEnoughCoinsException thrown when the number of coins requested to be removed are more than the number of player's coins
	 */

	public void removeCoins(int coins) throws NotEnoughCoinsException {
		if (numCoins < coins) throw new NotEnoughCoinsException();
		int oldCoins = numCoins;
		numCoins -= coins;
		listeners.firePropertyChange(new PropertyChangeEvent(nickName, "Coins", oldCoins, numCoins));
	}

	/**
	 * method setWizardType links the type of wizard chosen to the player
	 *
	 * @param wizardType type of the wizard chosen
	 */

	public void setWizardType(WizardType wizardType) {
		this.wizardType = wizardType;
	}

	/**
	 * method getWizardType gets the type of wizard linked to the player
	 *
	 * @return returns the type of wizard linked to the player
	 */

	public WizardType getWizardType() {
		return this.wizardType;
	}

	/**
	 * method equals verify if the object is the same of the player
	 *
	 * @param o object to be verified
	 * @return returns true or false if the object is the same of the player or not
	 */

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player player)) return false;
		return Objects.equals(nickName, player.nickName);
	}

	/**
	 * method hashCode returns the player's nickname in hash cose
	 *
	 * @return returns the player's nickname in hash code
	 */

	@Override
	public int hashCode() {
		return Objects.hash(nickName);
	}

	/**
	 * method propertyChange takes an event relating to the player and pass it to the player's listener
	 *
	 * @param evt event passed
	 */

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		listeners.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

	/**
	 * method restorePlayer restores the school and the turn effect linked to the player
	 *
	 * @param game game where the player's school and the player's turn effect have to be restored
	 * @param gameConstants constants of the game involved
	 */

	public void restorePlayer(Game game, GameConstants gameConstants) {
		if (school != null) { //if school is null, there is nothing to restore
			school.restoreSchool(this, game, gameConstants);
		}
		turnEffect.restoreTurnEffect(this);
	}
}
