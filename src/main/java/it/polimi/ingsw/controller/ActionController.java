package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.CloudListener;
import it.polimi.ingsw.model.*;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionController {
	private TurnPhase turnPhase;
	private final TurnController turnController;
	private final GameController gameController;
	protected EventListenerList listenerList = new EventListenerList();

	public ActionController(GameController gameController, TurnController turnController) {
		this.gameController = gameController;
		this.turnController = turnController;
		turnPhase = TurnPhase.FILL_CLOUDS;
	}

	//TODO: these methods should return a message to the client and not an exception
	//TODO: change in the turn phase after a conventional action is requested
	public void doAction(String message) throws IllegalArgumentException, AssistantAlreadyPlayedException,
			StudentNotFoundException, NoSuchAssistantException, EmptyCloudException, FullDiningRoomException,
			NotEnoughCoinsException, IllegalCharacterActionRequestedException, WrongTurnActionRequestedException {
		List<String> args = new ArrayList<>(Arrays.asList(message.split(" ")));
		String action = args.get(0);
		args.remove(0);
		switch (action) {
			case "Assistant" -> playAssistant(args);
			case "MotherNature" -> motherNatureMovement(args);
			case "Students" -> studentMovement(args);
			case "Cloud" -> pickStudentsFromClouds(args);
			case "PlayCharacter" -> playCharacter(args);
			case "CharacterEffect" -> characterEffect(args);
			default -> throw new IllegalArgumentException();
		}
	}

	public void playAssistant(List<String> args) throws IllegalArgumentException, AssistantAlreadyPlayedException,
			NoSuchAssistantException, WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.PLAY_ASSISTANT) throw new WrongTurnActionRequestedException();
		if (args.size() != 1) throw new IllegalArgumentException();
		int assistantIdx = Integer.parseInt(args.get(0));
		List<Player> players = gameController.getModel().getPlayers();
		List<Integer> assistantAlreadyPlayed = new ArrayList<>();
		for (Player p: players) {
			int assistant = p.getTurnEffect().getOrderPrecedence();
			//assistant == 0 indicates that the player has not already played an assistant
			if (assistant != 0) {
				assistantAlreadyPlayed.add(assistant);
			}
		}
		if (!turnController.getActivePlayer().playAssistant(assistantIdx, assistantAlreadyPlayed))
			throw new AssistantAlreadyPlayedException();
		setTurnPhase(TurnPhase.TURN_ENDED); //planning phase is ended for this player
	}

	public void studentMovement(List<String> args) throws IllegalArgumentException, StudentNotFoundException,
			FullDiningRoomException, WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.MOVE_STUDENTS) throw new WrongTurnActionRequestedException();
		RealmType studentType;
		int islandIndex;
		if (args.size() > 9) throw new IllegalArgumentException();
		for (int i = 0; i < args.size(); i+=2) {
			studentType = RealmType.getRealmByAbbreviation(args.get(i)); //make sure that the argument is correct
			if (!args.get(i + 1).equals("D") && !args.get(i + 1).equals("I")) {
				throw new IllegalArgumentException();
			}
			if (args.get(i + 1).equals("I")) {
				islandIndex = Integer.parseInt(args.get(i + 2));
				if (!isValidIsland(islandIndex)) throw new IllegalArgumentException();
				i++;
			}
		}
		School school = turnController.getActivePlayer().getSchool();
		for (int i = 0; i < args.size(); i+=2) {
			studentType = RealmType.getRealmByAbbreviation(args.get(i));
			switch (args.get(i + 1)) {
				case "D" ->	{
					if (school.moveFromEntranceToDiningRoom(studentType)) {
						turnController.getActivePlayer().insertCoin();
					}
				}
				case "I" -> {
					islandIndex = Integer.parseInt(args.get(i + 2));
					Island island = gameController.getModel().getIslands().get(islandIndex);
					school.sendStudentToIsland(island, studentType);
					i++;
				}
			}
		}
		setTurnPhase(TurnPhase.MOVE_MOTHER_NATURE);
	}

	public void motherNatureMovement(List<String> args) throws IllegalArgumentException, WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.MOVE_MOTHER_NATURE) throw new WrongTurnActionRequestedException();
		if (args.size() != 1) throw new IllegalArgumentException();
		int motherNatureMovement = Integer.parseInt(args.get(0));
		int legalMotherNatureMovement = turnController.getActivePlayer().getTurnEffect().getMotherNatureMovement();
		if (motherNatureMovement <= 0 || motherNatureMovement > legalMotherNatureMovement)
			throw new IllegalArgumentException(); //TODO: create a specific exception
		gameController.getModel().moveMotherNature(motherNatureMovement);
		setTurnPhase(TurnPhase.SELECT_CLOUD);
	}

	//Is this useful?
	public void chooseCloudPickStudents(List<String> args) {

	}

	public void pickStudentsFromClouds(List<String> args) throws EmptyCloudException, IllegalArgumentException,
			WrongTurnActionRequestedException {
		if (turnPhase != TurnPhase.SELECT_CLOUD) throw new WrongTurnActionRequestedException();
		if (args.size() != 1) throw new IllegalArgumentException();
		int cloudindex = Integer.parseInt(args.get(0));
		Cloud cloud = gameController.getModel().getClouds()[cloudindex];
		turnController.getActivePlayer().pickFromCloud(cloud);
		fireMyEvent(cloudindex,turnController.getActivePlayer().getNickName());
		setTurnPhase(TurnPhase.TURN_ENDED);
	}

	public void playCharacter(List<String> args) throws IllegalArgumentException,
			NotEnoughCoinsException, IllegalCharacterActionRequestedException {
		//TODO: illegal argument exception is too general
		if (turnController.getActivePlayer().getTurnEffect().isCharacterPlayed()) throw new IllegalArgumentException();
		int characterId = Integer.parseInt(args.get(0));
		CharacterCard characterCard = gameController.getModel().getCharacterById(characterId);
		if (characterCard == null) throw new IllegalArgumentException();
		int coinToGeneralSupply = characterCard.getPrice();
		//TODO: if player does not have enough coins this is not correct
		if (!characterCard.isCoinPresent()) coinToGeneralSupply--;
		characterCard.playCard(turnController.getActivePlayer());
		turnController.getActivePlayer().getTurnEffect().setCharacterPlayed(true);
		turnController.getActivePlayer().getTurnEffect().setCharacterEffectConsumed(false);
		gameController.getModel().insertCoinsInGeneralSupply(coinToGeneralSupply);
		//If the character effect is active and the player calls the effect immediately
		if (args.size() > 1) {
			characterEffect(args);
		}
	}

	public void characterEffect(List<String> args) throws IllegalCharacterActionRequestedException {
		if (turnController.getActivePlayer().getTurnEffect().isCharacterEffectConsumed()) throw new IllegalArgumentException();
		int characterId = Integer.parseInt(args.get(0));
		CharacterCard characterCard = gameController.getModel().getCharacterById(characterId);
		if (characterCard == null) throw new IllegalArgumentException();
		characterCard.useEffect(args.subList(1, args.size()));
	}

	private Island getIslandFromNumber(int islandId) {
		return gameController.getModel().getIslands().get(islandId);
	}

	private boolean isValidIsland(int islandIndex) {
		int islandNumber = gameController.getModel().getIslands().size();
		return islandIndex < islandNumber && islandIndex >= 0;
	}

	@Deprecated
	private boolean isValidCharacter(int characterId) {
		CharacterCard[] characterCards = gameController.getModel().getCharacterCards();
		for (CharacterCard c: characterCards) {
			if (characterId == c.getId()) return true;
		}
		return false;
	}

	public void setTurnPhase(TurnPhase turnPhase) {
		this.turnPhase = turnPhase;
	}

	//TODO: invoke addEventListner method into InitController after making RemoteView
	public void addEventListener(CloudListener listener) {
		listenerList.add(CloudListener.class, listener);
	}

	void fireMyEvent(int cloudIndex, String namePlayer) {
		for(CloudListener event : listenerList.getListeners(CloudListener.class)){
			event.eventPerformed(cloudIndex,namePlayer);
		}
	}

}
