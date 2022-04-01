package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.NoEntryTileManager;
import it.polimi.ingsw.model.modelObservables.MotherNatureMovementObservable;

import java.util.List;

public abstract class Island extends MotherNatureMovementObservable {
	private TowerType towerType;
	private boolean motherNaturePresent;
	private int noEntryTilePresents;
	private NoEntryTileManager noEntryTileManager = null;
	public static final int NUM_ISLANDS = 12;

	//TODO: add an observer

	public Island() {
		super();
		motherNaturePresent = false;
		noEntryTilePresents = 0;
	}

	protected Island(TowerType towerType){
		this();
		this.towerType = towerType;
	}

	public abstract void putTower(TowerType t);

	public abstract List<Student> getStudents();

	public abstract int getNumStudentsOfType (RealmType studentType);

	public abstract void addStudent(Student s);

	public abstract int getNumTowers();

	public boolean isMotherNaturePresent() {
		return motherNaturePresent;
	}

	public void setMotherNaturePresent(boolean motherNaturePresent){
		this.motherNaturePresent = motherNaturePresent;
		if (noEntryTilePresents == 0 && motherNaturePresent) {
			notifyObservers(this);
		} else if (noEntryTilePresents > 0){
			removeNoEntryTile();
		}
	}

	public int getNoEntryTilePresents() {
		return noEntryTilePresents;
	}

	public void insertNoEntryTile(NoEntryTileManager noEntryTileManager) {
		this.noEntryTilePresents++;
		if (this.noEntryTileManager == null) this.noEntryTileManager = noEntryTileManager;
	}

	//TODO: handle the case with 0 no entry tiles
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
		this.towerType = towerType;
	}
}
