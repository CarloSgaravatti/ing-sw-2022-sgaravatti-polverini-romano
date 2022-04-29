package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.util.EventListener;

public class GameController implements EventListener {
	private TurnController turnController;
	private ActionController actionController;
	private final InitController initController;
	private Game game;
	private final int gameId;
	private final boolean isExpertGame;

	public GameController(int gameId, int numPlayers, boolean isExpertGame) {
		initController = new InitController(numPlayers, isExpertGame);
		this.gameId = gameId;
		this.isExpertGame = isExpertGame;
	}

	public boolean isExpertGame() {
		return isExpertGame;
	}

	public void startGame() {
		game.start();
	}

	public void endGame() {

	}

	public String declareWinner() {
		return null;
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

	public void setGame() {
		this.game = initController.getGame();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void initializeControllers() {
		turnController = new TurnController(game.getPlayers().toArray(new Player[0]), game);
		actionController = new ActionController(this, turnController);
	}

	//TODO: event performed
	//TODO: set index active player in game
	public void eventPerformed(MessageFromClient message) {
		//Message have to be ACTION
		String nicknamePlayer = message.getClientMessageHeader().getNicknameSender();
		if (!nicknamePlayer.equals(turnController.getActivePlayer().getNickName())) {
			//TODO: error message (invalid action request)
			return;
		}
		//Message start turn?
		if (message.getClientMessageHeader().getMessageName().equals("EndTurn")) {
			turnController.endTurn();
			game.setIndexActivePlayer(turnController.getActivePlayer());
			//TODO: notify new turn or round
			return;
		}
		try {
			actionController.doAction(message);
		} catch (Exception e) {
			//TODO: decide if it has to handled here or directly in the action controller class;
			//	the exception will be transformed in an error message
		}
	}
}
