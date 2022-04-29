package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.listeners.IslandListener;
import it.polimi.ingsw.listeners.MotherNatureListener;
import it.polimi.ingsw.listeners.SchoolListener;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;

import javax.swing.event.EventListenerList;
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
	private final Cloud[] clouds;
	private int studentsPerCloud;
	private int coinGeneralSupply;
	private final CharacterCard[] characterCards;
	private int indexActivePlayer;
	private final static int NUM_STUDENTS = 130;
	private int numStudents = NUM_STUDENTS; //Is this useful?
	private final EventListenerList listenerList = new EventListenerList();

	//TODO: invoke addEventListener method into InitController after making RemoteView
	public void addEventListener(IslandListener listener) {
		listenerList.add(IslandListener.class, listener);
	}

	public void addEventListener(SchoolListener listener){
		listenerList.add(SchoolListener.class, listener);
	}

	public void addEventListener(MotherNatureListener listener){
		listenerList.add(MotherNatureListener.class,listener);
	}

	public Game(List<Island> islands, Cloud[] clouds){
		numPlayers = 0;
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

	//TODO: this has to be done at the beginning of the match
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public void addPlayer(String nickname) {
		if (players.size() < numPlayers) {
			players.add(new Player(nickname));
			coinGeneralSupply--;
		}
		//else exception
	}

	public int getNumPlayers(){
		return players.size();
	}

	public boolean isStarted() {
		return started;
	}

	public void moveMotherNature(int movement){
		int i=0;
		while(!islands.get(i).isMotherNaturePresent() && i<islands.size()) {
			i++;
		}
		islands.get(i).setMotherNaturePresent(false);
		int initialPosition = i;
		i = (i + movement) % islands.size();
		islands.get(i).setMotherNaturePresent(true);
		fireMyEvent(initialPosition+1,i+1);
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

	public void createCharacterCard() {
		Random rnd = new Random();
		List<Integer> charactersCreated = new ArrayList<>();
		int characterToCreate;
		CharacterCreator characterCreator = new CharacterCreator(this);
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

	public Player getPlayerByNickname(String nickname) {
		for (Player p: players) {
			if (p.getNickName().equals(nickname)) return p;
		}
		return null;
	}

	public void setIndexActivePlayer(Player player) {
		this.indexActivePlayer = players.indexOf(player);
	}

	public void createAllStudentsForBag(){
		for(RealmType r: RealmType.values()){
			for(int i = 0; i<24;i++){
				bag.insertStudent(new Student(r));
				numStudents--; //Not needed
			}
		}
	}

	public void genStudentForBeginning(){
		for(int i = 0; i< 2; i++) {
			for (RealmType r : RealmType.values()) {
				bag.insertStudent(new Student(r));
				numStudents--; //Not needed
			}
		}
	}

	public void insertCoinsInGeneralSupply(int coins){
		coinGeneralSupply += coins;
	}

	public void takeCoinFromGeneralSupply() {
		coinGeneralSupply --;
		//TODO: exception if coins are finished
	}

	public void assignDeck(Player player, WizardType type){
		List<Assistant> assistants = new ArrayList<>();
		int j=0;
		for(int i = 1; i <= 5; i++){
			j++;
			assistants.add(new Assistant(j,i,type));
			j++;
			assistants.add(new Assistant(j,i,type));
		}
		player.setAssistants(assistants);
		player.setWizardType(type);
	}

	//TODO: assign also school


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
			fireMyEvent(studentType,playerTakeProfessor.getNickName());
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
		if (maxInfluenceOcc == 0) return;
		Player playerMaxInfluence = players.get(playerInfluences.indexOf(maxInfluence));
		if (maxInfluenceOcc == 1 && island.getTowerType() != playerMaxInfluence.getSchool().getTowerType()) {
			if (island.getTowerType() != null) {
				getPlayerByTowerType(island.getTowerType()).getSchool().insertTower(island.getNumTowers());
			}
			playerMaxInfluence.getSchool().sendTowerToIsland(island);
			fireMyEvent(playerMaxInfluence.getSchool().getTowerType(), islands.indexOf(island));
			updateIslandUnification(island);
		}
	}

	@Override
	public void updateIslandUnification(Island island) {
		List<Island> islandToUnify = new ArrayList<>();
		List<Integer> islandIndexes = new ArrayList<>();
		int islandIndex = islands.indexOf(island);
		int leftIndex = (islandIndex + islands.size() - 1) % islands.size();
		int rightIndex = (islandIndex + 1) % islands.size();
		int indexToReplace = islandIndex;
		TowerType towerType = island.getTowerType();
		if (islands.get(leftIndex).getTowerType() == towerType) {
			islandToUnify.add(islands.get(leftIndex));
			indexToReplace = Integer.min(indexToReplace, leftIndex);
			islandIndexes.add(leftIndex);
		}
		if (islands.get(rightIndex).getTowerType() == towerType) {
			islandToUnify.add(islands.get(rightIndex));
			indexToReplace = Integer.min(indexToReplace, rightIndex);
			islandIndexes.add(rightIndex);
		}
		if (!islandToUnify.isEmpty()) {
			islandToUnify.add(island);
			islandIndexes.add(islandIndex);
			islands.removeAll(islandToUnify);
			islands.add(indexToReplace, new IslandGroup(islandToUnify.toArray(new Island[0])));
			fireMyEvent(islandIndexes);
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

	//For testing
	public int motherNaturePositionIndex() {
		int i = 0;
		while (!islands.get(i).isMotherNaturePresent()) {
			i++;
		}
		return i;
	}

	public CharacterCard getCharacterById(int characterId) {
		for (CharacterCard c: characterCards) {
			if (characterId == c.getId()) return c;
		}
		return null;
	}

	public void setupIslands() throws EmptyBagException {
		Random rnd = new Random();
		int indexOfMotherNature = rnd.nextInt(Island.NUM_ISLANDS);
		islands.get(indexOfMotherNature).setMotherNaturePresent(true);
		for(int i = 0; i < Island.NUM_ISLANDS;i++){
			islands.get(i).addObserver(this);
			if(i != (indexOfMotherNature+(Island.NUM_ISLANDS/2))%Island.NUM_ISLANDS  && i != indexOfMotherNature) {
				islands.get(i).addStudent(bag.pickStudent());
			}
		}
	}

	void fireMyEvent(TowerType type, int indexIslands) {
		for(IslandListener event : listenerList.getListeners(IslandListener.class)){
			event.eventPerformed(type, indexIslands);
		}
	}

	void fireMyEvent(List<Integer> islandIndexList ) {
		for(IslandListener event : listenerList.getListeners(IslandListener.class)){
			event.eventPerformed(islandIndexList);
		}
	}

	void fireMyEvent(RealmType type, String namePlayer){
		for(SchoolListener event : listenerList.getListeners(SchoolListener.class)){
			event.eventPerformed(type,namePlayer);
		}
	}

	void fireMyEvent(int initialPosition, int finalPosition){
		for(MotherNatureListener event : listenerList.getListeners(MotherNatureListener.class)){
			event.eventPerformed(initialPosition,finalPosition);
		}
	}

}
