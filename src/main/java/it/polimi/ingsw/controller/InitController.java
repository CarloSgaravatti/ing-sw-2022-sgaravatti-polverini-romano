package it.polimi.ingsw.controller;
import it.polimi.ingsw.listeners.PlayerListener;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.enumerations.*;

import java.util.*;

import javax.swing.event.EventListenerList;

public class InitController implements EventListener {
	private Game game;
	private int numPlayers;
	private final EventListenerList listenerList = new EventListenerList();

	private final Map<String, WizardType> playersWithWizard = new HashMap<>();
	private final Map<String, TowerType> playersWithTower = new HashMap<>();

	public void setNumPlayers(int numPlayers){
		this.numPlayers = numPlayers;
	}

	public int getNumPlayers(){
		return this.numPlayers;
	}

	public void addEventListener(PlayerListener listener) {
		listenerList.add(PlayerListener.class, listener);
	}

	public void initializeGameComponents() throws EmptyBagException {
		List <Island> islands = new ArrayList<>();
		for(int i=0; i<Island.NUM_ISLANDS;i++){
			islands.add(new SingleIsland());
		}
		School[] schools = new School[numPlayers]; //?
		game = new Game(islands,createClouds());
		game.setNumPlayers(numPlayers);
		game.genStudentForBeginning();
		game.setupIslands();
		game.createAllStudentsForBag();
		game.createCharacterCard();
		//setupSchools(); At this time it can't be done (players don't have schools)
	}

	public void addPlayer(String nick) {
		game.addPlayer(nick);
	}

	//TODO: two separate methods
	@Deprecated
	public void setupPlayers(TowerType type, Player player, WizardType type2) throws WizardTypeAlreadyTakenException, TowerTypeAlreadyTakenException {
		int towerPerSchool = (numPlayers==3) ? 6 : 8;
		if(player.getSchool() == null) {
			for(int j=0; j< game.getNumPlayers();j++){
				School school = game.getPlayers().get(j).getSchool();
				if(school != null && school.getTowerType() == type){
					throw new TowerTypeAlreadyTakenException();
				}
			}
			player.setSchool(new School(towerPerSchool, type));
			fireMyEvent(type,player.getNickName());
			playersWithTower.put(player.getNickName(), type);
		}
		if(player.getWizardType() == null) {
			for(int i = 0; i < game.getNumPlayers(); i++){
				if(game.getPlayers().get(i).getWizardType() == type2) {
					throw new WizardTypeAlreadyTakenException();
				}
			}
			game.assignDeck(player, type2);
			playersWithWizard.put(player.getNickName(), type2);
		}
	}

	public void setupPlayerTower(Player player, TowerType tower) throws TowerTypeAlreadyTakenException {
		if (player.getSchool() != null) return; //TODO: player has already made the choice (exception?)
		int towerPerSchool = (numPlayers == 3) ? 6 : 8;
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
		for(int i = 0; i< game.getNumPlayers(); i++){
			int studentPerSchool = (numPlayers == 3) ? 9 : 7; //Can be parametric
			for(int j=0; j<studentPerSchool;j++){
				game.getPlayers().get(i).getSchool().insertEntrance(game.getBag().pickStudent());
			}
		}
	}

	//TODO: before doing this you have always to set the number of players
	public Cloud[] createClouds(){
		int studentsPerCloud = (numPlayers == 3) ? 4 : 3;
		Cloud[] clouds = new Cloud[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			clouds[i] = new Cloud(studentsPerCloud);
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

	public void eventPerformed(MessageFromClient message)
			throws WizardTypeAlreadyTakenException, TowerTypeAlreadyTakenException {
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
			//TODO: default (exception?)
		}
	}

}
