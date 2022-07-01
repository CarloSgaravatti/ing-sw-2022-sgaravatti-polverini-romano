package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.server.RemoteView;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Game is the main class of the model package that contains associations to all components of the model. The game class is
 * responsible for providing all information of the game state that the controllers need and is also responsible for
 * automatically update some model parts after some events (towers in islands and islands unification after a movement of
 * mother nature, professors after a dining room insertion of students in schools); this is done by implementing the
 * ModelObserver interface. The game is also a PropertyChangeListener that catch PropertyChangeEvents from model components
 * and pass them to the listeners of the listeners package
 *
 * @see it.polimi.ingsw.model.ModelObserver
 * @see java.beans.PropertyChangeListener
 */
public class Game implements ModelObserver, PropertyChangeListener {
	private boolean started;
	private final List<Player> players;
	private final Bag bag;
	private final List<Island> islands;
	private int numPlayers;
	private final Cloud[] clouds;
	private int coinGeneralSupply = 0;
	private transient CharacterCard[] characterCards;
	private int indexActivePlayer;
	private transient final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private final GameConstants gameConstants;
	private boolean isLastRound = false;
	private final boolean isExpertGame;

	/**
	 * Constructs a game that have the specified islands and clouds, that will use the specified game constants and that is
	 * an expert game with characters only if the boolean value is true
	 *
	 * @param islands the islands of the game
	 * @param clouds the clouds of the game
	 * @param gameConstants the constants of the game
	 * @param isExpertGame true if the game is expert, otherwise false
	 */
	public Game(List<Island> islands, Cloud[] clouds, GameConstants gameConstants, boolean isExpertGame){
		this.gameConstants = gameConstants;
		numPlayers = 0;
		started = false;
		bag = new Bag();
		characterCards = new CharacterCard[gameConstants.getNumCharacterPerGame()];
		this.islands = islands;
		players = new ArrayList<>();
		this.clouds = clouds;
		coinGeneralSupply = gameConstants.getNumCoins();
		this.isExpertGame = isExpertGame;
	}

	/**
	 * Construct a new restored game after the persistence data is read from the disk
	 *
	 * @param islands the islands of the game
	 * @param clouds the clouds of the game
	 * @param constants the constants of the game
	 * @param isExpertGame true if the game is expert, otherwise false
	 * @param bag the bag of the game
	 * @param players the players of the game
	 */
	public Game(List<Island> islands, Cloud[] clouds, GameConstants constants, boolean isExpertGame, Bag bag, Player[] players) {
		this.gameConstants = constants;
		started = false;
		this.bag = bag;
		this.islands = islands;
		this.players = new ArrayList<>(Arrays.stream(players).toList());
		this.clouds = clouds;
		this.isExpertGame = isExpertGame;
		characterCards = new CharacterCard[gameConstants.getNumCharacterPerGame()];
	}

	/**
	 * Create all listeners for the model components that will notify the view
	 *
	 * @param views the remote views of the players
	 * @param lobby the game lobby of the game
	 */
	public void createListeners(List<RemoteView> views, GameLobby lobby) {
		listeners.addPropertyChangeListener("EndGame", new EndGameListener(lobby));
		for (RemoteView r: views) {
			listeners.addPropertyChangeListener("MotherNature", new MotherNatureListener(r));
			IslandListener listener = new IslandListener(r);
			listeners.addPropertyChangeListener("IslandStudents", listener);
			listeners.addPropertyChangeListener("IslandTower", listener);
			listeners.addPropertyChangeListener("IslandUnification", listener);
			listeners.addPropertyChangeListener("PickFromCloud", new CloudListener(r));
			getPlayerByNickname(r.getPlayerNickname()).addListener(new PlayerListener(r));
			CharacterListener characterListener = new CharacterListener(r);
			if (isExpertGame) {
				for (CharacterCard c: characterCards) c.addListener(characterListener);
			}
		}
		for (Island i: islands) i.addListener(this);
		for (Cloud c: clouds) c.addListener(this);
	}

	/**
	 * Returns true if the game is started, otherwise false
	 */
	public void start() {
		started = true;
	}

	/**
	 * Set the number of players of the game
	 *
	 * @param numPlayers the number of players of the game
	 */
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	/**
	 * Add a player with the specified nickname to the game
	 *
	 * @param nickname the nickname of the player
	 */
	public void addPlayer(String nickname) {
		if (players.size() < numPlayers) {
			players.add(new Player(nickname));
			coinGeneralSupply--;
		}
	}

	/**
	 * Returns the number of players of the game
	 *
	 * @return the number of players of the game
	 */
	public int getNumPlayers() {
		return players.size();
	}

	/**
	 * Returns true if the game is started, otherwise false
	 *
	 * @return true if the game is started, otherwise false
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Return the index of the active player in the players list
	 *
	 * @return the index of the active player in the players list
	 */
	public int getIndexActivePlayer() {
		return indexActivePlayer;
	}

	/**
	 * Set the value of the index of the active player in the players list
	 *
	 * @param indexActivePlayer the value of the index of the active player in the players list
	 */
	public void setIndexActivePlayer(int indexActivePlayer) {
		this.indexActivePlayer = indexActivePlayer;
	}

	/**
	 * Moves mother nature by the specified movement
	 *
	 * @param movement the movement of mother nature
	 */
	public void moveMotherNature(int movement){
		int initialPosition = motherNaturePositionIndex();
		islands.get(initialPosition).setMotherNaturePresent(false);
		int finalPosition = (initialPosition + movement) % islands.size();
		listeners.firePropertyChange("MotherNature", initialPosition, finalPosition);
		islands.get(finalPosition).setMotherNaturePresent(true);
	}

	/**
	 * Returns the clouds of the game
	 *
	 * @return the clouds of the game
	 */
	public Cloud[] getClouds (){
		return this.clouds;
	}

	/**
	 * Returns the bag of the game
	 *
	 * @return the bag of the game
	 */
	public Bag getBag () {
		return bag;
	}

	/**
	 * Returns the players of the game
	 *
	 * @return the players of the game
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Returns the islands of the game
	 *
	 * @return the islands of the game
	 */
	public List<Island> getIslands() {
		return islands;
	}

	/**
	 * Create all characters for the expert game. The characters are created randomly
	 */
	public void createCharacterCards() {
		Random rnd = new Random();
		List<Integer> charactersCreated = new ArrayList<>();
		int characterToCreate;
		CharacterCreator characterCreator = new CharacterCreator(this);
		for (int i = 0; i < gameConstants.getNumCharacterPerGame(); i++) {
			do {
				characterToCreate = rnd.nextInt(gameConstants.getNumCharacters());
			} while(charactersCreated.contains(characterToCreate));
			charactersCreated.add(characterToCreate);
			characterCards[i] = characterCreator.getCharacter(characterToCreate + 1);
		}
	}

	/**
	 * Returns the character cards of the game
	 *
	 * @return the character cards of the game
	 */
	public CharacterCard[] getCharacterCards() {
		return characterCards;
	}

	/**
	 * Returns the player that owns the specified tower, or null if such player does not exist
	 *
	 * @param towerType the type of tower
	 * @return the player that owns the specified tower
	 */
	public Player getPlayerByTowerType(TowerType towerType) {
		for (Player p: players) {
			if (p.getSchool().getTowerType() == towerType) return p;
		}
		//temporary solution
		return null;
	}

	/**
	 * Returns the player that have the specified nickname, or null if such player does not exists
	 *
	 * @param nickname the nickname of the player
	 * @return the player that have the specified nickname
	 */
	public Player getPlayerByNickname(String nickname) {
		for (Player p: players) {
			if (p.getNickName().equals(nickname)) return p;
		}
		return null;
	}

	/**
	 * Set the active player of the game
	 *
	 * @param player the active player of the turn
	 */
	public void setActivePlayer(Player player) {
		this.indexActivePlayer = players.indexOf(player);
	}

	/**
	 * Generate all students that will be present at the beginning of the game in the bag
	 */
	public void createAllStudentsForBag() {
		int numPerType = gameConstants.getNumTotalStudents() / RealmType.values().length;
		for(RealmType r: RealmType.values()){
			for(int i = 0; i < numPerType;i++){
				bag.insertStudent(new Student(r));
			}
		}
	}

	/**
	 * Generate all the students that will be putted in the bag and then extracted to the islands
	 */
	public void genStudentForBeginning() {
		for(int i = 0; i < 2; i++) {
			for (RealmType r : RealmType.values()) {
				bag.insertStudent(new Student(r));
			}
		}
	}

	/**
	 * Insert the specified coins in the general supply of the game
	 *
	 * @param coins the coins that are added to the general supply
	 */
	public void insertCoinsInGeneralSupply(int coins){
		coinGeneralSupply += coins;
	}

	/**
	 * Removes a coin from the general supply
	 */
	public void takeCoinFromGeneralSupply() {
		coinGeneralSupply --;
	}

	/**
	 * Assign an assistant deck with the specified wizard to the specified player
	 *
	 * @param player the player that will have the specified assistants
	 * @param type the wizard that will be assigned to the player
	 */
	public void assignDeck(Player player, WizardType type){
		List<Assistant> assistants = new ArrayList<>();
		int j = 0;
		for(int i = 1; i <= gameConstants.getNumAssistantsPerWizard() / 2; i++){
			j++;
			assistants.add(new Assistant(j,i,type));
			j++;
			assistants.add(new Assistant(j,i,type));
		}
		player.setAssistants(assistants);
		player.setWizardType(type);
	}

	/**
	 * Updates the professor of the specified RealmType
	 *
	 * @param studentType the Realm Type of the professor to update
	 * @see ModelObserver#updateProfessorPresence(RealmType)
	 */
	@Override
	public void updateProfessorPresence(RealmType studentType) {
		Optional<Player> currPlayerProfessor = players.stream()
				.filter(p -> p.getSchool().isProfessorPresent(studentType)).findFirst();
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
		if (playerTakeProfessor != null && !playerTakeProfessor.getSchool().isProfessorPresent(studentType)) {
			currPlayerProfessor.ifPresent(p -> p.getSchool().removeProfessor(studentType));
			playerTakeProfessor.getSchool().insertProfessor(studentType);
		}
	}

	/**
	 * Updates the tower of the specified island
	 *
	 * @param island the island that needs to be updated
	 * @see ModelObserver#updateIslandTower(Island)
	 */
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
			Player previousOwner = null;
			if (island.getTowerType() != null) {
				previousOwner = getPlayerByTowerType(island.getTowerType());
				previousOwner.getSchool().insertTower(island.getNumTowers());
			}
			playerMaxInfluence.getSchool().sendTowerToIsland(island);
			listeners.firePropertyChange(new PropertyChangeEvent(
					islands.indexOf(island), "IslandTower", previousOwner, playerMaxInfluence
			));
			updateIslandUnification(island);
			if (playerMaxInfluence.getSchool().getNumTowers() == 0) checkWinners();
		}
	}

	/**
	 * Check if the specified island can be unified with the near islands
	 *
	 * @param island the island that have had a tower update
	 */
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
			Island newIsland = new IslandGroup(true, islandToUnify.toArray(new Island[0]));
			islands.add(indexToReplace, newIsland);
			newIsland.addListener(this);
			newIsland.addObserver(this);
			listeners.firePropertyChange( "IslandUnification", islandIndexes.toArray(new Integer[0]), newIsland);
			if (islands.size() <= 3) checkWinners();
		}
	}

	/**
	 * Update the specified student container by inserting a student in it
	 *
	 * @param studentContainer the student container to update
	 * @see ModelObserver#updateStudentContainer(StudentContainer)
	 */
	@Override
	public void updateStudentContainer(StudentContainer studentContainer) {
		try {
			studentContainer.insertStudent(bag.pickStudent());
		} catch(EmptyBagException e) {
			setLastRound(true);
		}
	}

	/**
	 * Returns the position of mother nature in the islands
	 *
	 * @return the position of mother nature in the islands
	 */
	public int motherNaturePositionIndex() {
		int i = 0;
		while (!islands.get(i).isMotherNaturePresent()) {
			i++;
		}
		return i;
	}

	/**
	 * Returns the character associated with the specified id or null if that character does not exist
	 *
	 * @param characterId the id of the character
	 * @return the character associated with the specified id
	 */
	public CharacterCard getCharacterById(int characterId) {
		for (CharacterCard c: characterCards) {
			if (c != null && characterId == c.getId()) return c;
		}
		return null;
	}

	/**
	 * Performs the setup for all islands, by adding this class as an observer for each island and by generating a random
	 * position for
	 *
	 * @throws EmptyBagException if the bag finished the students
	 */
	public void setupIslands() throws EmptyBagException {
		Random rnd = new Random();
		int numIslands = gameConstants.getNumIslands();
		int indexOfMotherNature = rnd.nextInt(numIslands);
		islands.get(indexOfMotherNature).setMotherNaturePresent(true);
		for(int i = 0; i < numIslands;i++){
			islands.get(i).addObserver(this);
			if(i != (indexOfMotherNature + (numIslands / 2)) % numIslands  && i != indexOfMotherNature) {
				islands.get(i).addStudent(bag.pickStudent());
			}
		}
	}

	/**
	 * Check if someone has won the game at the end of the game and fires and end game event to the listeners
	 */
	public void checkWinners() {
		List<Player> winnerOrTies = onGameEndEvent();
		boolean isWin = winnerOrTies.size() == 1;
		PropertyChangeEvent evt = new PropertyChangeEvent(winnerOrTies.stream()
				.map(Player::getNickName).toList().toArray(new String[0]), "EndGame", null, isWin);
		listeners.firePropertyChange(evt);
	}

	/**
	 * Returns the constants of the game
	 *
	 * @return the constants of the game
	 */
	public GameConstants getGameConstants() {
		return gameConstants;
	}

	/**
	 * Returns a list of player that contains one element (the winner) if there is a winner or a list of elements (the tiers)
	 * if there aren't winners
	 *
	 * @return a list of player that contains one element (the winner) if there is a winner or a list of elements (the tiers)
	 * if there aren't winners
	 */
	protected List<Player> onGameEndEvent() {
		List<Player> winnersOrTies = new ArrayList<>();
		checkPlayerZeroTower().ifPresent(winnersOrTies::add);
		if (!winnersOrTies.isEmpty()) return winnersOrTies;
		winnersOrTies = getPlayersWithLessTowers();
		if (winnersOrTies.size() == 1) return winnersOrTies;
		return getPlayersWithMostProfessors(winnersOrTies);
	}

	/**
	 * Checks if there is a player has zero towers in his school, in this case he is the winner. Returns true if there is
	 * such a player, otherwise false.
	 *
	 * @return true if there is a player with zero towers, otherwise false
	 */
	private Optional<Player> checkPlayerZeroTower() {
		for (Player p: players) {
			if (p.getSchool().getNumTowers() == 0) {
				return Optional.of(p);
			}
		}
		return Optional.empty();
	}

	/**
	 * Checks if there is a player has fewer towers than the others, in this case he is the winner. Returns all the players
	 * with the minimum number of towers.
	 *
	 * @return all the players with the minimum number of towers.
	 */
	private List<Player> getPlayersWithLessTowers() {
		int minTowers = players.stream().map(player -> player.getSchool().getNumTowers())
				.min(Comparator.comparingInt(n -> n)).orElseGet(gameConstants::getNumTowers);
		return players.stream().filter(player -> player.getSchool().getNumTowers() == minTowers)
				.collect(Collectors.toList());
	}

	/**
	 * Checks if there is a player has more professors than the others, in this case he is the winner. Returns all the players
	 * with the maximum number of professors.
	 *
	 * @return all the players with the maximum number of professors
	 */
	private List<Player> getPlayersWithMostProfessors(List<Player> players) {
		int maxProfessors = players.stream().map(player -> player.getSchool().getProfessorNumber())
				.max(Comparator.comparingInt(n -> n)).orElse(0);
		return players.stream().filter(player -> player.getSchool().getProfessorNumber() == maxProfessors)
				.collect(Collectors.toList());
	}

	/**
	 * Returns true if it is the last round, otherwise false
	 *
	 * @return true if it is the last round, otherwise false
	 */
	public boolean isLastRound() {
		return isLastRound;
	}

	public void setLastRound(boolean isLastRound) {
		this.isLastRound = isLastRound;
	}

	/**
	 * Returns the number of coins in the coin general supply
	 *
	 * @return the number of coins in the coin general supply
	 */
	public int getCoinGeneralSupply() {
		return coinGeneralSupply;
	}

	/**
	 * Responds to an event that come from a model component
	 *
	 * @param evt A PropertyChangeEvent object describing the event source
	 *          and the property that has changed.
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
			case "IslandStudents", "IslandTower" -> {
				Integer islandIndex = islands.indexOf((Island) evt.getSource());
				listeners.firePropertyChange(new PropertyChangeEvent(
						islandIndex, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
			}
			case "MotherNature" -> updateIslandTower((Island) evt.getNewValue());
			case "PickFromCloud" -> {
				int cloudIndex = 0;
				while(evt.getSource() != clouds[cloudIndex]) cloudIndex++; //!= because I want the exact cloud object
				listeners.firePropertyChange(new PropertyChangeEvent(
						players.get(indexActivePlayer).getNickName(), "PickFromCloud",
						null, new Pair<>(cloudIndex, (Student[]) evt.getNewValue())
				));
			}
		}
	}

	/**
	 * Returns true if the game is expert, otherwise false
	 *
	 * @return true if the game is expert, otherwise false
	 */
	public boolean isExpertGame() {
		return isExpertGame;
	}

	/**
	 * Restore the characters of the game after the expert game was restored from persistence data
	 *
	 * @param characterCards the restored characters
	 */
	public void restoreCharacters(CharacterCard[] characterCards) {
		this.characterCards = characterCards;
		for(CharacterCard characterCard: characterCards) {
			characterCard.restoreCharacter(this);
		}
	}
}
