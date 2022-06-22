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
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
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
    private ActionController actionController;
    private GameControllerStub gameController;
    private Player activePlayer; //Used to check values after the actions
    private GameConstants gameConstants;
    private RemoteViewStub viewActivePlayer;

    public static class GameControllerStub extends GameController {
        public GameControllerStub() {
            super(2, true);
            InitController initController = super.getInitController();
            try {
                initController.initializeGameComponents();
            } catch (EmptyBagException e) {
                Assertions.fail();
            }
            initController.addPlayer("player1");
            initController.addPlayer("player2");
            super.setGame(initController.getGame());
            initializeControllers();
        }

        public void setupGame() throws TowerTypeAlreadyTakenException, WizardTypeAlreadyTakenException {
            getInitController().setupPlayerTower(getModel().getPlayers().get(0), TowerType.BLACK);
            getInitController().setupPlayerWizard(getModel().getPlayers().get(0), WizardType.values()[0]);
            getInitController().setupPlayerTower(getModel().getPlayers().get(1), TowerType.WHITE);
            getInitController().setupPlayerWizard(getModel().getPlayers().get(1), WizardType.values()[1]);
        }
    }

    @BeforeEach
    void setup() {
        gameConstants = JsonUtils.constantsByNumPlayer(2);
        gameController = new GameControllerStub();
        try {
            gameController.setupGame();
        } catch (WizardTypeAlreadyTakenException | TowerTypeAlreadyTakenException e) {
            Assertions.fail();
        }
        actionController = gameController.getActionController();
        activePlayer = gameController.getTurnController().getActivePlayer();
        RemoteViewStub viewPlayer1 = new RemoteViewStub(1, "player1", gameController, null);
        RemoteViewStub viewPlayer2 = new RemoteViewStub(1, "player2", gameController, null);
        ErrorDispatcher errorDispatcher = new ErrorDispatcher(List.of(viewPlayer1, viewPlayer2));
        actionController.addListener("Error", errorDispatcher);
        actionController.getCharacterController().addListener("Error", errorDispatcher);
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

    @RepeatedTest(5)
    void playCharacterTest() {
        CharacterCard characterCard = gameController.getModel().getCharacterCards()[new Random().nextInt(3)];
        int characterToPlay = characterCard.getId();
        for (int i = 0; i < characterCard.getPrice(); i++) activePlayer.insertCoin();
        ClientMessageHeader header =
                new ClientMessageHeader("PlayCharacter", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterToPlay);
        payload.setAttribute("Arguments", null);
        MessageFromClient message = new MessageFromClient(header, payload);
        if (characterCard.requiresInput()) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> actionController.doAction(message));
        } else {
            try {
                actionController.doAction(message);
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail();
            }
            Assertions.assertEquals(activePlayer, characterCard.getPlayerActive());
        }
    }

    @Test
    void playCharacterTest_WithCharacterAlreadyPlayedInTheTurn() {
        CharacterCard characterCard = gameController.getModel().getCharacterCards()[new Random().nextInt(3)];
        int characterToPlay = characterCard.getId();
        ClientMessageHeader header =
                new ClientMessageHeader("PlayCharacter", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", characterToPlay);
        payload.setAttribute("Arguments", null);
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
    void notValidCharacterTest() {
        ClientMessageHeader header =
                new ClientMessageHeader("PlayCharacter", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("CharacterId", 15);
        payload.setAttribute("Arguments", null);
        MessageFromClient message = new MessageFromClient(header, payload);
        Assertions.assertThrows(IllegalArgumentException.class, () -> actionController.doAction(message));
        Assertions.assertEquals("Error", viewActivePlayer.getMessage().getServerMessageHeader().getMessageName());
        Assertions.assertEquals(ServerMessageType.SERVER_MESSAGE,
                viewActivePlayer.getMessage().getServerMessageHeader().getMessageType());
        Assertions.assertEquals(ErrorMessageType.ILLEGAL_ARGUMENT,
                viewActivePlayer.getMessage().getMessagePayload().getAttribute("ErrorType").getAsObject());
    }

    @Test
    void notValidMoveStudentsAction() {
        activePlayer.setSchool(new School(8, TowerType.BLACK,gameConstants, activePlayer));
        List<Student> entrance = List.of(new Student(RealmType.YELLOW_GNOMES), new Student(RealmType.YELLOW_GNOMES),
                new Student(RealmType.BLUE_UNICORNS), new Student(RealmType.RED_DRAGONS));
        activePlayer.getSchool().insertEntrance(entrance.toArray(new Student[0]));
        actionController.setTurnPhase(TurnPhase.MOVE_STUDENTS);
        ClientMessageHeader header =
                new ClientMessageHeader("MoveStudents", activePlayer.getNickName(), ClientMessageType.ACTION);
        MessagePayload payload = new MessagePayload();
        MessageFromClient message = new MessageFromClient(header, payload);
        //Player doesn't have 3 yellow gnomes
        payload.setAttribute("StudentsToDR", List.of(RealmType.YELLOW_GNOMES, RealmType.YELLOW_GNOMES, RealmType.YELLOW_GNOMES));
        payload.setAttribute("StudentsToIslands", new ArrayList<>());
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
}