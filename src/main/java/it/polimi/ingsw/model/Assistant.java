package it.polimi.ingsw.model;

public class Assistant {
	private WizardType wizard;
	private int cardValue;
	private int motherNatureMovement;
	private boolean played = false;
	public static final int NUM_ASSISTANTS = 10;

	public int getCardValue() {
		return cardValue;
	}

	public int getMotherNatureMovement() {
		return motherNatureMovement;
	}

	public Assistant(int cardValue, int motherNatureMovement){
		this.cardValue = cardValue;
		this.motherNatureMovement = motherNatureMovement;
	}

	public boolean getPlayed() {
		return played;
	}

	public void setPlayed(boolean b) {
		played = b;
	}

}
