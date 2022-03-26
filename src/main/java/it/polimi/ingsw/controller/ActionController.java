package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.exceptions.EmptyCloudException;
import it.polimi.ingsw.exceptions.NoSuchAssistantException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionController {
	private TurnPhase turnPhase;
	private TurnController turnController;
	private GameController gameController;

	//TODO: these methods should return a message to the client and not an exception
	//TODO: change in the turn phase after a conventional action is requested
	public void doAction(String message) throws IllegalArgumentException, AssistantAlreadyPlayedException,
			StudentNotFoundException, EmptyCloudException, NoSuchAssistantException {
		List<String> args = Arrays.asList(message.split(" "));
		String action = args.get(0);
		args.remove(0);
		switch (action) {
			case "A" -> playAssistant(args);
			case "M" -> motherNatureMovement(args);
			case "S" -> studentMovement(args);
			case "C" -> pickStudentsFillClouds(args);
			case "CP" -> playCharacter(args);
			case "CE" -> characterEffect(args);
			default -> throw new IllegalArgumentException();
		}
	}

	public void playAssistant(List<String> args) throws IllegalArgumentException, AssistantAlreadyPlayedException,
			NoSuchAssistantException {
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
	}

	public void studentMovement(List<String> args) throws IllegalArgumentException, StudentNotFoundException {
		RealmType studentType;
		int islandIndex;
		for (int i = 0; i < args.size(); i+=2) {
			studentType = getStudentType(args.get(i));
			if (!args.get(i + 1).equals("D") && !args.get(i + 1).equals("I")) {
				throw new IllegalArgumentException();
			}
			if (args.get(i + 1).equals("I")) {
				islandIndex = Integer.parseInt(args.get(i + 2));
				if (!isValidIsland(islandIndex)) throw new IllegalArgumentException();
				i++;
			}
		}
		if (args.size() > 9) throw new IllegalArgumentException();
		School school = turnController.getActivePlayer().getSchool();
		for (int i = 0; i < args.size(); i+=2) {
			studentType = getStudentType(args.get(i));
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
	}

	public void motherNatureMovement(List<String> args) throws IllegalArgumentException {
		if (args.size() != 1) throw new IllegalArgumentException();
		int motherNatureMovement = Integer.parseInt(args.get(0));
		int legalMotherNatureMovement = turnController.getActivePlayer().getTurnEffect().getMotherNatureMovement();
		if (motherNatureMovement <= 0 || motherNatureMovement > legalMotherNatureMovement)
			throw new IllegalArgumentException(); //TODO: create a specific exception
		gameController.getModel().moveMotherNature(motherNatureMovement);
	}

	public void chooseCloudPickStudents(List<String> args) throws IllegalArgumentException, EmptyCloudException {
		if (args.size() != 1) throw new IllegalArgumentException();
		Cloud cloud = gameController.getModel().getClouds()[Integer.parseInt(args.get(0))];
		Student[] students = cloud.pickStudents();
		School school = turnController.getActivePlayer().getSchool();
		school.insertEntrance(students);
	}

	//Is this useful?
	public void pickStudentsFillClouds(List<String> args) {

	}

	public void playCharacter(List<String> args) throws IllegalArgumentException {
		int characterId = Integer.parseInt(args.get(0));
		if (!isValidCharacter(characterId)) throw new IllegalArgumentException();
		CharacterCard characterCard = CharacterCreator.getCharacter(characterId);
		characterCard.playCard(turnController.getActivePlayer());
		turnController.getActivePlayer().getTurnEffect().setCharacterPlayed(true);
		turnController.getActivePlayer().getTurnEffect().setCharacterEffectConsumed(false);
		//If the character effect is active and the player calls it immediately
		if (args.size() > 1) {
			characterEffect(args);
		}
	}

	//Character effect is ok to be called only for characters that have some active effects,
	//these are characters 1,3,5,7,10,11,12
	public void characterEffect(List<String> args) {
		//TODO: reflection with json
	}

	private RealmType getStudentType(String abbreviation) throws IllegalArgumentException {
		return switch (abbreviation) {
			case "Y" -> RealmType.YELLOW_GNOMES;
			case "B" -> RealmType.BLUE_UNICORNS;
			case "G" -> RealmType.GREEN_FROGS;
			case "R" -> RealmType.RED_DRAGONS;
			case "P" -> RealmType.PINK_FAIRES;
			default -> throw new IllegalArgumentException();
		};
	}

	private boolean isValidIsland(int islandIndex) {
		int islandNumber = gameController.getModel().getIslands().size();
		return islandIndex < islandNumber && islandIndex >= 0;
	}

	public boolean isValidCharacter(int characterId) {
		CharacterCard[] characterCards = gameController.getModel().getCharacterCards();
		CharacterCard characterRequested = CharacterCreator.getCharacter(characterId);
		for (CharacterCard c: characterCards) {
			if (characterRequested.equals(c)) return true;
		}
		return false;
	}
}
