package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameLobby {
    private final GameController gameController;
    private Game game;
    private final int numPlayers;
    private final Map<String, ClientConnection> participants = new ConcurrentHashMap<>();
    private boolean started = false;
    private final int gameId;

    private final Object setupLock = new Object();

    public GameLobby(int gameId, int numPlayers) {
        this.gameId = gameId;
        gameController = new GameController(gameId);
        gameController.getInitController().setNumPlayers(numPlayers);
        this.numPlayers = numPlayers;
    }

    public synchronized void insertInLobby(String nickname, ClientConnection clientConnection)
            throws GameAlreadyStartedException {
        if (isStarted()) throw new GameAlreadyStartedException();
        participants.putIfAbsent(nickname, clientConnection);
        ServerMessageHeader header = new ServerMessageHeader("PlayerJoined", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", nickname);
        multicast(new MessageFromServer(header, payload), nickname);
        payload = new MessagePayload();
        if (participants.size() == numPlayers) {
            setStarted(true);
            header = new ServerMessageHeader("GameStarted", ServerMessageType.GAME_UPDATE);
            payload.setAttribute("Players", participants.keySet());
            MessageFromServer message = new MessageFromServer(header, payload);
            broadcast(message);
            InitController initController = gameController.getInitController();
            for (String name: participants.keySet()) {
                initController.addPlayer(name);
            }
            try {
                initController.initializeGameComponents();
            } catch (EmptyBagException e) {
                //At this point it shouldn't be thrown, maybe it should be handled before
            }
            //Need to create a new thread because this method is done by the thread that reads messages in
            //SocketClientConnection, we want that when the setupGame method is running all the SocketClientConnections
            //are reading messages to update setup choices.
            new Thread(this::setupGame).start();
        } else {
            header = new ServerMessageHeader("GameLobby", ServerMessageType.CLIENT_SETUP);
            payload.setAttribute("GameNumPlayers", numPlayers);
            payload.setAttribute("WaitingPlayers", participants.size());
            //The last attribute can be replaced with players nicknames
            clientConnection.asyncSend(new MessageFromServer(header, payload));
        }
    }

    public synchronized boolean isStarted() {
        return started;
    }

    public synchronized void setStarted(boolean started) {
        this.started = started;
    }

    public void broadcast(MessageFromServer message) {
        for (ClientConnection c: participants.values()) {
            c.asyncSend(message);
        }
    }

    public void multicast(MessageFromServer message, String except) {
        for (String nickname: participants.keySet()) {
            if (!except.equals(nickname)) {
                participants.get(nickname).asyncSend(message);
            }
        }
    }

    public void setupGame() {
        boolean setupTowersDone = false;
        boolean setupWizardsDone = false;
        while (!(setupTowersDone && setupWizardsDone)) {
            Map<String, TowerType> playersWithTower = gameController.getInitController().getPlayersWithTower();
            int numPlayersWithTower = playersWithTower.size();
            int numPlayersWithWizard = 0;
            if (playersWithTower.size() < numPlayers) {
                setupTowers(playersWithTower);
            } else {
                Map<String, WizardType> playersWithWizard = gameController.getInitController().getPlayersWithWizard();
                numPlayersWithWizard = playersWithWizard.size();
                if (playersWithWizard.size() < numPlayers) {
                    setupWizards(playersWithWizard);
                }
            }
            try {
                while (gameController.getInitController().getPlayersWithTower().size() == numPlayersWithTower &&
                        gameController.getInitController().getPlayersWithWizard().size() == numPlayersWithWizard) {
                    setupLock.wait();
                }
            } catch (InterruptedException e) {
                //Don't know what to put here (maybe we can just ignore it?)
            }
            setupTowersDone = gameController.getInitController().getPlayersWithTower().size() == numPlayers;
            setupWizardsDone = gameController.getInitController().getPlayersWithWizard().size() == numPlayers;
        }
        sendInitializations();
    }

    //This method wake up the setupGame Thread because someone has chosen a tower or a wizard
    public synchronized void notifySetupChanges() {
        setupLock.notify();
    }

    private void setupTowers(Map<String, TowerType> playersWithTower) {
        List<String> playersWithoutTower = participants.keySet().stream()
                .filter(p -> !playersWithTower.containsKey(p)).toList();
        List<TowerType> towerTypesNotTaken = Arrays.stream(TowerType.values())
                .filter(t -> !playersWithTower.containsValue(t)).toList();
        ClientConnection connection = participants.get(playersWithoutTower.get(0));
        ServerMessageHeader header = new ServerMessageHeader("TowerType", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("FreeTowers", towerTypesNotTaken.toArray(new TowerType[0]));
        MessageFromServer message = new MessageFromServer(header, payload);
        connection.asyncSend(message);
        //TODO: notify other clients that a client is choosing the tower
    }

    private void setupWizards(Map<String, WizardType> playersWithWizard) {
        List<String> playersWithoutWizard = participants.keySet().stream()
                .filter(p -> !playersWithWizard.containsKey(p)).toList();
        List<WizardType> wizardTypesNotTaken = Arrays.stream(WizardType.values())
                .filter(w -> !playersWithWizard.containsValue(w)).toList();
        ClientConnection connection = participants.get(playersWithoutWizard.get(0));
        ServerMessageHeader header = new ServerMessageHeader("WizardType", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("FreeWizards", wizardTypesNotTaken.toArray(new WizardType[0]));
        MessageFromServer message = new MessageFromServer(header, payload);
        connection.asyncSend(message);
        //TODO: notify other clients that a client is choosing the wizard
    }

    private void sendInitializations() {
        gameController.setGame();
        game = gameController.getModel();
        ServerMessageHeader header = new ServerMessageHeader("GameInitializations", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("MotherNature", game.motherNaturePositionIndex());
        Map<String, Integer[]> playersSchools = new HashMap<>();
        for (String name: participants.keySet()) {
            Integer[] students = new Integer[RealmType.values().length];
            for (int i = 0; i < students.length; i++) {
                students[i] = game.getPlayerByNickname(name).getSchool().getStudentsEntrance(RealmType.values()[i]);
            }
            playersSchools.put(name, students);
        }
        payload.setAttribute("Schools", playersSchools);
        Map<Integer, RealmType> islands = new HashMap<>();
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            List<Student> islandStudents = game.getIslands().get(i).getStudents();
            islands.put(i, (islandStudents.size() == 0) ? null : islandStudents.get(0).getStudentType());
        }
        payload.setAttribute("Islands", islands);
        Map<Integer, List<RealmType>> clouds = new HashMap<>();
        Cloud[] gameClouds = game.getClouds();
        for (int i = 0; i < gameClouds.length; i++) {
            clouds.put(i, Arrays.stream(gameClouds[i].getStudents())
                                    .map(Student::getStudentType).collect(Collectors.toList()));
        }
        payload.setAttribute("Clouds", clouds);
        broadcast(new MessageFromServer(header, payload));
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public GameController getGameController() {
        return gameController;
    }

    public Map<String, ClientConnection> getParticipants() {
        return participants;
    }
}
