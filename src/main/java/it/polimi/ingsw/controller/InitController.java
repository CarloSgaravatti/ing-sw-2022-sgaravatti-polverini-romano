package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InitController {
	private Game game;
	private int numPlayers;

	public void setNumPlayers(int numPlayers){
		this.numPlayers=numPlayers;
	}

	public void inizializeGameComponents() throws EmptyBagException {
		List <Island> islands = new ArrayList<>();
		for(int i=0; i<Island.NUM_ISLANDS;i++){
			islands.add(new SingleIsland());
		}
		School[] schools = new School[numPlayers];
		game = new Game(islands,createClouds());
		game.createCharacterCard();
		game.genStudentForBeginning();
		setupIslands();
		game.createAllStudentsForBag();
	}
	//perche devo fare il throws dell'effetto dentro qua ? se non lo faccio mi segna errore
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

	public void setupPlayers(TowerType type, Player player) {
		int towerPerSchool = (numPlayers==3) ? 6 : 8;
		player.setSchool(new School(towerPerSchool,type));
		player.setTowerType(type);
	}
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

}
