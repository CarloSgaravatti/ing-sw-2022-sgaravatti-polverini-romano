package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyCloudException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
	private boolean started;
	private final List<Player> players;
	private final Bag bag;
	private final List<Island> islands;
	private int numPlayers;
	private int numRound;
	private Cloud[] clouds;
	private int studentsPerCloud;
	private int coinGeneralSupply;
	private final CharacterCard[] characterCards;
	private final static int NUM_STUDENTS = 130;

	public Game(List<Island> islands,Cloud[] clouds){
		numPlayers = 0;
		numRound = 0;
		started = false;
		bag = new Bag();
		coinGeneralSupply = 20;
		characterCards = new CharacterCard[CharacterCard.NUM_CHARACTERS_PER_GAME];
		this.islands = islands;
		players = new ArrayList<>();
		this.clouds = clouds;
	}

	public void start() {

	}

	public void addPlayer(String nickname) {
		if(numPlayers <= 4) {
			numPlayers++;
			coinGeneralSupply--;
			players.add(new Player(nickname));
		}
	}

	public int getNumPlayers(){
		return players.size();
	}

	public boolean isStarted() {
		return started;
	}

	public void moveMotherNature(int movement /*Movement inteso gia la scelta del giocatore di quanto muovere madre natura*/){
		int i=0;
		while(!islands.get(i).isMotherNaturePresent() && i<islands.size()){
			i++;
		}
		islands.get(i).setMotherNaturePresent(false);
		i = (i+movement)%islands.size();
		islands.get(i).setMotherNaturePresent(true);
	}

	public Cloud[] getClouds (){
		return this.clouds;
	}

	public Bag getBag () {
		return bag;
	}

	public List<Player> getPlayers () {
		return players;
	}

	public List<Island> getIslands() {
		return islands;
	}

	public void createCharacterCard(){
		Random rnd = new Random();
		List<Integer> charactersCreated = new ArrayList<>();
		int characterToCreate;
		for(int i = 0; i<CharacterCard.NUM_CHARACTERS_PER_GAME; i++){
			do{
				characterToCreate = rnd.nextInt(CharacterCard.NUM_CHARACTERS);
			}while(charactersCreated.contains(characterToCreate));
			charactersCreated.add(characterToCreate);
			characterCards[i] = CharacterCreator.getCharacter(characterToCreate+1);
		}
	}
}
