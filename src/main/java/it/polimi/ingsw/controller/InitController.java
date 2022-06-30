package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.enumerations.*;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.*;

/**
 * Class InitController is used for initialize the game and all its components
 */
public class InitController implements PropertyChangeListener {
	private transient Game game;
	private final int numPlayers;
	private final boolean isExpertGame;
	private final GameConstants gameConstants;
	private transient final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	private final Map<String, WizardType> playersWithWizard = new HashMap<>();
	private final Map<String, TowerType> playersWithTower = new HashMap<>();

	/**
	 * Construct a InitController that will initialize the game by number of player and rules for the game
	 *
	 * @param numPlayers number of players of the game
	 * @param isExpertGame boolean value for rules of the game
	 */
	public InitController(int numPlayers, boolean isExpertGame) {
		this.numPlayers = numPlayers;
		this.isExpertGame = isExpertGame;
		this.gameConstants = JsonUtils.constantsByNumPlayer(numPlayers);
	}

	/**
	 * Construct a InitController that will initialize a restored game with value stored in a json file
	 * created every time a player make a move
	 *
	 * @param gameRestored game restored with value contained in json file
	 */
	public InitController(Game gameRestored) {
		this(gameRestored.getNumPlayers(), gameRestored.isExpertGame());
		this.game = gameRestored;
		game.getPlayers().stream().filter(player -> player.getWizardType() != null)
				.forEach(player -> playersWithWizard.put(player.getNickName(), player.getWizardType()));
		game.getPlayers().stream().filter(player -> player.getSchool() != null)
				.forEach(player -> playersWithTower.put(player.getNickName(), player.getSchool().getTowerType()));
	}

	/**
	 * Returns number of player of the game
	 *
	 * @return number of player of the game
	 */
	public int getNumPlayers(){
		return this.numPlayers;
	}

	/**
	 * Add a property change listener in this class, that will listen this class on specify propertyName
	 *
	 * @param propertyName name of the property to listen
	 * @param listener the listener that listen the class
	 */
	public void addListener(String propertyName, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Method that initialize all components of a new game
	 *
	 * @throws EmptyBagException if the game tried to get a student from the bag but the bag is empty
	 */
	public void initializeGameComponents() throws EmptyBagException {
		List <Island> islands = new ArrayList<>();
		for(int i=0; i < gameConstants.getNumIslands();i++){
			islands.add(new SingleIsland());
		}
		game = new Game(islands, createClouds(), gameConstants, isExpertGame);
		game.setNumPlayers(numPlayers);
		game.genStudentForBeginning();
		game.setupIslands();
		game.createAllStudentsForBag();
		if (isExpertGame) game.createCharacterCards();
	}

	/**
	 * Add a player to the game by the player's name
	 *
	 * @param nick player nick name
	 */
	public void addPlayer(String nick) {
		game.addPlayer(nick);
	}

	/**
	 * Sets a tower type to a player if the type isn't already taken
	 *
	 * @param player player to set the tower type
	 * @param tower type of the tower to set
	 * @throws TowerTypeAlreadyTakenException if the type of tower chosen is already taken by another player
	 */
	public void setupPlayerTower(Player player, TowerType tower) throws TowerTypeAlreadyTakenException {
		if (player.getSchool() != null) return;
		int towerPerSchool = gameConstants.getNumTowers();
		for(int j = 0; j < numPlayers; j++){
			School school = game.getPlayers().get(j).getSchool();
			if(school != null && school.getTowerType() == tower){
				throw new TowerTypeAlreadyTakenException();
			}
		}
		player.setSchool(new School(towerPerSchool, tower, gameConstants, player));
		School school = player.getSchool();
		school.addObserver(game);
		int studentPerSchool = gameConstants.getNumStudentsInEntrance();
		for(int j = 0; j < studentPerSchool;j++){
			try {
				school.insertEntrance(game.getBag().pickStudent());
			} catch (EmptyBagException e) {
				game.setLastRound(true);
			}
		}
		listeners.firePropertyChange("Tower", tower, player.getNickName());
		playersWithTower.put(player.getNickName(), tower);
	}

	/**
	 * Sets a wizard type to a player if the type isn't already taken
	 *
	 * @param player player to set the wizard
	 * @param wizard type of wizard to set
	 * @throws WizardTypeAlreadyTakenException if the type of wizard chosen is already taken by another player
	 */
	public void setupPlayerWizard(Player player, WizardType wizard) throws WizardTypeAlreadyTakenException {
		if (player.getWizardType() != null) return;
		for(int i = 0; i < numPlayers; i++){
			if(game.getPlayers().get(i).getWizardType() == wizard) {
				throw new WizardTypeAlreadyTakenException();
			}
		}
		game.assignDeck(player, wizard);
		listeners.firePropertyChange("Wizard", wizard, player.getNickName());
		playersWithWizard.put(player.getNickName(), wizard);
	}

	/**
	 * Create clouds by number of players
	 *
	 * @return created clouds
	 */
	private Cloud[] createClouds(){
		Cloud[] clouds = new Cloud[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			clouds[i] = new Cloud(gameConstants.getNumStudentsPerCloud());
		}
		return clouds;
	}

	/**
	 * Return the initialized game
	 *
	 * @return the initialized game
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * Return players that have already chosen a wizard type
	 *
	 * @return players that have already chosen a wizard type
	 */
	public Map<String, WizardType> getPlayersWithWizard() {
		return playersWithWizard;
	}

	/**
	 * Return players that have already chosen a tower type
	 *
	 * @return players that have already chosen a tower type
	 */
	public Map<String, TowerType> getPlayersWithTower() {
		return playersWithTower;
	}

	/**
	 * This method receives an event from the View that contains a message with in it a chose of setup
	 *
	 * @param evt A PropertyChangeEvent object describing the event source
	 *               and the property that has changed.
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		MessageFromClient message = (MessageFromClient) evt.getNewValue();
		String messageName = message.getClientMessageHeader().getMessageName();
		String nicknameSender = message.getClientMessageHeader().getNicknameSender();
		switch (messageName) {
			case "TowerChoice" -> {
				TowerType tower = (TowerType) message.getMessagePayload().getAttribute("Tower").getAsObject();
				Player player = game.getPlayerByNickname(nicknameSender);
				try {
					setupPlayerTower(player, tower);
				} catch (TowerTypeAlreadyTakenException e) {
					listeners.firePropertyChange(new PropertyChangeEvent(nicknameSender, "Error",
							ErrorMessageType.TOWER_ALREADY_TAKEN, e.getMessage()));
					return;
				}
			}
			case "WizardChoice" -> {
				WizardType wizard = (WizardType) message.getMessagePayload().getAttribute("Wizard").getAsObject();
				Player player = game.getPlayerByNickname(nicknameSender);
				try {
					setupPlayerWizard(player, wizard);
				} catch (WizardTypeAlreadyTakenException e) {
					listeners.firePropertyChange(new PropertyChangeEvent(nicknameSender, "Error",
							ErrorMessageType.WIZARD_ALREADY_TAKEN, e.getMessage()));
					return;
				}
			}
			default -> {
				listeners.firePropertyChange(new PropertyChangeEvent(nicknameSender, "Error",
						ErrorMessageType.UNRECOGNIZED_MESSAGE, "Your message was not recognized."));
				return;
			}
		}
		listeners.firePropertyChange("Setup", nicknameSender, messageName);
	}
}
