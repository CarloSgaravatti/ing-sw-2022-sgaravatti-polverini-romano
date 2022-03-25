package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class InitController {
	private Game game;
	private int numPlayers;

	public void setNumPlayers(int numPlayers){
		this.numPlayers=numPlayers;
	}

	public void inizializeGameComponents() {
		List <Island> islands = new ArrayList<>();
		for(int i=0; i<Island.NUM_ISLANDS;i++){
			islands.add(new SingleIsland());
		}
		School[] schools = new School[numPlayers];
		game = new Game(islands,createClouds());
		game.createCharacterCard();
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
