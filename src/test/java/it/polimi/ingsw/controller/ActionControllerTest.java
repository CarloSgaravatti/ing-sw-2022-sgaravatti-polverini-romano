package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.listeners.ErrorDispatcher;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.School;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import it.polimi.ingsw.utils.Pair;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class ActionControllerTest extends TestCase {
    ActionController actionController;
    GameController gameController;
    Player activePlayer; //Used to check values after the actions
    GameConstants gameConstants;
    RemoteViewStub viewActivePlayer;

    @BeforeEach
    void setup() {
        gameConstants = JsonUtils.constantsByNumPlayer(2);
        gameController = new GameController(1, 2, true);
        InitController initController = gameController.getInitController();
        try {
            initController.initializeGameComponents();
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        initController.addPlayer("player1");
        initController.addPlayer("player2");
        gameController.setGame();
        gameController.initializeControllers();
        try {
            initController.setupPlayerTower(gameController.getModel().getPlayers().get(0), TowerType.BLACK);
            initController.setupPlayerWizard(gameController.getModel().getPlayers().get(0), WizardType.values()[0]);
            initController.setupPlayerTower(gameController.getModel().getPlayers().get(1), TowerType.WHITE);
            initController.setupPlayerWizard(gameController.getModel().getPlayers().get(1), WizardType.values()[1]);
        } catch (WizardTypeAlreadyTakenException | TowerTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        gameController.initializeControllers();
        actionController = gameController.getActionController();
        activePlayer = gameController.getTurnController().getActivePlayer();
        RemoteViewStub viewPlayer1 = new RemoteViewStub(1, "player1", gameController, null);
        RemoteViewStub viewPlayer2 = new RemoteViewStub(1, "player2", gameController, null);
        ErrorDispatcher errorDispatcher = new ErrorDispatcher(List.of(viewPlayer1, viewPlayer2));
        actionController.addListener("Error", errorDispatcher);
        viewActivePlayer = (activePlayer.getNickName().equals("player1")) ? viewPlayer1 : viewPlayer2;
    }

    @Test
    void playAssistantTest() {
        actionController.setTurnPhase(TurnPhase.PLAY_ASSISTANT);
        ClientMessageHeader header =
                new ClientMessageHeader("PlayAssistant", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Assistant", 3);
        MessageFromClient message = new MessageFromClient(header, payload);
        try {
            actionController.doAction(message);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(3, activePlayer.getTurnEffect().getOrderPrecedence());
    }

    @Test
    void studentMovementOnlyToDiningRoomTest() {
        actionController.setTurnPhase(TurnPhase.MOVE_STUDENTS);
        Student[] students = new Student[3];
        List<RealmType> toDiningRoom = new ArrayList<>();
        List<Pair<RealmType, Integer>> toIslands = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            students[i] = new Student(RealmType.values()[i]);
            toDiningRoom.add(RealmType.values()[i]);
        }
        activePlayer.setSchool(new School(8, TowerType.BLACK, gameConstants, activePlayer));
        activePlayer.getSchool().insertEntrance(students);
        //Now in the entrance there are: 1 Y, 1 B, 1 G
        ClientMessageHeader header =
                new ClientMessageHeader("MoveStudents", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("StudentsToDR", toDiningRoom);
        payload.setAttribute("StudentsToIslands", toIslands);
        MessageFromClient message = new MessageFromClient(header, payload);
        try {
            actionController.doAction(message);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        for (Student s: students) {
            Assertions.assertEquals(1, activePlayer.getSchool().getNumStudentsDiningRoom(s.getStudentType()));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 11})
    void studentMovementAlsoToIslandTest(int islandIndex) {
        actionController.setTurnPhase(TurnPhase.MOVE_STUDENTS);
        Island island = gameController.getModel().getIslands().get(islandIndex);
        int initialYellowsInIsland = island.getNumStudentsOfType(RealmType.YELLOW_GNOMES);
        Student[] students = new Student[3];
        List<RealmType> toDiningRoom = new ArrayList<>();
        List<Pair<RealmType, Integer>> toIslands = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            students[i] = new Student(RealmType.YELLOW_GNOMES);
            if (i < 2) toIslands.add(new Pair<>(RealmType.YELLOW_GNOMES, islandIndex));
            if (i == 2) toDiningRoom.add(RealmType.YELLOW_GNOMES);
        }
        activePlayer.setSchool(new School(8, TowerType.BLACK, gameConstants, activePlayer));
        activePlayer.getSchool().insertEntrance(students);
        ClientMessageHeader header =
                new ClientMessageHeader("MoveStudents", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        MessageFromClient message = new MessageFromClient(header, payload);
        payload.setAttribute("StudentsToDR", toDiningRoom);
        payload.setAttribute("StudentsToIslands", toIslands);
        try {
            actionController.doAction(message);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        Assertions.assertEquals(1, activePlayer.getSchool().getNumStudentsDiningRoom(RealmType.YELLOW_GNOMES));
        Assertions.assertEquals(initialYellowsInIsland + 2,
                island.getNumStudentsOfType(RealmType.YELLOW_GNOMES));
    }

    @Test
    void motherNatureMovementTest() {
        actionController.setTurnPhase(TurnPhase.MOVE_MOTHER_NATURE);
        int currMotherNature = gameController.getModel().motherNaturePositionIndex();
        try {
            activePlayer.playAssistant(5, new ArrayList<>());
        } catch (NoSuchAssistantException e) {
            Assertions.fail();
        }
        ClientMessageHeader header =
                new ClientMessageHeader("MoveMotherNature", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("MotherNature", 3);
        MessageFromClient message = new MessageFromClient(header, payload);
        try {
            actionController.doAction(message);
        } catch (Exception e) {
            Assertions.fail();
        }
        int newMotherNature = gameController.getModel().motherNaturePositionIndex();
        Assertions.assertEquals((currMotherNature + 3) % gameController.getModel().getIslands().size(), newMotherNature);
    }

    @Test
    void chooseCloudPickStudentsTest() {
        actionController.setTurnPhase(TurnPhase.SELECT_CLOUD);
        Cloud cloud = gameController.getModel().getClouds()[1];
        Student[] students = new Student[3];
        Arrays.fill(students, new Student(RealmType.YELLOW_GNOMES));
        try {
            cloud.insertStudents(students);
        } catch (StudentsNumberInCloudException e) {
            Assertions.fail();
        }
        activePlayer.setSchool(new School(8, TowerType.BLACK,gameConstants, activePlayer));
        ClientMessageHeader header =
                new ClientMessageHeader("PickFromCloud", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Cloud", 1);
        MessageFromClient message = new MessageFromClient(header, payload);
        try {
            actionController.doAction(message);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(3, activePlayer.getSchool().getStudentsEntrance(RealmType.YELLOW_GNOMES));
    }

    @RepeatedTest(3)
    void playCharacterTest() {
        CharacterCard characterCard = gameController.getModel().getCharacterCards()[new Random().nextInt(3)];
        int characterToPlay = characterCard.getId();
        List<String> arguments = new ArrayList<>();
        for (int i = 0; i < characterCard.getPrice(); i++) activePlayer.insertCoin();
        ClientMessageHeader header =
                new ClientMessageHeader("PlayCharacter", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterToPlay);
        payload.setAttribute("Arguments", arguments);
        MessageFromClient message = new MessageFromClient(header, payload);
        try {
            actionController.doAction(message);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(activePlayer, characterCard.getPlayerActive());
    }

    @Test
    void playCharacterTest_WithCharacterAlreadyPlayedInTheTurn() {
        CharacterCard characterCard = gameController.getModel().getCharacterCards()[new Random().nextInt(3)];
        int characterToPlay = characterCard.getId();
        ClientMessageHeader header =
                new ClientMessageHeader("PlayCharacter", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterToPlay);
        MessageFromClient message = new MessageFromClient(header, payload);
        activePlayer.getTurnEffect().setCharacterPlayed(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> actionController.doAction(message));
        Assertions.assertEquals("Error", viewActivePlayer.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals(ServerMessageType.SERVER_MESSAGE,
                viewActivePlayer.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals(ErrorMessageType.CHARACTER_ALREADY_PLAYED,
                viewActivePlayer.getMessage().getMessagePayload().getAttribute("ErrorType").getAsObject());
    }

    @Test
    void notValidCharacterEffectTest() {
        CharacterCard characterCard = gameController.getModel().getCharacterCards()[new Random().nextInt(3)];
        int characterToPlay = characterCard.getId();
        List<String> arguments = new ArrayList<>();
        arguments.add("Y"); //totally random argument, because it is not important, but arguments must be not empty
        ClientMessageHeader header =
                new ClientMessageHeader("CharacterEffect", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterToPlay);
        payload.setAttribute("Arguments", arguments);
        MessageFromClient message = new MessageFromClient(header, payload);
        for (int i = 0; i < characterCard.getPrice(); i++) activePlayer.insertCoin();
        activePlayer.getTurnEffect().setCharacterEffectConsumed(true);
        //Character is already consumed, so it cannot be played anymore
        Assertions.assertThrows(IllegalArgumentException.class, () -> actionController.doAction(message));
        Assertions.assertEquals("Error", viewActivePlayer.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals(ServerMessageType.SERVER_MESSAGE,
                viewActivePlayer.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals(ErrorMessageType.ILLEGAL_ARGUMENT,
                viewActivePlayer.getMessage().getMessagePayload().getAttribute("ErrorType").getAsObject());
    }

    @Test
    void refillCloudsTest() {
        actionController.refillClouds();
        for (Cloud cloud: gameController.getModel().getClouds()) {
            Assertions.assertEquals(gameController.getModel().getGameConstants().getNumStudentsPerCloud(),
                    cloud.getStudentsNumber());
        }
    }

    @Test
    void illegalActionRequired() {
        ClientMessageHeader header = new ClientMessageHeader("IllegalAction", null, null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> actionController.doAction(new MessageFromClient(header, null)));
    }

    //TODO: one test for all characters that have an active effect?
}