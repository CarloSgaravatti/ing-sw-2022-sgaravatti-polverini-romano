package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CoinAlreadyPresentException;

import java.util.Objects;

public abstract class CharacterCard {
	private final int id;
	private int coinPrice;
	private boolean coinPresent;
	public static final int NUM_CHARACTERS_PER_GAME = 3;
	public static final int NUM_CHARACTERS = 12;

	public CharacterCard (int coinPrice, int id){
		this.coinPrice = coinPrice;
		this.id = id;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CharacterCard)) return false;
		CharacterCard that = (CharacterCard) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
