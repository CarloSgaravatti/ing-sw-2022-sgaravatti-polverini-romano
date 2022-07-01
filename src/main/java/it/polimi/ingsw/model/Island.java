package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.Character5;
import it.polimi.ingsw.model.effects.NoEntryTileManager;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.modelObservables.MotherNatureMovementObservable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

/**
 * Island abstract class represent a generic island of the island map of the game. The island contains students and can
 * also contain towers, mother nature and no entry tiles. An Island can be a SingleIsland or an IslandGroup, for which the
 * composite pattern was used.
 *
 * @see MotherNatureMovementObservable
 * @see SingleIsland
 * @see IslandGroup
 */
public abstract class Island extends MotherNatureMovementObservable {
	private TowerType towerType;
	private boolean motherNaturePresent;
	private int noEntryTilePresents;
	private transient NoEntryTileManager noEntryTileManager = null;
	public static final int NUM_ISLANDS = 12;
	public transient PropertyChangeSupport game = new PropertyChangeSupport(this);

	/**
	 * Constructs an empty Island with no students, no entry tiles and no mother nature in it
	 */
	public Island() {
		super();
		motherNaturePresent = false;
		noEntryTilePresents = 0;
	}

	/**
	 * Construct a new Island that have the specified tower and that will contain mother nature in base of the boolean value
	 *
	 * @param towerType the tower of the constructed island
	 * @param motherNaturePresent true if mother nature is present on the island, otherwise false
	 */
	protected Island(TowerType towerType, boolean motherNaturePresent){
		this();
		this.towerType = towerType;
		this.motherNaturePresent = motherNaturePresent;
	}

	/**
	 * Insert a tower of the specified type on the island
	 *
	 * @param t the inserted tower
	 */
	public abstract void putTower(TowerType t);

	/**
	 * Returns the students that are present in the island
	 *
	 * @return the students that are present in the island
	 */
	public abstract List<Student> getStudents();

	/**
	 * Returns the number of students of the specified type that are present on the island
	 *
	 * @param studentType the type of students
	 * @return the number of students of the specified type that are present on the island
	 */
	public abstract int getNumStudentsOfType (RealmType studentType);

	/**
	 * Adds the specified student to the island
	 *
	 * @param s the student that will be added
	 */
	public abstract void addStudent(Student s);

	/**
	 * Returns the number of towers that the island can take
	 *
	 * @return the number of towers that the island can take
	 */
	public abstract int getNumTowers();

	/**
	 * Returns true if mother nature is present on the island, otherwise false
	 *
	 * @return true if mother nature is present on the island, otherwise false
	 */
	public boolean isMotherNaturePresent() {
		return motherNaturePresent;
	}

	/**
	 * Method setMotherNaturePresent set mother nature presence on the island; if mother nature
	 * is inserted on the island and there aren't no entry tiles present on the island, this method
	 * calls the notifyObservers method of the super class; if mother nature is inserted on the island
	 * and there are no entry tiles this method removes one of them.
	 *
	 * @param motherNaturePresent boolean value that specifies if mother nature is about to be
	 *                            inserted on the island or not
	 */
	public void setMotherNaturePresent(boolean motherNaturePresent){
		this.motherNaturePresent = motherNaturePresent;
		if (noEntryTilePresents == 0 && motherNaturePresent) {
			notifyObservers(this);
		} else if (noEntryTilePresents > 0 && noEntryTileManager != null){
			removeNoEntryTile();
		}
	}

	/**
	 * Returns the number of no entry tiles present on the island
	 * @return the number of no entry tiles
	 */
	public int getNoEntryTilePresents() {
		return noEntryTilePresents;
	}

	/**
	 * Inserts a no entry tile in the island
	 *
	 * @param noEntryTileManager the NoEntryTileManager from whom the no entry tile is taken
	 */
	public void insertNoEntryTile(NoEntryTileManager noEntryTileManager) {
		this.noEntryTilePresents++;
		if (this.noEntryTileManager == null) this.noEntryTileManager = noEntryTileManager;
	}

	/**
	 * Removes a no entry tile from the island and adds it to the no entry tiles manager that have previously added
	 * the no entry tile to the island
	 */
	//This method is private because a no entry tile can be removed only when mother nature
	//comes to this island
	private void removeNoEntryTile () {
		noEntryTileManager.insertNoEntryTile();
		noEntryTilePresents--;
	}

	/**
	 * Returns the tower that is present on the island. If no tower is present, the method returns null
	 *
	 * @return the tower that is present on the island
	 */
	public TowerType getTowerType() {
		return towerType;
	}

	/**
	 * Assign a tower to the island
	 *
	 * @param towerType the assigned tower
	 */
	public void setTowerType(TowerType towerType) {
		this.towerType = towerType;
	}

	/**
	 * Adds the specified students to the island
	 *
	 * @param isFromEntrance true if the students come from the entrance, otherwise false
	 * @param students the students that are added
	 */
	public void addStudents(boolean isFromEntrance, Student... students) {
		for (Student s: students) {
			addStudent(s);
		}
		game.firePropertyChange("IslandStudents", isFromEntrance, Arrays.stream(students)
				.map(Student::getStudentType).toList().toArray(new RealmType[0]));
	}

	/**
	 * Adds a PropertyChangeListener that will listen to the island
	 *
	 * @param listener the property change listener that will listen to the island
	 */
	public void addListener(PropertyChangeListener listener) {
		game.addPropertyChangeListener(listener);
	}

	/**
	 * Restore the island observers and the no entry manager (if it exists in the game) after the game was restored
	 * from persistence data.
	 *
	 * @param game the restored game
	 */
	public void restoreIsland(Game game) {
		addObserver(game);
		CharacterCard characterCard = game.getCharacterById(5);
		if (characterCard != null) noEntryTileManager = (Character5) characterCard;
	}
}
