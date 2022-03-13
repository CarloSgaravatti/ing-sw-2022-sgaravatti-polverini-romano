package it.polimi.ingsw.model;

public abstract class Island {
	private TowerType towerType;
	private boolean motherNaturePresent;
	private boolean entryTilePresent;
	private Student[] student;


	public boolean isMotherNaturePresent() {
		return motherNaturePresent;
	}

	public void setMotherNaturePresent(boolean motherNaturePresent){}

	public int getInfluence(Player p) {
		return 0;
	}

	public boolean isEntryTilePresent() {
		return false;
	}

	public void putTower(TowerType t) {

	}

}
