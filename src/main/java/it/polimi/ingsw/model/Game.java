package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyCloudException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
	private boolean started;
	private final List<Player> players;
	private Round currRound;
	private final Bag bag;
	private final Island[] islands;
	private int numPlayers;
	private int numRound;
	private Cloud[] clouds;
	private int studentsPerCloud;
	private int coinGeneralSupply;
	private final CharacterCard[] characterCards;

	public Game(){
		numPlayers = 0;
		numRound = 0;
		started = false;
		bag = new Bag();
		coinGeneralSupply = 20;
		characterCards = new CharacterCard[CharacterCard.NUM_CHARACTERS_PER_GAME];
		islands = new Island[Island.NUM_ISLANDS];
		players = new ArrayList<>();
	}

	public void start() throws EmptyBagException, EmptyCloudException {
		//maybe it is not necessary the if instruction, because someone calls start()
		//only when there is a correct number of players
		if(numPlayers >=2 && numPlayers <=4) {
			started = true;
			doPreparation();
			studentsPerCloud = (numPlayers == 3) ? 4 : 3;
		}
		//At the moment, to test you have to comment these lines
		while(!isFinished()) {
			Round currRound = newRound();
			currRound.planningPhase();
			currRound.actionPhase();
		}
	}

	public void addPlayer(String nickname) {
		numPlayers++;
		coinGeneralSupply--;
		players.add(new Player(nickname));
	}

	public int getNumPlayers(){
		return players.size();
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isFinished() {
		return false;
	}

	public Round newRound() {
		Round round = new Round(numRound, this);
		numRound++;
		return round;
	}

	public void doPreparation() throws EmptyBagException{
		int i;
		//insert students in bag
		int studentsPerType = Student.NUM_STUDENTS / RealmType.values().length;//=26
		for(i = 0; i < Student.NUM_STUDENTS; i++) {
			bag.insertStudent(new Student(RealmType.values()[i / studentsPerType]));
		}

		//TODO: someone have to save these professor
		Professor[] professors = new Professor[Professor.NUM_PROFESSORS];
		professors[RealmType.YELLOW_GNOMES.ordinal()] = new Professor(RealmType.YELLOW_GNOMES);
		professors[RealmType.BLUE_UNICORNS.ordinal()] = new Professor(RealmType.BLUE_UNICORNS);
		professors[RealmType.GREEN_FROGS.ordinal()] = new Professor(RealmType.GREEN_FROGS);
		professors[RealmType.RED_DRAGONS.ordinal()] = new Professor(RealmType.RED_DRAGONS);
		professors[RealmType.PINK_FAIRES.ordinal()] = new Professor(RealmType.PINK_FAIRES);

		//islands = new Island[Island.NUM_ISLANDS];
		for(i = 0;i < Island.NUM_ISLANDS; i++){
			islands[i] = new SingleIsland();
		}
		//put mother nature and students on islands
		Random rnd = new Random();
		int islandMotherNature = rnd.nextInt(Island.NUM_ISLANDS);
		islands[islandMotherNature].setMotherNaturePresent(true);
		int islandNoStudent = (islandMotherNature + 6) % Island.NUM_ISLANDS;
		for(i = 0;i < Island.NUM_ISLANDS; i++) {
			if(i != islandMotherNature && i != islandNoStudent)
				islands[i].addStudent(bag.pickStudent());
		}

		//creation of schools and set for each player
		for(i = 0; i < numPlayers; i++){
			if(numPlayers == 2 || numPlayers == 4) {
				if(i == 0)
					players.get(i).setSchool(new School(8, TowerType.WHITE));
				if(i == 1)
					players.get(i).setSchool(new School(8, TowerType.BLACK));
			}
			if(numPlayers == 3) {
				if(i == 0)
					players.get(i).setSchool(new School(6, TowerType.WHITE));
				if(i == 1)
					players.get(i).setSchool(new School(6, TowerType.BLACK));
				if(i == 2)
					players.get(i).setSchool(new School(6, TowerType.GREY));
			}
		}

		//TODO: created to much assistants, two assistants are created at every iteration
		int j = 1;
		for(i = 0; i < numPlayers; i++){
			Assistant[] assistants = new Assistant[Assistant.NUM_ASSISTANTS];
			for(int k = 0; k < Assistant.NUM_ASSISTANTS; k++){ //da controllare manca costruttore in assistant e attributo static = 10
				assistants[k] = new Assistant(k+1,j);
				assistants[k+1] = new Assistant(k+2,j);
				k++;
				j++;
			}
			// set assistant deck to all players
			players.get(i).setAssistants(assistants);
		}

		clouds = new Cloud[numPlayers];
		for(i = 0; i < numPlayers; i++) {
			clouds[i] = new Cloud(studentsPerCloud);
		}
		//create character cards
		List<Integer> charactersCreated = new ArrayList<>();
		int characterToCreate;
		for (i = 0; i < CharacterCard.NUM_CHARACTERS_PER_GAME; i++) {
			do {
				characterToCreate = rnd.nextInt(CharacterCard.NUM_CHARACTERS);
			} while(charactersCreated.contains(characterToCreate));
			charactersCreated.add(characterToCreate);
			characterCards[i] = CharacterCreator.getCharacter(characterToCreate);
		}
	}

	public Cloud[] getClouds (){
		return this.clouds;
	}

	public int getStudentsPerCloud () {
		return studentsPerCloud;
	}

	public Bag getBag () {
		return bag;
	}

	public Player[] getPlayers () {
		return players.toArray(new Player[0]);
	}

	public Island[] getIslands() {
		return islands;
	}
}
