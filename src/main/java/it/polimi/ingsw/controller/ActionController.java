package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.*;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.JsonUtils;
import it.polimi.ingsw.utils.Pair;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ActionController {
	private TurnPhase turnPhase;
	private final TurnController turnController;
	private final GameController gameController;
	private final List<String> possibleActions;
	private final List<TurnPhase> currentTurnRemainingActions = new ArrayList<>();
	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public ActionController(GameController gameController, TurnController turnController) {
		this.gameController = gameController;
		this.turnController = turnController;
		turnPhase = TurnPhase.PLAY_ASSISTANT;
		possibleActions = JsonUtils.getRulesByDifficulty(gameController.isExpertGame());
		currentTurnRemainingActions.add(TurnPhase.PLAY_ASSISTANT);
	}

	public void addListener(String propertyName, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}

	//TODO: these methods should return a message to the client and not an exception
	public void doAction(MessageFromClient message) throws IllegalArgumentException, AssistantAlreadyPlayedException,
			StudentNotFoundException, NoSuchAssistantException, EmptyCloudException, FullDiningRoomException,
			NotEnoughCoinsException, IllegalCharacterActionRequestedException, WrongTurnActionRequestedException {
		String messageName = message.getClientMessageHeader().getMessageName();
		MessagePayload payload = message.getMessagePayload();
		if (!possibleActions.contains(messageName)) throw new IllegalArgumentException();
		switch (messageName) {
			case "PlayAssistant" -> playAssistant(payload);
			case "MoveMotherNature" -> motherNatureMovement(payload);
			case "MoveStudents" -> studentMovement(payload);
			case "PickFromCloud" -> pickStudentsFromClouds(payload);
			case "PlayCharacter" -> playCharacter(payload);
			case "CharacterEffect" -> characterEffect(payload);
			default -> throw new IllegalArgumentException();
		}
	}

	public void playAssistant(MessagePayload payload) throws AssistantAlreadyPlayedException,
			NoSuchAssistantException, WrongTurnActionRequestedException, ClassCastException {
		String playerName = turnController.getActivePlayer().getNickName();
		if (turnPhase != TurnPhase.PLAY_ASSISTANT) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_TURN_ACTION, playerName);
			//TODO: remove the exception
			throw new WrongTurnActionRequestedException();
		}
		int assistantIdx = payload.getAttribute("Assistant").getAsInt();
		List<Player> players = gameController.getModel().getPlayers();
		List<Integer> assistantAlreadyPlayed = new ArrayList<>();
		for (Player p: players) {
			int assistant = p.getTurnEffect().getOrderPrecedence();
			//assistant == 0 indicates that the player has not already played an assistant
			if (assistant != 0) {
				assistantAlreadyPlayed.add(assistant);
			}
		}
		if (!turnController.getActivePlayer().playAssistant(assistantIdx, assistantAlreadyPlayed)){
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, playerName);
			throw new AssistantAlreadyPlayedException();
		}
		currentTurnRemainingActions.remove(TurnPhase.PLAY_ASSISTANT);
		setTurnPhase(TurnPhase.TURN_ENDED); //planning phase is ended for this player
		if (turnController.getActivePlayer().getAssistants().size() == 0) {
			gameController.getModel().setLastRound(true);
		}
	}

	public void motherNatureMovement(MessagePayload payload) throws IllegalArgumentException,
			WrongTurnActionRequestedException, ClassCastException {
		if (turnPhase != TurnPhase.MOVE_MOTHER_NATURE) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_TURN_ACTION, turnController.getActivePlayer().getNickName());
			throw new WrongTurnActionRequestedException();
		}
		int motherNatureMovement = payload.getAttribute("MotherNature").getAsInt();
		int legalMotherNatureMovement = turnController.getActivePlayer().getTurnEffect().getMotherNatureMovement();
		if (motherNatureMovement <= 0 || motherNatureMovement > legalMotherNatureMovement) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException(); //TODO: create a specific exception
		}
		gameController.getModel().moveMotherNature(motherNatureMovement);
		currentTurnRemainingActions.remove(TurnPhase.MOVE_MOTHER_NATURE);
		setTurnPhase(TurnPhase.SELECT_CLOUD);
	}

	//Use of ? to not have unchecked warning (maybe we should change the message format to not use generics)
	//TODO: maybe this method can be done better
	public void studentMovement(MessagePayload payload) throws IllegalArgumentException, StudentNotFoundException,
			FullDiningRoomException, WrongTurnActionRequestedException, ClassCastException {
		if (turnPhase != TurnPhase.MOVE_STUDENTS){
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_TURN_ACTION, turnController.getActivePlayer().getNickName());
			throw new WrongTurnActionRequestedException();
		}
		List<?> toDiningRoom = (List<?>) payload.getAttribute("StudentsToDR").getAsObject();
		List<?> toIslands = new ArrayList<>((List<?>) payload.getAttribute("StudentsToIslands").getAsObject());
		if (toDiningRoom.size() + toIslands.size() > 3){
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		School school = turnController.getActivePlayer().getSchool();
		for (Object studentType: toDiningRoom) {
			if (school.moveFromEntranceToDiningRoom((RealmType) studentType)) {
				turnController.getActivePlayer().insertCoin();
				gameController.getModel().takeCoinFromGeneralSupply();
			}
		}
		while (!toIslands.isEmpty()) {
			Pair<?, ?> pairStudentIsland = (Pair<?, ?>) toIslands.get(0);
			int islandIndex = (Integer) pairStudentIsland.getSecond();
			if (!isValidIsland(islandIndex)){
				listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
				throw new IllegalArgumentException();
			}
			List<RealmType> studentsToIsland = new ArrayList<>();
			studentsToIsland.add((RealmType) pairStudentIsland.getFirst());
			toIslands.remove(pairStudentIsland);
			List<Pair<?, ?>> toRemove = new ArrayList<>();
			for (Object toIsland : toIslands) {
				Pair<?, ?> pair = (Pair<?, ?>) toIsland;
				if ((Integer) pair.getSecond() == islandIndex) {
					studentsToIsland.add((RealmType) pair.getFirst());
					toRemove.add(pair);
				}
			}
			for (Object o: toRemove) toIslands.remove(o);
			Island island = gameController.getModel().getIslands().get(islandIndex);
			school.sendStudentToIsland(island, studentsToIsland.toArray(new RealmType[0]));
		}
		currentTurnRemainingActions.remove(TurnPhase.MOVE_STUDENTS);
		setTurnPhase(TurnPhase.MOVE_MOTHER_NATURE);
	}

	public void pickStudentsFromClouds(MessagePayload payload) throws EmptyCloudException, WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.SELECT_CLOUD){
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_TURN_ACTION, turnController.getActivePlayer().getNickName());
			throw new WrongTurnActionRequestedException();
		}
		int cloudIndex = payload.getAttribute("Cloud").getAsInt();
		Cloud cloud = gameController.getModel().getClouds()[cloudIndex];
		turnController.getActivePlayer().pickFromCloud(cloud);
		currentTurnRemainingActions.remove(TurnPhase.SELECT_CLOUD);
		setTurnPhase(TurnPhase.TURN_ENDED);
	}

	public void playCharacter(MessagePayload payload) throws IllegalArgumentException,
			NotEnoughCoinsException, IllegalCharacterActionRequestedException, ClassCastException {
		Player activePlayer = turnController.getActivePlayer();
		//TODO: illegal argument exception is too general
		if (activePlayer.getTurnEffect().isCharacterPlayed()) {
			listeners.firePropertyChange("Error", ErrorMessageType.CHARACTER_ALREADY_PLAYED, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
        }
		int characterId = payload.getAttribute("CharacterId").getAsInt();
		CharacterCard characterCard = gameController.getModel().getCharacterById(characterId);
		if (characterCard == null) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
        }
		int coinToGeneralSupply = characterCard.getPrice();
		//TODO: if player does not have enough coins this is not correct
		if (!characterCard.isCoinPresent()) coinToGeneralSupply--;
		characterCard.playCard(turnController.getActivePlayer());
		activePlayer.getTurnEffect().setCharacterPlayed(true);
		activePlayer.getTurnEffect().setCharacterEffectConsumed(false);
		gameController.getModel().insertCoinsInGeneralSupply(coinToGeneralSupply);
		currentTurnRemainingActions.remove(TurnPhase.PLAY_CHARACTER_CARD);
		characterEffect(payload);
	}

	public void characterEffect(MessagePayload payload) throws IllegalCharacterActionRequestedException {
		List<?> payloadArgs = (List<?>) payload.getAttribute("Arguments").getAsObject();
		if (payloadArgs.size() == 0) return;
		List<String> args = new ArrayList<>();
		for (Object payloadArg : payloadArgs) {
			args.add((String) payloadArg);
		}
		if (turnController.getActivePlayer().getTurnEffect().isCharacterEffectConsumed()) {
            //TODO: check if "ILLEGAL ARGUMENT" is correct in this case
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
            throw new IllegalArgumentException();
        }
		int characterId = payload.getAttribute("CharacterId").getAsInt();
		CharacterCard characterCard = gameController.getModel().getCharacterById(characterId);
		if (characterCard == null) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
            throw new IllegalArgumentException();
        }
		characterCard.useEffect(args.subList(1, args.size()));
	}

	public void refillClouds() {
		Cloud[] clouds = gameController.getModel().getClouds();
		try {
			for (Cloud c : clouds) {
				Student[] students = new Student[gameController.getModel().getGameConstants().getNumStudentsPerCloud()];
				for (int i = 0; i < students.length; i++) {
					students[i] = gameController.getModel().getBag().pickStudent();
				}
				c.insertStudents(students);
			}
		}  catch (EmptyBagException | StudentsNumberInCloudException e) {
			gameController.getModel().setLastRound(true);
		}
	}

	private boolean isValidIsland(int islandIndex) {
		int islandNumber = gameController.getModel().getIslands().size();
		return islandIndex < islandNumber && islandIndex >= 0;
	}

	public void setTurnPhase(TurnPhase turnPhase) {
		this.turnPhase = turnPhase;
	}

	public boolean checkIfTurnIsEnded() {
		return turnPhase == TurnPhase.TURN_ENDED ||
				(currentTurnRemainingActions.size() == 1 && currentTurnRemainingActions.contains(TurnPhase.PLAY_CHARACTER_CARD));
	}

	public void resetPossibleActions(RoundPhase phase) {
		currentTurnRemainingActions.clear(); //can contain play character card
		if (phase == RoundPhase.PLANNING) {
			currentTurnRemainingActions.add(TurnPhase.PLAY_ASSISTANT);
		} else {
			currentTurnRemainingActions.add(TurnPhase.MOVE_STUDENTS);
			currentTurnRemainingActions.add(TurnPhase.MOVE_MOTHER_NATURE);
			currentTurnRemainingActions.add(TurnPhase.SELECT_CLOUD);
			if (possibleActions.contains("PlayCharacterCard")) {
				currentTurnRemainingActions.add(TurnPhase.PLAY_CHARACTER_CARD);
			}
		}
	}
}
