package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

public class GameController {
	private TurnController turnController;
	private ActionController actionController;
	private InitController initController;
	private Game game;

	public GameController() {
		initController = new InitController();
	}

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

	public InitController getInitController() {
		return initController;
	}

	public void setGame(){
		this.game = initController.getGame();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void initializeTurnController() {
		turnController = new TurnController(game.getPlayers().toArray(new Player[0]), game);
	}

}
