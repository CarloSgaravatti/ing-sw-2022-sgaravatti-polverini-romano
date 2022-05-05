package it.polimi.ingsw.controller;

import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.server.RemoteView;

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.List;

public class GameController implements EventListener {
	private TurnController turnController;
	private ActionController actionController;
	private final InitController initController;
	private Game game;
	private final int gameId;
	private final boolean isExpertGame;
	private final EventListenerList listenerList = new EventListenerList();

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
		fireEndPhaseEvent(turnController.getCurrentPhase(), turnController.getActivePlayer().getNickName());
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

	public synchronized void eventPerformed(MessageFromClient message) {
		//Message have to be ACTION
		String nicknamePlayer = message.getClientMessageHeader().getNicknameSender();
		String actionName = message.getClientMessageHeader().getMessageName();
		if (!nicknamePlayer.equals(turnController.getActivePlayer().getNickName())) {
			fireErrorEvent(ErrorMessageType.ILLEGAL_TURN, nicknamePlayer);
			return;
		}
		//Message start turn?
		if (actionName.equals("EndTurn")) {
			boolean isTurnEnded = actionController.checkIfTurnIsEnded();
			if (!isTurnEnded) {
				fireErrorEvent(ErrorMessageType.TURN_NOT_FINISHED, nicknamePlayer);
				return;
			}
			boolean isPhaseEnded = turnController.endTurn();
			game.setIndexActivePlayer(turnController.getActivePlayer());
			if (isPhaseEnded) {
				handleEndPhase();
				return;
			}
			//notify new turn
			String turnStarter = turnController.getActivePlayer().getNickName();
			fireEndTurnEvent(nicknamePlayer, turnStarter);
		} else {
			try {
				actionController.doAction(message);
				fireAckEvent(nicknamePlayer, actionName);
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
			game.fireEndGameEvent();
		}
		//Notify change phase
		fireEndPhaseEvent(turnController.getCurrentPhase(), turnController.getActivePlayer().getNickName());
	}

	public void addListener(ErrorDispatcher errorListener) {
		listenerList.add(ErrorDispatcher.class, errorListener);
	}

	public void addListener(TurnListener listener) {
		listenerList.add(TurnListener.class, listener);
	}

	public void addListener(AcknowledgementDispatcher dispatcher) {
		listenerList.add(AcknowledgementDispatcher.class, dispatcher);
	}

	protected void fireAckEvent(String nicknameToAcknowledge, String actionName) {
		for (AcknowledgementDispatcher ackDispatcher: listenerList.getListeners(AcknowledgementDispatcher.class)) {
			ackDispatcher.confirmActionPerformed(nicknameToAcknowledge, actionName);
		}
	}

	protected void fireErrorEvent(ErrorMessageType error, String nickname) {
		for (ErrorDispatcher errorDispatcher: listenerList.getListeners(ErrorDispatcher.class)) {
			errorDispatcher.onErrorEvent(error, nickname);
		}
	}

	protected void fireEndPhaseEvent(RoundPhase newPhase, String starter) {
		for (TurnListener listener: listenerList.getListeners(TurnListener.class)) {
			listener.endPhaseEventPerformed(newPhase, starter);
		}
	}

	protected void fireEndTurnEvent(String turnEnder, String turnStarter) {
		for (TurnListener listener: listenerList.getListeners(TurnListener.class)) {
			listener.endTurnEventPerformed(turnEnder, turnStarter);
		}
	}

	public void createListeners(List<RemoteView> views, GameLobby lobby) {
		ErrorDispatcher errorDispatcher = new ErrorDispatcher(views);
		AcknowledgementDispatcher ackDispatcher = new AcknowledgementDispatcher(views);
		addListener(errorDispatcher);
		actionController.addListener(errorDispatcher);
		addListener(ackDispatcher);
		initController.addListener(ackDispatcher);
		game.addEventListener(new EndGameListener(lobby));
		for(RemoteView view: views) {
			addListener(new TurnListener(view));
			PlayerListener playerListener = new PlayerListener(view);
			initController.addEventListener(playerListener);
			actionController.addEventListener(playerListener);
			actionController.addEventListener(new CloudListener(view));
			actionController.addEventListener(new CharacterListener(view));
			game.addEventListener(new IslandListener(view));
			game.addEventListener(new SchoolListener(view));
			game.addEventListener(new MotherNatureListener(view));
		}
	}
}
