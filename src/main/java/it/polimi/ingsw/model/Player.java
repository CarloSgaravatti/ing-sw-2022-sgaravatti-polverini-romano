package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;

import java.util.Scanner;

public class Player {
	private TowerType towerType;
	private int numCoins;
	private boolean starter;
	private String nickName;
	private School school;
	private Assistant[] assistants;

	public Player(String nickName){
		numCoins = 0;
		starter = false;
		this.nickName = nickName;
	}

	public void doActionPhase() {

	}

	public void doPlanningPhase() throws EmptyCloudException {

	}

	public void chooseCloud(Cloud cloud) {

	}

	public boolean isStarter() {
		return starter;
	}

	public void setStarter(boolean bol){
		this.starter=starter;
	}

	public void setSchool(School s){
		this.school = s;
	}

	public School getSchool() {
		return school;
	}

	public boolean playCard(int ass/*numero scelto dato dalla view*/, Round round) {
			// metto ass-1 perchè cosi la persona ha i numeri da scegliere da 1 a 12 inclusi
		if(!assistants[ass-1].getPlayed()){
			if(round.checkPlayedCardForAllPlayer(ass-1, nickName) != 0){
				assistants[ass-1].setPlayed(true);
				return true;
			}
		}
		return false;
	}
	public void setAssistants(Assistant[] assistants){
		this.assistants=assistants;
	}

	public TowerType getTowerType() {
		return towerType;
	}

	public int getNumCoins() {
		return numCoins;
	}

	public Assistant[] getAssistants() {
		return assistants;
	}

	public String getNickName() {
		return nickName;
	}
}
