package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CoinAlreadyPresentException;

public abstract class CharacterCard {
	private int coinPrice;
	private boolean coinPresent;
	public static final int NUM_CHARACTERS_PER_GAME = 3;
	public static final int NUM_CHARACTERS = 12;

	public CharacterCard (int coinPrice){
		this.coinPrice = coinPrice;
		coinPresent = false;
	}

	public int getPrice(){
		return coinPrice;
	}

	//only the first time the card is played it is allowed to put a coin
	public void putCoin() throws CoinAlreadyPresentException{
		if (coinPresent) throw new CoinAlreadyPresentException();
		coinPresent = true;
		coinPrice++;
	}

	public abstract void playCard(Player player);
}
