package it.polimi.ingsw.model;

public class Player {
	private TowerType towerType;
	private int numCoins;
	private boolean isStarter;
	private String nickName;
	private School school;
	private Assistant[] assistants;

	public void doActionPhase() {

	}

	public void doPlanningPhase() {

	}

	public Cloud chooseCloud() {
		return null;
	}

	public boolean isStarter() {
		return false;
	}

	public School getSchool() {
		return null;
	}

}
