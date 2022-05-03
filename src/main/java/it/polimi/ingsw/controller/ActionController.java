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
import java.util.ArrayList;
import java.util.List;

public class ActionController {
	private TurnPhase turnPhase;
	private final TurnController turnController;
	private final GameController gameController;
	protected EventListenerList listenerList = new EventListenerList();
	private final List<String> possibleActions;

	public ActionController(GameController gameController, TurnController turnController) {
		this.gameController = gameController;
		this.turnController = turnController;
		turnPhase = TurnPhase.FILL_CLOUDS;
		possibleActions = JsonUtils.getRulesByDifficulty(gameController.isExpertGame());
	}

	//TODO: these methods should return a message to the client and not an exception
	//TODO: change in the turn phase after a conventional action is requested
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
		if (turnPhase != TurnPhase.PLAY_ASSISTANT) {
			fireErrorEvent(ErrorMessageType.ILLEGAL_TURN_ACTION, turnController.getActivePlayer().getNickName());
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
			fireErrorEvent(ErrorMessageType.ILLEGAL_ARGUMENT,turnController.getActivePlayer().getNickName());
			throw new AssistantAlreadyPlayedException();
		}
		setTurnPhase(TurnPhase.TURN_ENDED); //planning phase is ended for this player
		fireMyEventAssistant(assistantIdx); //assistantIdx è l'indice dell'assistant?
		if (turnController.getActivePlayer().getAssistants().size() == 0) {
			gameController.getModel().setLastRound(true);
		}
	}

	public void motherNatureMovement(MessagePayload payload) throws IllegalArgumentException,
			WrongTurnActionRequestedException, ClassCastException {
		if (turnPhase != TurnPhase.MOVE_MOTHER_NATURE) {
			fireErrorEvent(ErrorMessageType.ILLEGAL_TURN_ACTION, turnController.getActivePlayer().getNickName());
			throw new WrongTurnActionRequestedException();
		}
		int motherNatureMovement = payload.getAttribute("MotherNature").getAsInt();
		int legalMotherNatureMovement = turnController.getActivePlayer().getTurnEffect().getMotherNatureMovement();
		if (motherNatureMovement <= 0 || motherNatureMovement > legalMotherNatureMovement) {
			fireErrorEvent(ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException(); //TODO: create a specific exception
		}
		gameController.getModel().moveMotherNature(motherNatureMovement);
		setTurnPhase(TurnPhase.SELECT_CLOUD);
	}

	//Use of ? to not have unchecked warning (maybe we should change the message format to not use generics)
	public void studentMovement(MessagePayload payload) throws IllegalArgumentException, StudentNotFoundException,
			FullDiningRoomException, WrongTurnActionRequestedException, ClassCastException {
		if (turnPhase != TurnPhase.MOVE_STUDENTS){
			fireErrorEvent(ErrorMessageType.ILLEGAL_TURN_ACTION, turnController.getActivePlayer().getNickName());
			throw new WrongTurnActionRequestedException();
		}
		List<?> toDiningRoom = (List<?>) payload.getAttribute("StudentsToDR").getAsObject();
		List<?> toIslands = (List<?>) payload.getAttribute("StudentsToIslands").getAsObject();
		if (toDiningRoom.size() + toIslands.size() > 3){
			fireErrorEvent(ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
			throw new IllegalArgumentException();
		}
		School school = turnController.getActivePlayer().getSchool();
		for (Object studentType: toDiningRoom) {
			if (school.moveFromEntranceToDiningRoom((RealmType) studentType)) {
				turnController.getActivePlayer().insertCoin();
				gameController.getModel().takeCoinFromGeneralSupply();
			}
		}
		for (Object pair: toIslands) {
			Pair<?, ?> pairStudentIsland = (Pair<?, ?>) pair;
			int islandIndex = (Integer) pairStudentIsland.getSecond();
			if (!isValidIsland(islandIndex)){
				fireErrorEvent(ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
				throw new IllegalArgumentException();
			}
			Island island = gameController.getModel().getIslands().get(islandIndex);
			RealmType studentType = (RealmType) pairStudentIsland.getFirst();
			school.sendStudentToIsland(island, studentType);
		}
		setTurnPhase(TurnPhase.MOVE_MOTHER_NATURE);
	}

	public void pickStudentsFromClouds(MessagePayload payload) throws EmptyCloudException, WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.SELECT_CLOUD) throw new WrongTurnActionRequestedException();
		int cloudIndex = payload.getAttribute("Cloud").getAsInt();
		Cloud cloud = gameController.getModel().getClouds()[cloudIndex];
		turnController.getActivePlayer().pickFromCloud(cloud);
		fireMyEvent(cloudIndex,turnController.getActivePlayer().getNickName());
		setTurnPhase(TurnPhase.TURN_ENDED);
	}

	public void playCharacter(MessagePayload payload) throws IllegalArgumentException,
			NotEnoughCoinsException, IllegalCharacterActionRequestedException, ClassCastException {
		//TODO: illegal argument exception is too general
		if (turnController.getActivePlayer().getTurnEffect().isCharacterPlayed()) {
            fireErrorEvent(ErrorMessageType.CHARACTER_ALREADY_PLAYED, turnController.getActivePlayer().getNickName());
            throw new IllegalArgumentException();
        }
		int characterId = payload.getAttribute("CharacterId").getAsInt();
		CharacterCard characterCard = gameController.getModel().getCharacterById(characterId);
		if (characterCard == null) {
            fireErrorEvent(ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
            throw new IllegalArgumentException();
        }
		int coinToGeneralSupply = characterCard.getPrice();
		//TODO: if player does not have enough coins this is not correct
		if (!characterCard.isCoinPresent()) coinToGeneralSupply--;
		characterCard.playCard(turnController.getActivePlayer());
		fireMyEventCharacter(characterId, turnController.getActivePlayer().getNickName());
		turnController.getActivePlayer().getTurnEffect().setCharacterPlayed(true);
		turnController.getActivePlayer().getTurnEffect().setCharacterEffectConsumed(false);
		gameController.getModel().insertCoinsInGeneralSupply(coinToGeneralSupply);
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
            fireErrorEvent(ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
            throw new IllegalArgumentException();
        }
		int characterId = payload.getAttribute("CharacterId").getAsInt();
		CharacterCard characterCard = gameController.getModel().getCharacterById(characterId);
		if (characterCard == null) {
            fireErrorEvent(ErrorMessageType.ILLEGAL_ARGUMENT, turnController.getActivePlayer().getNickName());
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

	//TODO: invoke addEventListener method into InitController after making RemoteView
	public void addEventListener(CloudListener listener) {
		listenerList.add(CloudListener.class, listener);
	}

    public void addEventListener(CharacterListener listener) {
		listenerList.add(CharacterListener.class, listener);
	}

	public void addEventListener(PlayerListener listener) {
		listenerList.add(PlayerListener.class, listener);
	}

	public void fireMyEvent(int cloudIndex, String namePlayer) {
		for(CloudListener event : listenerList.getListeners(CloudListener.class)){
			event.eventPerformed(cloudIndex, namePlayer);
		}
	}

	public void fireMyEventCharacter(int characterId, String namePlayer) {
		for(CharacterListener event : listenerList.getListeners(CharacterListener.class)) {
			event.eventPerformed(characterId, namePlayer);
		}
	}

	public void fireMyEventAssistant(int assistantIdx) {
		for (PlayerListener event : listenerList.getListeners(PlayerListener.class)) {
			event.eventPerformedAssistant(assistantIdx, turnController.getActivePlayer().getNickName());
		}
	}

	public boolean checkIfTurnIsEnded() {
		return turnPhase == TurnPhase.TURN_ENDED;
	}

	//-------------------------------------------------------
	//Not sure of doing these things
	public void addListener(ErrorDispatcher errorListener) {
		listenerList.add(ErrorDispatcher.class, errorListener);
	}

	public void fireErrorEvent(ErrorMessageType error, String nickname) {
		for (ErrorDispatcher errorDispatcher: listenerList.getListeners(ErrorDispatcher.class)) {
			errorDispatcher.onErrorEvent(error, nickname);
		}
	}
}
