package it.polimi.ingsw.model;

public abstract class Island {
	private TowerType towerType;
	private boolean motherNaturePresent;
	private boolean entryTilePresent;
	private Student[] student;

	private Student[] students;

	public boolean isMotherNaturePresent() {
		return false;
	}

	public int getInfluence(Player p) {
		return 0;
	}

	public boolean isEntryTilePresent() {
		return false;
	}

	public void putTower(TowerType t) {

	}

}
