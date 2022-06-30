package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.Character5;
import it.polimi.ingsw.model.effects.NoEntryTileManager;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.modelObservables.MotherNatureMovementObservable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * ...
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
	 * Constructs an empty Island with no students, no entry tiles and mother nature in it
	 */
	public Island() {
		super();
		motherNaturePresent = false;
		noEntryTilePresents = 0;
	}

	protected Island(TowerType towerType, boolean motherNaturePresent){
		this();
		this.towerType = towerType;
		this.motherNaturePresent = motherNaturePresent;
	}

	public abstract void putTower(TowerType t);

	public abstract List<Student> getStudents();

	public abstract int getNumStudentsOfType (RealmType studentType);

	public abstract void addStudent(Student s);

	public abstract int getNumTowers();

	public boolean isMotherNaturePresent() {
		return motherNaturePresent;
	}

	/**
	 * Method setMotherNaturePresent set mother nature presence on the island; if mother nature
	 * is inserted on the island and there aren't no entry tiles present on the island, this method
	 * calls the notifyObservers method of the super class; if mother nature is inserted on the island
	 * and there are no entry tiles this method removes one of them.
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
	 * @param noEntryTileManager the NoEntryTileManager from whom the no entry tile is taken
	 */
	public void insertNoEntryTile(NoEntryTileManager noEntryTileManager) {
		this.noEntryTilePresents++;
		if (this.noEntryTileManager == null) this.noEntryTileManager = noEntryTileManager;
	}

	//This method is private because a no entry tile can be removed only when mother nature
	//comes to this island
	private void removeNoEntryTile () {
		noEntryTileManager.insertNoEntryTile();
		noEntryTilePresents--;
	}

	public TowerType getTowerType() {
		return towerType;
	}

	public void setTowerType(TowerType towerType) {
		//game.firePropertyChange("IslandTower", this.towerType, towerType);
		this.towerType = towerType;
	}

	public void addStudents(boolean isFromEntrance, Student... students) {
		for (Student s: students) {
			addStudent(s);
		}
		game.firePropertyChange("IslandStudents", isFromEntrance, Arrays.stream(students)
				.map(Student::getStudentType).toList().toArray(new RealmType[0]));
	}

	public void addListener(PropertyChangeListener listener) {
		game.addPropertyChangeListener(listener);
	}

	public void restoreIsland(Game game) {
		addObserver(game);
		CharacterCard characterCard = game.getCharacterById(5);
		if (characterCard != null) noEntryTileManager = (Character5) characterCard;
	}
}
