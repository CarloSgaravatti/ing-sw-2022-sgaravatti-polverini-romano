package it.polimi.ingsw.controller;

import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class GameController implements PropertyChangeListener {
	private TurnController turnController;
	private ActionController actionController;
	private final InitController initController;
	private Game game;
	private final int gameId;
	private final boolean isExpertGame;
	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

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
		//TODO
		//listeners.firePropertyChange("EndPhase", turnController.getCurrentPhase(), turnController.getActivePlayer().getNickName());
		handleEndPhase();
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		MessageFromClient message = (MessageFromClient) evt.getNewValue();
		//Message have to be ACTION
		String nicknamePlayer = message.getClientMessageHeader().getNicknameSender();
		String actionName = message.getClientMessageHeader().getMessageName();
		if (!nicknamePlayer.equals(turnController.getActivePlayer().getNickName())) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_TURN, nicknamePlayer);
			return;
		}
		if (actionName.equals("EndTurn")) {
			boolean isTurnEnded = actionController.checkIfTurnIsEnded();
			if (!isTurnEnded) {
				listeners.firePropertyChange("Error", ErrorMessageType.TURN_NOT_FINISHED, nicknamePlayer);
				return;
			}
			boolean isPhaseEnded = turnController.endTurn();
			actionController.resetPossibleActions(turnController.getCurrentPhase());
			game.setIndexActivePlayer(turnController.getActivePlayer());
			setStartingTurnPhase(turnController.getCurrentPhase());
			if (isPhaseEnded) {
				handleEndPhase();
				return;
			}
			//notify new turn
			String turnStarter = turnController.getActivePlayer().getNickName();
			TurnPhase[] newPossibleActions = actionController.getCurrentTurnRemainingActions().toArray(new TurnPhase[0]);
			//listeners.firePropertyChange("EndTurn", nicknamePlayer, turnStarter);
			listeners.firePropertyChange(new PropertyChangeEvent(turnStarter, "EndTurn", nicknamePlayer, newPossibleActions));
		} else {
			try {
				actionController.doAction(message);
				TurnPhase[] newPossibleActions = actionController.getCurrentTurnRemainingActions().toArray(new TurnPhase[0]);
				//TODO: update possible actions
				PropertyChangeEvent event =
						new PropertyChangeEvent(nicknamePlayer, "Action", actionName, newPossibleActions);
				//listeners.firePropertyChange("Action", nicknamePlayer, actionName);
				listeners.firePropertyChange(event);
			} catch (Exception e) {
				//TODO: decide if it has to handled here or directly in the action controller class;
				//	the exception will be transformed in an error message
			}
		}
	}

	private void handleEndPhase() {
		if (turnController.getCurrentPhase() == RoundPhase.ACTION) {
			actionController.refillClouds();
		} else if (game.isLastRound()) {
			//notify game finished
			game.checkWinners();
			return;
		}
		//Notify change phase
		TurnPhase[] newPossibleActions = actionController.getCurrentTurnRemainingActions().toArray(new TurnPhase[0]);
		//listeners.firePropertyChange("EndPhase", turnController.getCurrentPhase(), turnController.getActivePlayer().getNickName());
		listeners.firePropertyChange(new PropertyChangeEvent(turnController.getActivePlayer().getNickName(), "EndPhase",
				turnController.getCurrentPhase(), newPossibleActions));
	}

	public void setStartingTurnPhase(RoundPhase currentRoundPhase) {
		if (currentRoundPhase == RoundPhase.ACTION) {
			actionController.setTurnPhase(TurnPhase.MOVE_STUDENTS);
		} else {
			actionController.setTurnPhase(TurnPhase.PLAY_ASSISTANT);
		}
	}

	public void createListeners(List<RemoteView> views, GameLobby lobby) {
		ErrorDispatcher errorDispatcher = new ErrorDispatcher(views);
		AcknowledgementDispatcher ackDispatcher = new AcknowledgementDispatcher(views);
		listeners.addPropertyChangeListener("Error", errorDispatcher);
		initController.addListener("Error", errorDispatcher);
		actionController.addListener("Error", errorDispatcher);
		listeners.addPropertyChangeListener("Action", ackDispatcher);
		initController.addListener("Setup", ackDispatcher);
		game.createListeners(views, lobby);
		for(RemoteView view: views) {
			TurnListener turnListener = new TurnListener(view);
			listeners.addPropertyChangeListener("EndTurn", turnListener);
			listeners.addPropertyChangeListener("EndPhase", turnListener);
			PlayerSetupListener playerListener = new PlayerSetupListener(view);
			initController.addListener("Tower", playerListener);
			initController.addListener("Wizard", playerListener);
		}
	}
}
