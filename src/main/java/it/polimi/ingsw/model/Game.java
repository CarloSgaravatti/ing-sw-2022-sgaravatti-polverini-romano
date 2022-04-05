package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.SchoolWithoutTowersException;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Game implements ModelObserver{
	private boolean started;
	private final List<Player> players;
	private final Bag bag;
	private final List<Island> islands;
	private int numPlayers;
	private int numRound;
	private final Cloud[] clouds;
	private int studentsPerCloud;
	private int coinGeneralSupply;
	private final CharacterCard[] characterCards;
	private int indexActivePlayer;
	private final static int NUM_STUDENTS = 130;
	private int numStudents=NUM_STUDENTS;

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
		while(!islands.get(i).isMotherNaturePresent() && i<islands.size()) {
			i++;
		}
		islands.get(i).setMotherNaturePresent(false);
		i = (i + movement) % islands.size();
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
		CharacterCreator characterCreator = CharacterCreator.getInstance();
		characterCreator.setGame(this);
		for (int i = 0; i < CharacterCard.NUM_CHARACTERS_PER_GAME; i++) {
			do {
				characterToCreate = rnd.nextInt(CharacterCard.NUM_CHARACTERS);
			} while(charactersCreated.contains(characterToCreate));
			charactersCreated.add(characterToCreate);
			characterCards[i] = characterCreator.getCharacter(characterToCreate + 1);
		}
	}

	public CharacterCard[] getCharacterCards() {
		return characterCards;
	}

	public Player getPlayerByTowerType(TowerType towerType) {
		for (Player p: players) {
			if (p.getSchool().getTowerType() == towerType) return p;
		}
		//temporary solution
		return null;
	}

	public void setIndexActivePlayer(Player player) {
		this.indexActivePlayer = players.indexOf(player);
	}

	public void createAllStudentsForBag(){
		for(RealmType r: RealmType.values()){
			for(int i = 0; i<24;i++){
				bag.insertStudent(new Student(r));
				numStudents--;
			}
		}
	}

	public void genStudentForBeginning(){
		for(int i = 0; i< 2; i++) {
			for (RealmType r : RealmType.values()) {
				bag.insertStudent(new Student(r));
				numStudents--;
			}
		}
	}

	public void insertCoinsInGeneralSupply(int coins) {
		coinGeneralSupply += coins;
	}


	//This method maybe can be done in a better way
	@Override
	public void updateProfessorPresence(RealmType studentType) {
		Optional<Player> currPlayerProfessor = players.stream()
				.filter(p -> p.getSchool().isProfessorPresent(studentType))
				.findFirst();
		int indexPlayerProfessor = currPlayerProfessor.map(players::indexOf).orElseGet(() -> numPlayers + 1);
		int maxStudents = 0;
		Player playerTakeProfessor = null;
		if (currPlayerProfessor.isPresent()) {
			maxStudents = currPlayerProfessor.get().getSchool().getNumStudentsDiningRoom(studentType);
			playerTakeProfessor = currPlayerProfessor.get();
		}
		for (int i = 0; i < numPlayers; i++) {
			if (i != indexPlayerProfessor) {
				Player currPlayer = players.get(i);
				int students = currPlayer.getSchool().getNumStudentsDiningRoom(studentType);
				//Only one player can have the professor precedence at a time
				if (students > maxStudents || (students == maxStudents &&
						currPlayer.getTurnEffect().isProfessorPrecedence())) {
					playerTakeProfessor = currPlayer;
					maxStudents = students;
				}
			}
		}
		if (playerTakeProfessor != null) {
			currPlayerProfessor.ifPresent(p -> p.getSchool().removeProfessor(studentType));
			playerTakeProfessor.getSchool().insertProfessor(studentType);
		}
	}

	@Override
	public void updateIslandTower(Island island) {
		List<Integer> playerInfluences = new ArrayList<>();
		int maxInfluence = 0;
		for (int i = 0; i < numPlayers; i++) {
			playerInfluences.add(players.get(indexActivePlayer).getTurnEffect().getInfluence(island, players.get(i)));
			if (playerInfluences.get(i) > maxInfluence) {
				maxInfluence = playerInfluences.get(i);
			}
		}
		int maxInfluenceOcc = 0;
		for (Integer i: playerInfluences) {
			if (i == maxInfluence) maxInfluenceOcc++;
		}
		Player playerMaxInfluence = players.get(playerInfluences.indexOf(maxInfluence));
		if (maxInfluenceOcc == 1 && island.getTowerType() != playerMaxInfluence.getSchool().getTowerType()) {
			if (island.getTowerType() != null) {
				getPlayerByTowerType(island.getTowerType()).getSchool().insertTower(island.getNumTowers());
			}
			playerMaxInfluence.getSchool().sendTowerToIsland(island);
			updateIslandUnification(island);
		}
	}

	@Override
	public void updateIslandUnification(Island island) {
		List<Island> islandToUnify = new ArrayList<>();
		int islandIndex = islands.indexOf(island);
		//need to add islands.size() before calculate in (mod islands.size()) because of possible
		//negative value if islandIndex is 0
		int leftIndex = (islandIndex + islands.size() - 1) % islands.size();
		int rightIndex = (islandIndex + 1) % islands.size();
		int indexToReplace = islandIndex;
		TowerType towerType = island.getTowerType();
		if (towerType == null) return;
		if (islands.get(leftIndex).getTowerType() == towerType) {
			islandToUnify.add(islands.get(leftIndex));
			indexToReplace = Integer.min(indexToReplace, leftIndex);
		}
		if (islands.get(rightIndex).getTowerType() == towerType) {
			islandToUnify.add(islands.get(rightIndex));
			indexToReplace = Integer.min(indexToReplace, rightIndex);
		}
		if (!islandToUnify.isEmpty()) {
			islandToUnify.add(island);
			islands.removeAll(islandToUnify);
			islands.add(indexToReplace, new IslandGroup(islandToUnify.toArray(new Island[0])));
		}
	}

	@Override
	public void updateStudentContainer(StudentContainer studentContainer) {
		try {
			studentContainer.insertStudent(bag.pickStudent());
		} catch(EmptyBagException e) {
			//TODO: the game is finished
		}
	}
}
