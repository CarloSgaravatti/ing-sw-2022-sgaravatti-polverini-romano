package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.EmptyCloudException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {

	private boolean started;
	private Player[] players;
	private Round round;
	private Bag bag;
	private Island[] island;
	private int numPlayers;
	private int numRound;
	private Cloud[] clouds;
	private int studentsPerCloud;
	private int coinGeneralSupply;
	private CharacterCard[] characterCards;

	public Game(){
		numPlayers = 0;
		numRound = 0;
		started = false;
		bag = new Bag();
		coinGeneralSupply = 20;
		characterCards = new CharacterCard[CharacterCard.NUM_CHARACTERS_PER_GAME];
	}

	public void addPlayer(Player p) {
		numPlayers++;
	}

	public void start() throws EmptyBagException, EmptyCloudException {
		//maybe it is not necessary the if instruction, because someone call start()
		//only when there is a correct number of players
		if(numPlayers >=2 || numPlayers <=4) {
			started = true;
			doPreparation();
			//newRound();
			studentsPerCloud = (numPlayers == 3) ? 4 : 3;
		}
		//What i propose:
		while(!isFinished()) {
			Round currRound = newRound();
			currRound.planningPhase(players);
			currRound.actionPhase(players);
		}
	}

	public int getNumPlayers(){
		return numPlayers;
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
		//creating list of students and chose the type
		for(i=0;i<Student.NUM_STUDENTS;i++){
			if(i<26){
				bag.insertStudent(new Student(RealmType.YELLOW_GNOMES));
			}
			if(i>=26 && i<52){
				bag.insertStudent(new Student(RealmType.BLUE_UNICORNS));
			}
			if(i>=52 && i<78){
				bag.insertStudent(new Student(RealmType.GREEN_FROGS));
			}
			if(i>=78 && i<104){
				bag.insertStudent(new Student(RealmType.RED_DRAGONS));
			}
			if(i>=104){
				bag.insertStudent(new Student(RealmType.PINK_FAIRES));
			}
		}

		Professor[] professors = new Professor[Professor.NUM_PROFESSORS];
		professors[RealmType.YELLOW_GNOMES.ordinal()] = new Professor(RealmType.YELLOW_GNOMES);
		professors[RealmType.BLUE_UNICORNS.ordinal()] = new Professor(RealmType.BLUE_UNICORNS);
		professors[RealmType.GREEN_FROGS.ordinal()] = new Professor(RealmType.GREEN_FROGS);
		professors[RealmType.RED_DRAGONS.ordinal()] = new Professor(RealmType.RED_DRAGONS);
		professors[RealmType.PINK_FAIRES.ordinal()] = new Professor(RealmType.PINK_FAIRES);

		players = new Player[numPlayers];
		for(i=0; i< numPlayers; i++){
			// chose nickname and create player
			System.out.println("Chose your nickname: ");
			Scanner s1 = new Scanner(System.in);
			String nick = s1.nextLine();
			players[i]=new Player(nick);
		}
		coinGeneralSupply -= numPlayers;

		Island[] islands = new Island[Island.NUM_ISLANDS]; // devo creare isole ma non so come fare perchè forse devo usare Singleisland
		for(i=0;i<Island.NUM_ISLANDS;i++){
			islands[i] = new SingleIsland();
		}
		//ass mother nature and students on islands
		Random rnd = new Random();
		int islandMotherNature = rnd.nextInt(Island.NUM_ISLANDS);
		islands[islandMotherNature].setMotherNaturePresent(true);
		int islandNoStudent=0;
		islandNoStudent = (islandMotherNature + 6)%Island.NUM_ISLANDS;
		for(i=0;i<Island.NUM_ISLANDS;i++){
			if(i!=islandMotherNature && i!= islandNoStudent){
				islands[i].addStudent(bag.pickStudent());
			}
		}

		//creation of schools and set for each student
		School[] schools = new School[numPlayers];
		for(i=0; i< numPlayers; i++){
			if(numPlayers ==2 || numPlayers ==4) {
				if(i==0) {
					schools[i] = new School(8, TowerType.WHITE);
					players[i].setSchool(schools[i]);
				}
				if(i==1){
					schools[i] = new School(8, TowerType.BLACK);
					players[i].setSchool(schools[i]);
				}
			}
			if(numPlayers ==3) {
				if(i==0) {
					schools[i] = new School(6, TowerType.WHITE);
					players[i].setSchool(schools[i]);
				}
				if(i==1){
					schools[i] = new School(6, TowerType.BLACK);
					players[i].setSchool(schools[i]);
				}
				if(i==2){
					schools[i] = new School(6, TowerType.GREY);
					players[i].setSchool(schools[i]);
				}
			}
		}

		int j=1;
		for(i=0; i< numPlayers; i++){
			Assistant[] assistants = new Assistant[Assistant.NUM_ASSISTANTS];
			for(i=0; i<Assistant.NUM_ASSISTANTS; i++){ //da controllare manca costruttore in assistant e attributo static = 10
				assistants[i] = new Assistant(i+1,j);
				assistants[i+1] = new Assistant(i+2,j);
				i++;
				j++;
			}
			// set assistant deck to all players
			players[i].setAssistants(assistants);
		}

		clouds = new Cloud[numPlayers];
		for(i=0; i< numPlayers; i++){
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
		return players;
	}
}
