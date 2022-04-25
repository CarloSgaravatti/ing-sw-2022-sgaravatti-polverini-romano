package it.polimi.ingsw.controller;
import it.polimi.ingsw.listeners.PlayerListener;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.enumerations.*;
import org.w3c.dom.events.EventListener;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InitController implements EventListener {
	private Game game;
	private int numPlayers;
	private EventListenerList listenerList = new EventListenerList();

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
		game.setupIslands(); //TODO: put this method in the game class
		game.createAllStudentsForBag();
		game.createCharacterCard();
		setupSchools();
	}
	@Deprecated
	public void setupIslands() throws EmptyBagException {
		Random rnd = new Random();
		int indexOfMatherNature = rnd.nextInt(Island.NUM_ISLANDS);
		game.getIslands().get(indexOfMatherNature).setMotherNaturePresent(true);
		for(int i=0; i<Island.NUM_ISLANDS;i++){
			if(i != (indexOfMatherNature+(Island.NUM_ISLANDS/2))%Island.NUM_ISLANDS  && i != indexOfMatherNature){
				game.getIslands().get(i).addStudent(game.getBag().pickStudent());
			}
		}
	}

	public void addPlayer(String nick) {
		game.addPlayer(nick);
	}

	public void setupPlayers(TowerType type, Player player, WizardType type2) throws WizardTypeAlreadyTakenException, TowerTypeAlreadyTakenException {
		int towerPerSchool = (numPlayers==3) ? 6 : 8;
		if(player.getSchool().getTowerType() == null) {
			for(int j=0; j< game.getNumPlayers();j++){
				if(game.getPlayers().get(j).getSchool().getTowerType() == type){
					throw new TowerTypeAlreadyTakenException();
				}
			}
			player.setSchool(new School(towerPerSchool, type));
			fireMyEvent(type,player.getNickName());
		}
		if(player.getWizardType() == null) {
			for(int i = 0; i < game.getNumPlayers(); i++){
				if(game.getPlayers().get(i).getWizardType() == type2) {
					throw new WizardTypeAlreadyTakenException();
				}
			}
			game.assignDeck(player, type2);
		}
	}
	public void setupSchools() throws EmptyBagException {
		for(int i = 0; i< game.getNumPlayers(); i++){
			int studentPerSchool = (numPlayers==3) ? 9 : 7;
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
			event.eventPerformed(type,playerName);
		}
	}

	//TODO: handle player setup message
	public void eventPerformed(MessageFromClient message) {

	}

}
