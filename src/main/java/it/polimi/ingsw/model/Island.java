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

	public abstract int getInfluence(Player p);


	public boolean isEntryTilePresent() {
		return false;
	}

	public void setEntryTilePresent(boolean entryTilePresent) {
		this.entryTilePresent = entryTilePresent;
	}

	public abstract void putTower(TowerType t);

	public TowerType getTowerType(){
		return towerType;
	}

}
