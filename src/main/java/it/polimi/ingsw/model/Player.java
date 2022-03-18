package it.polimi.ingsw.model;

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

	public void doPlanningPhase() {

	}

	public void chooseCloud(Cloud cloud) {

	}

	public boolean isStarter() {
		return starter;
	}

	public void setSchool(School s){
		this.school = s;
	}

	public School getSchool() {
		return school;
	}

	public void playCard() {
		System.out.println("Chose the assistant you want to play: ");
		Scanner s1 = new Scanner(System.in);
		int ass = s1.nextInt();
		while(true){
			if(assistants[ass-1].getPlayed()==false){
				// non posso farlo deve essere fatto dentro round
			}
		}
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
}
