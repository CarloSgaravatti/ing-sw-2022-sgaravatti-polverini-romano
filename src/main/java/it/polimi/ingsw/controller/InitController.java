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
import java.util.*;

public class InitController implements PropertyChangeListener {
	private Game game;
	private final int numPlayers;
	private final boolean isExpertGame;
	private final GameConstants gameConstants;
	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	private final Map<String, WizardType> playersWithWizard = new HashMap<>();
	private final Map<String, TowerType> playersWithTower = new HashMap<>();

	public InitController(int numPlayers, boolean isExpertGame) {
		this.numPlayers = numPlayers;
		this.isExpertGame = isExpertGame;
		this.gameConstants = JsonUtils.constantsByNumPlayer(numPlayers);
	}

	public int getNumPlayers(){
		return this.numPlayers;
	}

	public void addListener(String propertyName, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void initializeGameComponents() throws EmptyBagException {
		List <Island> islands = new ArrayList<>();
		for(int i=0; i < gameConstants.getNumIslands();i++){
			islands.add(new SingleIsland());
		}
		game = new Game(islands,createClouds(),gameConstants);
		game.setNumPlayers(numPlayers);
		game.genStudentForBeginning();
		game.setupIslands();
		game.createAllStudentsForBag();
		if (isExpertGame) game.createCharacterCards();
		//setupSchools(); At this time it can't be done (players don't have schools)
	}

	public void addPlayer(String nick) {
		game.addPlayer(nick);
	}

	public void setupPlayerTower(Player player, TowerType tower) throws TowerTypeAlreadyTakenException {
		if (player.getSchool() != null) return; //TODO: player has already made the choice (exception?)
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

	public void setupPlayerWizard(Player player, WizardType wizard) throws WizardTypeAlreadyTakenException {
		if (player.getWizardType() != null) return; //TODO: player has already made the choice (exception?)
		for(int i = 0; i < numPlayers; i++){
			if(game.getPlayers().get(i).getWizardType() == wizard) {
				throw new WizardTypeAlreadyTakenException();
			}
		}
		game.assignDeck(player, wizard);
		listeners.firePropertyChange("Wizard", wizard, player.getNickName());
		playersWithWizard.put(player.getNickName(), wizard);
	}

	@Deprecated
	public void setupSchools() throws EmptyBagException {
		for(int i = 0; i < game.getNumPlayers(); i++){
			School school = game.getPlayers().get(i).getSchool();
			school.addObserver(game);
			int studentPerSchool = gameConstants.getNumStudentsInEntrance();
			for(int j = 0; j < studentPerSchool;j++){
				school.insertEntrance(game.getBag().pickStudent());
			}
		}
	}

	private Cloud[] createClouds(){
		Cloud[] clouds = new Cloud[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			clouds[i] = new Cloud(gameConstants.getNumStudentsPerCloud());
		}
		return clouds;
	}

	public Game getGame() {
		return this.game;
	}

	public Map<String, WizardType> getPlayersWithWizard() {
		return playersWithWizard;
	}

	public Map<String, TowerType> getPlayersWithTower() {
		return playersWithTower;
	}

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
					listeners.firePropertyChange("Error", ErrorMessageType.TOWER_ALREADY_TAKEN, nicknameSender);
					return;
				}
			}
			case "WizardChoice" -> {
				WizardType wizard = (WizardType) message.getMessagePayload().getAttribute("Wizard").getAsObject();
				Player player = game.getPlayerByNickname(nicknameSender);
				try {
					setupPlayerWizard(player, wizard);
				} catch (WizardTypeAlreadyTakenException e) {
					listeners.firePropertyChange("Error", ErrorMessageType.WIZARD_ALREADY_TAKEN, nicknameSender);
					return;
				}
			}
			default -> {
				listeners.firePropertyChange("Error", ErrorMessageType.UNRECOGNIZE_MESSAGE, nicknameSender);
				return;
			}
		}
		listeners.firePropertyChange("Setup", nicknameSender, messageName);
	}
}
