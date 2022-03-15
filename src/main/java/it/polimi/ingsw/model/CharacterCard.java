package it.polimi.ingsw.model;

public abstract class CharacterCard {
	private int coinPrice;
	private boolean coinPresent;

	public int getPrice(){
		return coinPrice;
	}

	//put coins on card
	public void putcoin(){}

	public abstract void playCard(Player player);

}
