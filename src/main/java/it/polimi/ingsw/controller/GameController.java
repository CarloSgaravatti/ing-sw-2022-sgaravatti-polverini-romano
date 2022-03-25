package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;

public class GameController {
	private TurnController turnController;
	private ActionController actionController;
	private InitController initController;

	private Game game;

	public void startGame() {
		game.start();
	}

	public void endGame() {

	}

	public void declareWinner() {

	}

	public Game getModel() {
		return game;
	}

	public TurnController getTurnController() {
		return turnController;
	}

	public ActionController getActionController() {
		return actionController;
	}

	public void setGame(){
		this.game=initController.getGame();
	}

}
