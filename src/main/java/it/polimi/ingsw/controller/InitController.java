package it.polimi.ingsw.controller;
import it.polimi.ingsw.listeners.PlayerListener;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.enumerations.*;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;

import java.util.*;

import javax.swing.event.EventListenerList;

public class InitController implements EventListener {
	private Game game;
	private final int numPlayers;
	private final boolean isExpertGame;
	private final EventListenerList listenerList = new EventListenerList();
	private final GameConstants gameConstants;

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

	public void addEventListener(PlayerListener listener) {
		listenerList.add(PlayerListener.class, listener);
	}

	public void initializeGameComponents() throws EmptyBagException {
		List <Island> islands = new ArrayList<>();
		for(int i=0; i<gameConstants.getNumIslands();i++){
			islands.add(new SingleIsland());
		}
		School[] schools = new School[numPlayers]; //?
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
		for(int j = 0; j < numPlayers;j++){
			School school = game.getPlayers().get(j).getSchool();
			if(school != null && school.getTowerType() == tower){
				throw new TowerTypeAlreadyTakenException();
			}
		}
		player.setSchool(new School(towerPerSchool, tower));
		fireMyEvent(tower, player.getNickName());
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
		//TODO: fire event
		playersWithWizard.put(player.getNickName(), wizard);
	}

	//TODO: we can move this in the setupPlayerTower method
	public void setupSchools() throws EmptyBagException {
		for(int i = 0; i < game.getNumPlayers(); i++){
			int studentPerSchool = gameConstants.getNumStudentsInEntrance();
			for(int j = 0; j < studentPerSchool;j++){
				game.getPlayers().get(i).getSchool().insertEntrance(game.getBag().pickStudent());
			}
		}
	}

	//TODO: before doing this you have always to set the number of players
	public Cloud[] createClouds(){
		Cloud[] clouds = new Cloud[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			clouds[i] = new Cloud(gameConstants.getNumStudentsPerCloud());
		}
		return clouds;
	}

	public Game getGame(){
		return this.game;
	}

	public void fireMyEvent(TowerType type, String playerName){
		for(PlayerListener event : listenerList.getListeners(PlayerListener.class)){
			event.eventPerformed(type, playerName);
		}
	}

	public Map<String, WizardType> getPlayersWithWizard() {
		return playersWithWizard;
	}

	public Map<String, TowerType> getPlayersWithTower() {
		return playersWithTower;
	}

	public void eventPerformed(MessageFromClient message) throws WizardTypeAlreadyTakenException,
			TowerTypeAlreadyTakenException, IllegalArgumentException {
		String messageName = message.getClientMessageHeader().getMessageName();
		switch (messageName) {
			case "TowerChoice" -> {
				TowerType tower = (TowerType) message.getMessagePayload().getAttribute("TowerChosen").getAsObject();
				Player player = game.getPlayerByNickname(message.getClientMessageHeader().getNicknameSender());
				setupPlayerTower(player, tower);
			}
			case "WizardChoice" -> {
				WizardType wizard = (WizardType) message.getMessagePayload().getAttribute("WizardChosen").getAsObject();
				Player player = game.getPlayerByNickname(message.getClientMessageHeader().getNicknameSender());
				setupPlayerWizard(player, wizard);
			}
			default -> throw new IllegalArgumentException();
		}
	}

}
