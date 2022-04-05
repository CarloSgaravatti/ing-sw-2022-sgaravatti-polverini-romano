package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.utils.JsonUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionController {
	private TurnPhase turnPhase;
	private final TurnController turnController;
	private final GameController gameController;

	public ActionController(GameController gameController, TurnController turnController) {
		this.gameController = gameController;
		this.turnController = turnController;
		turnPhase = TurnPhase.FILL_CLOUDS;
	}

	//TODO: these methods should return a message to the client and not an exception
	//TODO: change in the turn phase after a conventional action is requested
	public void doAction(String message) throws IllegalArgumentException, AssistantAlreadyPlayedException,
			StudentNotFoundException, NoSuchAssistantException, ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, EmptyCloudException, FullDiningRoomException,
			NotEnoughCoinsException {
		List<String> args = Arrays.asList(message.split(" "));
		String action = args.get(0);
		args.remove(0);
		switch (action) {
			case "Assistant" -> playAssistant(args);
			case "MotherNature" -> motherNatureMovement(args);
			case "Students" -> studentMovement(args);
			case "Cloud" -> pickStudentsFillClouds(args);
			case "PlayCharacter" -> playCharacter(args);
			case "CharacterEffect" -> characterEffect(args);
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
		turnPhase = TurnPhase.TURN_ENDED; //planning phase is ended for this player
	}

	public void studentMovement(List<String> args) throws IllegalArgumentException, StudentNotFoundException,
			FullDiningRoomException {
		RealmType studentType;
		int islandIndex;
		if (args.size() > 9) throw new IllegalArgumentException();
		for (int i = 0; i < args.size(); i+=2) {
			studentType = getStudentType(args.get(i)); //make sure that the argument is correct
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
		changeTurnPhase();
	}

	public void motherNatureMovement(List<String> args) throws IllegalArgumentException {
		if (args.size() != 1) throw new IllegalArgumentException();
		int motherNatureMovement = Integer.parseInt(args.get(0));
		int legalMotherNatureMovement = turnController.getActivePlayer().getTurnEffect().getMotherNatureMovement();
		if (motherNatureMovement <= 0 || motherNatureMovement > legalMotherNatureMovement)
			throw new IllegalArgumentException(); //TODO: create a specific exception
		gameController.getModel().moveMotherNature(motherNatureMovement);
		changeTurnPhase();
	}

	//Is this useful?
	public void chooseCloudPickStudents(List<String> args) {

	}

	public void pickStudentsFillClouds(List<String> args) throws EmptyCloudException, IllegalArgumentException {
		if (args.size() != 1) throw new IllegalArgumentException();
		Cloud cloud = gameController.getModel().getClouds()[Integer.parseInt(args.get(0))];
		turnController.getActivePlayer().pickFromCloud(cloud);
		/*Student[] students = cloud.pickStudents();
		School school = turnController.getActivePlayer().getSchool();
		school.insertEntrance(students);*/
		changeTurnPhase();
	}

	public void playCharacter(List<String> args) throws IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, NotEnoughCoinsException {
		//TODO: illegal argument exception is too general
		if (turnController.getActivePlayer().getTurnEffect().isCharacterPlayed()) throw new IllegalArgumentException();
		int characterId = Integer.parseInt(args.get(0));
		if (!isValidCharacter(characterId)) throw new IllegalArgumentException();
		int coinToGeneralSupply;
		CharacterCreator characterCreator = CharacterCreator.getInstance();
		CharacterCard characterCard = characterCreator.getCharacter(characterId);
		coinToGeneralSupply = characterCard.getPrice();
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

	//TODO: add a method to have a better reuse of code from playCharacter and characterEffect
	//Character effect is ok to be called only for characters that have some active effects,
	//these are characters 1,3,7,9,10,11,12
	public void characterEffect(List<String> args) throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {
		if (turnController.getActivePlayer().getTurnEffect().isCharacterEffectConsumed()) throw new IllegalArgumentException();
		int characterId = Integer.parseInt(args.get(0));
		if (!isValidCharacter(characterId)) throw new IllegalArgumentException();
		JsonObject characterJsonObject = JsonUtils.getCharacterJsonObject(characterId);
		Method effectMethod = JsonUtils.getCharacterMethod(characterJsonObject);
		Object[] methodParameters = getParameters(args.subList(1, args.size()), effectMethod.getParameterCount());
		CharacterCreator characterCreator = CharacterCreator.getInstance();
		CharacterCard characterCard = characterCreator.getCharacter(characterId);
		effectMethod.invoke(characterCard, methodParameters);
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

	private Island getIslandFromNumber(int islandId) {
		return gameController.getModel().getIslands().get(islandId);
	}

	private boolean isValidIsland(int islandIndex) {
		int islandNumber = gameController.getModel().getIslands().size();
		return islandIndex < islandNumber && islandIndex >= 0;
	}

	private boolean isValidCharacter(int characterId) {
		CharacterCard[] characterCards = gameController.getModel().getCharacterCards();
		CharacterCreator characterCreator = CharacterCreator.getInstance();
		CharacterCard characterRequested = characterCreator.getCharacter(characterId);
		for (CharacterCard c: characterCards) {
			if (characterRequested.equals(c)) return true;
		}
		return false;
	}

	private Object[] getParameters(List<String> args, int numMethodParameters) throws IllegalArgumentException {
		Object[] parameters = new Object[numMethodParameters];
		int argIndex = 0;
		if (args.size() != 2 * numMethodParameters) throw new IllegalArgumentException();
		for (int i = 0; i < numMethodParameters; i++) {
			if (args.get(argIndex).equals("S")) {
				if (args.get(argIndex).length() == 1) {
					parameters[i] = getStudentType(args.get((argIndex) + 1));
				}
				else {
					Object[] studentTypes = new RealmType[Integer.parseInt(args.get(argIndex).substring(1))];
					for (int k = 0; k < studentTypes.length; k++) {
						studentTypes[k] = getStudentType(args.get((argIndex) + k + 1));
					}
					parameters[i] = studentTypes;
				}
			} else if (args.get(argIndex).equals("I")){
				parameters[i] = getIslandFromNumber(Integer.parseInt(args.get(argIndex + 1)));
			} else throw new IllegalArgumentException();
			argIndex += 2;
		}
		return parameters;
	}

	private void changeTurnPhase() {
		turnPhase = TurnPhase.values()[(turnPhase.ordinal() + 1) % TurnPhase.values().length];
	}
}
