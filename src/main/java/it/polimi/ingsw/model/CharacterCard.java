package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CoinAlreadyPresentException;

public abstract class CharacterCard {
	private int coinPrice;
	private boolean coinPresent;

	public CharacterCard (int coinPrice){
		this.coinPrice = coinPrice;
		coinPresent = false;
	}

	public int getPrice(){
		return coinPrice;
	}

	//put coins on card
	public void putCoin() throws CoinAlreadyPresentException{
		if (coinPresent) throw new CoinAlreadyPresentException();
		coinPresent = true;
		coinPrice++;
	}

	public abstract void playCard(Player player);
}
