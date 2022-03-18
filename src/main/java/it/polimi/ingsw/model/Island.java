package it.polimi.ingsw.model;

import java.util.List;

public abstract class Island {
	private TowerType towerType;
	private boolean motherNaturePresent;
	private boolean entryTilePresent;
	public static final int NUM_ISLANDS = 12;

	public Island() {
		motherNaturePresent = false;
		entryTilePresent = false;
	}

	protected Island(TowerType towerType){
		this();
		this.towerType = towerType;
	}

	public abstract int getInfluence(Player p);

	public abstract void putTower(TowerType t);

	public abstract List<Student> getStudents();

	public abstract void addStudent(Student s);

	public boolean isMotherNaturePresent() {
		return motherNaturePresent;
	}

	public void setMotherNaturePresent(boolean motherNaturePresent){
		this.motherNaturePresent = motherNaturePresent;
	}

	public boolean isEntryTilePresent() {
		return entryTilePresent;
	}

	public void setEntryTilePresent(boolean entryTilePresent) {
		this.entryTilePresent = entryTilePresent;
	}

	public TowerType getTowerType() {
		return towerType;
	}

	public void setTowerType(TowerType towerType) {
		this.towerType = towerType;
	}
}
