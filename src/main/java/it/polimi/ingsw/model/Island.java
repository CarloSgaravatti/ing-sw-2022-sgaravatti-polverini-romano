package it.polimi.ingsw.model;

import it.polimi.ingsw.model.modelObservables.MotherNatureMovementObservable;

import java.util.List;

public abstract class Island extends MotherNatureMovementObservable {
	private TowerType towerType;
	private boolean motherNaturePresent;
	private boolean noEntryTilePresent;
	public static final int NUM_ISLANDS = 12;

	public Island() {
		motherNaturePresent = false;
		noEntryTilePresent = false;
	}

	protected Island(TowerType towerType){
		this();
		this.towerType = towerType;
	}

	public abstract int getInfluence(Player p);

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
		if (!noEntryTilePresent && motherNaturePresent) {
			notifyObservers(this);
		}
		//TODO: gestire il caso con no entry tiles
	}

	public boolean isNoEntryTilePresent() {
		return noEntryTilePresent;
	}

	public void setNoEntryTilePresent(boolean noEntryTilePresent) {
		this.noEntryTilePresent = noEntryTilePresent;
	}

	public TowerType getTowerType() {
		return towerType;
	}

	public void setTowerType(TowerType towerType) {
		this.towerType = towerType;
	}
}
