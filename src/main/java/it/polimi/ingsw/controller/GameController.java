package it.polimi.ingsw.controller;

import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * GameController is the main class of the controller package that handles all actions that a
 * client request by calling other controllers (TurnController and ActionController) methods.
 * The class implements the PropertyChangeListener interface to implement the Observer pattern with
 * the view (the RemoteView will fire events that will be passed to this class).
 */
public class GameController implements PropertyChangeListener{
	private TurnController turnController;
	private ActionController actionController;
	private final InitController initController;
	private Game game;
	private final boolean isExpertGame;
	private transient final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/**
	 * Constructs a new GameController instance that will control a game with the specified number of players and the
	 * specified rules.
	 *
	 * @param numPlayers the number of players of the game
	 * @param isExpertGame the rules of the game (true is for expert rules, false is for simple rules)
	 */
	public GameController(int numPlayers, boolean isExpertGame) {
		initController = new InitController(numPlayers, isExpertGame);
		this.isExpertGame = isExpertGame;
	}

	/**
	 * Returns true if the game is expert, otherwise false
	 * @return true if the game is expert, otherwise false
	 */
	public boolean isExpertGame() {
		return isExpertGame;
	}

	/**
	 * Officially starts the game after all setup operations are done by the InitController
	 */
	public void startGame() {
		game.start();
		handleEndPhase();
	}

	/**
	 * Returns the game that GameController is controlling
	 *
	 * @return the controlled model of the game
	 */
	public Game getModel() {
		return game;
	}

	/**
	 * Returns the TurnController of this object
	 *
	 * @return the TurnController of this object
	 */
	public TurnController getTurnController() {
		return turnController;
	}

	/**
	 * Returns the ActionController of this object
	 *
	 * @return the ActionController of this object
	 */
	public ActionController getActionController() {
		return actionController;
	}

	/**
	 * Returns the InitController of this object
	 *
	 * @return the InitController of this object
	 */
	public InitController getInitController() {
		return initController;
	}

	/**
	 * Set the game that this controller will control
	 *
	 * @param game the game associated to the controller
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Initialize ActionController and TurnController before the start of the game
	 */
	public void initializeControllers() {
		turnController = new TurnController(game.getPlayers().toArray(new Player[0]), game);
		actionController = new ActionController(this, turnController);
	}

	/**
	 * This method receives an event from the View that contains the action requested by a player: if the action requester
	 * is the correct active player the action is not an EndTurn action, the action is passed to the action controller.
	 *
	 * @param evt A PropertyChangeEvent object describing the event source
	 *          and the property that has changed.
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		MessageFromClient message = (MessageFromClient) evt.getNewValue();
		//Message have to be ACTION
		String nicknamePlayer = message.getClientMessageHeader().getNicknameSender();
		String actionName = message.getClientMessageHeader().getMessageName();
		if (!nicknamePlayer.equals(turnController.getActivePlayer().getNickName())) {
			listeners.firePropertyChange(new PropertyChangeEvent(nicknamePlayer, "Error",
					ErrorMessageType.ILLEGAL_TURN, "This isn't your turn."));
			return;
		}
		if (actionName.equals("EndTurn")) {
			boolean isTurnEnded = actionController.checkIfTurnIsEnded();
			if (!isTurnEnded) {
				listeners.firePropertyChange(new PropertyChangeEvent(nicknamePlayer, "Error",
						ErrorMessageType.TURN_NOT_FINISHED, "You can't end your turn now."));
				return;
			}
			listeners.firePropertyChange(new PropertyChangeEvent(nicknamePlayer, "Action", actionName, new TurnPhase[0]));
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
			listeners.firePropertyChange(new PropertyChangeEvent(turnStarter, "EndTurn", nicknamePlayer, newPossibleActions));
		} else {
			try {
				actionController.doAction(message);
				TurnPhase[] newPossibleActions = actionController.getCurrentTurnRemainingActions().toArray(new TurnPhase[0]);
				PropertyChangeEvent event =
						new PropertyChangeEvent(nicknamePlayer, "Action", actionName, newPossibleActions);
				listeners.firePropertyChange(event);
			} catch (IllegalArgumentException e) {
				//TODO: decide if it has to handled here or directly in the action controller class;
				//	the exception will be transformed in an error message
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles the end of the current phase (after the last player have ended his turn); if the new phase is a planning
	 * phase, the controller checks if the game is ended (if not clouds are refilled and players are notified about the
	 * refill). The method also notifies players (if the game is not ended) that a new phase is started.
	 */
	private void handleEndPhase() {
		if (turnController.getCurrentPhase() == RoundPhase.PLANNING && game.isLastRound()) {
			game.checkWinners();
			return;
		} else if (turnController.getCurrentPhase() == RoundPhase.PLANNING){
			actionController.refillClouds();
			Cloud[] clouds = game.getClouds();
			RealmType[][] cloudsStudents = new RealmType[clouds.length][];
			for (int i = 0; i < clouds.length; i++) {
				cloudsStudents[i] = Arrays.stream(clouds[i].getStudents()).map(Student::getStudentType).toList().toArray(new RealmType[0]);
			}
			listeners.firePropertyChange("CloudsRefill", null, cloudsStudents);
			for (Player p: game.getPlayers()) {
				Integer[] assistantValues = p.getAssistants().stream().map(Assistant::getCardValue).toList().toArray(new Integer[0]);
				Integer[] motherNatureMovements =
						p.getAssistants().stream().map(Assistant::getMotherNatureMovement).toList().toArray(new Integer[0]);
				listeners.firePropertyChange(new PropertyChangeEvent(p.getNickName(),
						"AssistantsUpdate", assistantValues, motherNatureMovements));
			}
		}
		//Notify change phase
		TurnPhase[] newPossibleActions = actionController.getCurrentTurnRemainingActions().toArray(new TurnPhase[0]);
		listeners.firePropertyChange(new PropertyChangeEvent(turnController.getActivePlayer().getNickName(), "EndPhase",
				turnController.getCurrentPhase(), newPossibleActions));
	}

	/**
	 * Sets the first action that the action controller will handle when a request come from a player (the action
	 * controller will use it to know if the player is requesting the correct action). This is done at the beginning of
	 * any new turn.
	 * @param currentRoundPhase the current round phase
	 */
	public void setStartingTurnPhase(RoundPhase currentRoundPhase) {
		if (currentRoundPhase == RoundPhase.ACTION) {
			actionController.setTurnPhase(TurnPhase.MOVE_STUDENTS);
		} else {
			actionController.setTurnPhase(TurnPhase.PLAY_ASSISTANT);
		}
	}

	/**
	 * Creates all listeners that will notify events to the client. The method creates all listeners that will listen to
	 * controllers classes (these listeners are the AcknowledgementDispatcher, the ErrorDispatcher, the TurnListener and
	 * the PlayerSetupListener). The method calls also the corresponding method in the game that will create all
	 * listeners that will listen to model objects
	 * @param views all the RemoteView objects of the players
	 * @param lobby the GameLobby that supports the game
	 */
	public void createListeners(List<RemoteView> views, GameLobby lobby) {
		ErrorDispatcher errorDispatcher = new ErrorDispatcher(views);
		AcknowledgementDispatcher ackDispatcher = new AcknowledgementDispatcher(views, lobby);
		listeners.addPropertyChangeListener("Error", errorDispatcher);
		initController.addListener("Error", errorDispatcher);
		actionController.addListener("Error", errorDispatcher);
		actionController.getCharacterController().addListener("Error", errorDispatcher);
		listeners.addPropertyChangeListener("Action", ackDispatcher);
		initController.addListener("Setup", ackDispatcher);
		game.createListeners(views, lobby);
		for(RemoteView view: views) {
			TurnListener turnListener = new TurnListener(view);
			listeners.addPropertyChangeListener("EndTurn", turnListener);
			listeners.addPropertyChangeListener("EndPhase", turnListener);
			listeners.addPropertyChangeListener("AssistantsUpdate", turnListener);
			listeners.addPropertyChangeListener("CloudsRefill", turnListener);
			PlayerSetupListener playerListener = new PlayerSetupListener(view);
			initController.addListener("Tower", playerListener);
			initController.addListener("Wizard", playerListener);
		}
	}
}
