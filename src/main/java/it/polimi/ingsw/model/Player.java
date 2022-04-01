package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.NoSuchAssistantException;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private TowerType towerType; //useless
	private int numCoins;
	private boolean starter;
	private final String nickName;
	private School school;
	private List<Assistant> assistants;
	private final TurnEffect turnEffect;
	private boolean selected;

	public Player(String nickName){
		numCoins = 1;
		starter = false;
		this.nickName = nickName;
		turnEffect = new TurnEffect();
		assistants = new ArrayList<>();
	}

	public void pickFromCloud(Cloud cloud) throws EmptyCloudException{
		school.insertEntrance(cloud.pickStudents());
	}

	public boolean isStarter() {
		return starter;
	}

	public void setStarter(boolean starter){
		this.starter = starter;
	}

	public void setSchool(School s){
		this.school = s;
	}

	public School getSchool() {
		return school;
	}

	public boolean playAssistant(int assistant, List<Integer> assistantPlayed) throws NoSuchAssistantException {
		if (assistantPlayed.contains(assistant)) {
			for (Assistant a: assistants) {
				if (!assistantPlayed.contains(a.getCardValue())) return false;
			}
		}
		for (int i = 0; i < assistants.size(); i++) {
			if (assistants.get(i).getCardValue() == assistant) {
				assistants.remove(i);
				turnEffect.setOrderPrecedence(assistant);
				turnEffect.incrementMotherNatureMovement(assistants.get(i).getMotherNatureMovement());
				return true;
			}
		}
		throw new NoSuchAssistantException();
	}

	public void setAssistants(List<Assistant> assistants){
		this.assistants = assistants;
	}

	public int getNumCoins() {
		return numCoins;
	}

	public List<Assistant> getAssistants() {
		return assistants;
	}

	public String getNickName() {
		return nickName;
	}

	public void setTowerType(TowerType type){
		this.towerType = type;
	}

	public TurnEffect getTurnEffect() {
		return turnEffect;
	}

	public void resetTurnEffect() {
		turnEffect.reset();
	}

	public void insertCoin () {
		numCoins++;
	}

	//TODO: handle the case in which the player doesn't have enough coins
	//TODO: put coins in game general supply
	public void removeCoins(int coins) {
		numCoins -= coins;
	}

	public int getCardValue() {
		return turnEffect.getOrderPrecedence();
	}

	public boolean getSelected() {
		return selected;
	}
	public void setSelected() {
		this.selected = true;
	}
}
