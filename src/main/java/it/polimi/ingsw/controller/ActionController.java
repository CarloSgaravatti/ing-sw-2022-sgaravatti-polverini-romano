package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.JsonUtils;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class ActionController {
	private TurnPhase turnPhase;
	private final TurnController turnController;
	private final GameController gameController;
	private final CharacterController characterController;
	private final List<String> possibleActions;
	private final List<TurnPhase> currentTurnRemainingActions = new ArrayList<>();
	private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private final int studentsToMove;

	//TODO: substitute all illegal argument exceptions with booleans

	public ActionController(GameController gameController, TurnController turnController) {
		this.gameController = gameController;
		this.turnController = turnController;
		this.characterController = new CharacterController(gameController.getModel());
		turnPhase = TurnPhase.PLAY_ASSISTANT;
		possibleActions = JsonUtils.getRulesByDifficulty(gameController.isExpertGame());
		currentTurnRemainingActions.add(TurnPhase.PLAY_ASSISTANT);
		studentsToMove = (gameController.getModel().getNumPlayers() == 3) ? 4 : 3;
	}

	public void addListener(String propertyName, PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}

	public void doAction(MessageFromClient message) throws IllegalArgumentException {
		String messageName = message.getClientMessageHeader().getMessageName();
		MessagePayload payload = message.getMessagePayload();
		if (!possibleActions.contains(messageName)) throw new IllegalArgumentException();
		try {
			switch (messageName) {
				case "PlayAssistant" -> playAssistant(payload);
				case "MoveMotherNature" -> motherNatureMovement(payload);
				case "MoveStudents" -> studentMovement(payload);
				case "PickFromCloud" -> pickStudentsFromClouds(payload);
				case "PlayCharacter" -> playCharacter(payload);
				default -> throw new IllegalArgumentException();
			}
		} catch (WrongTurnActionRequestedException e) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_TURN_ACTION, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
	}

	public void playAssistant(MessagePayload payload) throws WrongTurnActionRequestedException {
		String playerName = turnController.getActivePlayer().getNickName();
		if (turnPhase != TurnPhase.PLAY_ASSISTANT) throw new WrongTurnActionRequestedException();
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
		try {
			if (!turnController.getActivePlayer().playAssistant(assistantIdx, assistantAlreadyPlayed)) {
				listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, playerName);
				throw new IllegalArgumentException();
			}
		} catch (NoSuchAssistantException e) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, playerName);
			throw new IllegalArgumentException();
		}
		currentTurnRemainingActions.remove(TurnPhase.PLAY_ASSISTANT);
		setTurnPhase(TurnPhase.TURN_ENDED); //planning phase is ended for this player
		if (turnController.getActivePlayer().getAssistants().size() == 0) {
			gameController.getModel().setLastRound(true);
		}
	}

	public void motherNatureMovement(MessagePayload payload) throws IllegalArgumentException,
			WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.MOVE_MOTHER_NATURE) throw new WrongTurnActionRequestedException();
		int motherNatureMovement = payload.getAttribute("MotherNature").getAsInt();
		int legalMotherNatureMovement = turnController.getActivePlayer().getTurnEffect().getMotherNatureMovement();
		if (motherNatureMovement <= 0 || motherNatureMovement > legalMotherNatureMovement) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		gameController.getModel().moveMotherNature(motherNatureMovement);
		currentTurnRemainingActions.remove(TurnPhase.MOVE_MOTHER_NATURE);
		setTurnPhase(TurnPhase.SELECT_CLOUD);
	}

	//Use of ? to not have unchecked warning (maybe we should change the message format to not use generics)
	//TODO: maybe this method can be done better
	public void studentMovement(MessagePayload payload) throws IllegalArgumentException, WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.MOVE_STUDENTS) throw new WrongTurnActionRequestedException();
		List<?> toDiningRoom = (List<?>) payload.getAttribute("StudentsToDR").getAsObject();
		List<?> toIslands = new ArrayList<>((List<?>) payload.getAttribute("StudentsToIslands").getAsObject());
		if (toDiningRoom.size() + toIslands.size() > studentsToMove){
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		School school = turnController.getActivePlayer().getSchool();
		Student[] studentsToDiningRoom = getStudentsToDiningRoom(toDiningRoom);
		Map<Integer, List<Student>> studentsToIslands = getStudentsToIslands(toIslands);
		//TODO: make all methods in action controller return a boolean and not IllegalArgumentException
		try {
			int coinsGained = school.insertDiningRoom(studentsToDiningRoom, true, true);
			for (int i = 0; i < coinsGained; i++) {
				turnController.getActivePlayer().insertCoin();
				gameController.getModel().takeCoinFromGeneralSupply();
			}
		} catch (FullDiningRoomException e) {
			school.insertEntrance(studentsToDiningRoom);
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		for (Integer i: studentsToIslands.keySet()) {
			gameController.getModel().getIslands().get(i).addStudents(true, studentsToIslands.get(i).toArray(new Student[0]));
		}
		currentTurnRemainingActions.remove(TurnPhase.MOVE_STUDENTS);
		setTurnPhase(TurnPhase.MOVE_MOTHER_NATURE);
	}

	private Student[] getStudentsToDiningRoom(List<?> students) throws IllegalArgumentException {
		School school = turnController.getActivePlayer().getSchool();
		List<Student> removedFromEntrance = new ArrayList<>();
		try {
			for (Object studentType : students) {
				removedFromEntrance.add(school.removeStudentEntrance((RealmType) studentType));
			}
		} catch (StudentNotFoundException e) {
			school.insertEntrance(removedFromEntrance.toArray(new Student[0]));
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		return removedFromEntrance.toArray(new Student[0]);
	}

	private Map<Integer, List<Student>> getStudentsToIslands(List<?> students) throws IllegalArgumentException {
		School school = turnController.getActivePlayer().getSchool();
		Map<Integer, List<Student>> studentsToIslands = new HashMap<>();
		for (Object island: students) {
			int islandId = (Integer) ((Pair<?, ?>) island).getSecond();
			if (!isValidIsland(islandId)){
				listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
				throw new IllegalArgumentException();
			}
		}
		try {
			for (Object toIsland : students) {
				studentsToIslands.putIfAbsent((Integer) ((Pair<?, ?>) toIsland).getSecond(), new ArrayList<>());
				Student fromEntrance = school.removeStudentEntrance((RealmType) ((Pair<?, ?>) toIsland).getFirst());
				studentsToIslands.get((Integer) ((Pair<?, ?>) toIsland).getSecond()).add(fromEntrance);
			}
		} catch (StudentNotFoundException e) {
			Student[] studentsFromEntrance = studentsToIslands.values().stream().flatMap(Collection::stream).toList().toArray(new Student[0]);
			school.insertEntrance(studentsFromEntrance);
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		return studentsToIslands;
	}

	public void pickStudentsFromClouds(MessagePayload payload) throws WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.SELECT_CLOUD) throw new WrongTurnActionRequestedException();
		int cloudIndex = payload.getAttribute("Cloud").getAsInt();
		Cloud cloud = gameController.getModel().getClouds()[cloudIndex];
		try {
			turnController.getActivePlayer().pickFromCloud(cloud);
		} catch (EmptyCloudException e) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		currentTurnRemainingActions.remove(TurnPhase.SELECT_CLOUD);
		setTurnPhase(TurnPhase.TURN_ENDED);
	}

	public void playCharacter(MessagePayload payload) throws IllegalArgumentException {
		String arguments = payload.getAttribute("Arguments").getAsString();
		List<String> args;
		int id = payload.getAttribute("CharacterId").getAsInt();
		if (arguments == null) args = new ArrayList<>();
		else {
			String[] payloadArgs = arguments.split(" ");
			args = new ArrayList<>(Arrays.asList(payloadArgs));
		}
		CharacterCard characterCard = gameController.getModel().getCharacterById(id);
		if (characterCard == null) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		try {
			characterController.handleCharacterAction(args, characterCard, turnController.getActivePlayer());
		} catch (NotEnoughCoinsException | IllegalCharacterActionRequestedException e) {
			listeners.firePropertyChange("Error", ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		currentTurnRemainingActions.remove(TurnPhase.PLAY_CHARACTER_CARD);
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
		currentTurnRemainingActions.addAll(phase.getTurnActions());
		if (phase == RoundPhase.ACTION && !possibleActions.contains("PlayCharacter")) {
			currentTurnRemainingActions.remove(TurnPhase.PLAY_CHARACTER_CARD);
		}
	}

	public List<TurnPhase> getCurrentTurnRemainingActions() {
		return currentTurnRemainingActions;
	}

	public CharacterController getCharacterController() {
		return characterController;
	}
}
