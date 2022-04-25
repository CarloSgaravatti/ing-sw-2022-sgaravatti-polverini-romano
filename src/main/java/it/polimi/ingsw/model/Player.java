package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.enumerations.WizardType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
	private int numCoins;
	private final String nickName;
	private School school;
	private List<Assistant> assistants;
	private final TurnEffect turnEffect;
	private WizardType wizardType;

	public Player(String nickName){
		numCoins = 1;
		//starter = false;
		this.nickName = nickName;
		turnEffect = new TurnEffect();
		assistants = new ArrayList<>();
		wizardType = null;
	}

	public void pickFromCloud(Cloud cloud) throws EmptyCloudException{
		school.insertEntrance(cloud.pickStudents());
	}

	public void setSchool(School s){
		this.school = s;
	}

	public School getSchool() {
		return school;
	}

	public boolean playAssistant(int assistant, List<Integer> assistantPlayed) throws NoSuchAssistantException {
		boolean isFirstPlayedAssistant = true;
		if (assistantPlayed.contains(assistant)) {
			isFirstPlayedAssistant = false;
			for (Assistant a: assistants) {
				if (!assistantPlayed.contains(a.getCardValue())) return false;
			}
		}
		for (int i = 0; i < assistants.size(); i++) {
			if (assistants.get(i).getCardValue() == assistant) {
				turnEffect.setOrderPrecedence(assistant);
				turnEffect.incrementMotherNatureMovement(assistants.get(i).getMotherNatureMovement());
				turnEffect.setFirstPlayedAssistant(isFirstPlayedAssistant);
				assistants.remove(i);
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

	public TurnEffect getTurnEffect() {
		return turnEffect;
	}

	public void resetTurnEffect() {
		turnEffect.reset();
	}

	public void insertCoin () {
		numCoins++;
	}

	public void removeCoins(int coins) throws NotEnoughCoinsException{
		if (numCoins < coins) throw new NotEnoughCoinsException();
		numCoins -= coins;
	}

	public void setWizardType(WizardType wizardType){
		this.wizardType = wizardType;
	}

	public WizardType getWizardType(){
		return this.wizardType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player player)) return false;
		return Objects.equals(nickName, player.nickName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nickName);
	}
}
