package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.SimpleCharacter;
import it.polimi.ingsw.messages.simpleModel.SimpleField;
import it.polimi.ingsw.messages.simpleModel.SimpleIsland;
import it.polimi.ingsw.messages.simpleModel.SimplePlayer;
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
    private final boolean isExpertGame;

    private final Object setupLock = new Object();

    public GameLobby(int gameId, int numPlayers, boolean isExpertGame) {
        this.gameId = gameId;
        gameController = new GameController(gameId, numPlayers, isExpertGame);
        this.numPlayers = numPlayers;
        this.isExpertGame = isExpertGame;
    }

    public synchronized void insertInLobby(String nickname, ClientConnection clientConnection) {
        participants.putIfAbsent(nickname, clientConnection);
        ServerMessageHeader header = new ServerMessageHeader("PlayerJoined", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", nickname);
        multicast(new MessageFromServer(header, payload), nickname);
        payload = new MessagePayload();

        header = new ServerMessageHeader("GameLobby", ServerMessageType.SERVER_MESSAGE);
        payload.setAttribute("Rules", isExpertGame);
        payload.setAttribute("GameNumPlayers", numPlayers);
        payload.setAttribute("WaitingPlayers", participants.keySet().toArray(new String[0]));
        clientConnection.asyncSend(new MessageFromServer(header, payload));

        if (participants.size() == numPlayers) {
            setStarted(true);
            header = new ServerMessageHeader("GameStarted", ServerMessageType.GAME_SETUP);
            payload.setAttribute("Opponents", participants.keySet().toArray(new String[0]));
            broadcast(new MessageFromServer(header, payload));
            InitController initController = gameController.getInitController();
            try {
                initController.initializeGameComponents();
            } catch (EmptyBagException e) {
                //At this point it shouldn't be thrown, maybe it should be handled before
            }
            for (String name: participants.keySet()) {
                initController.addPlayer(name);
            }
            gameController.setGame();
            gameController.initializeControllers();
            gameController.createListeners(assignRemoteViews(), this);
            //Need to create a new thread because this method is done by the thread that reads messages in
            //SocketClientConnection, we want that when the setupGame method is running all the SocketClientConnections
            //are reading messages to update setup choices.
            new Thread(this::setupGame).start();
        } /*else {
            header = new ServerMessageHeader("GameLobby", ServerMessageType.SERVER_MESSAGE);
            payload.setAttribute("Rules", isExpertGame);
            payload.setAttribute("GameNumPlayers", numPlayers);
            payload.setAttribute("WaitingPlayers", participants.keySet().toArray(new String[0]));
            clientConnection.asyncSend(new MessageFromServer(header, payload));
        }*/
    }

    private List<RemoteView> assignRemoteViews() {
        List<RemoteView> views = new ArrayList<>();
        for (String participant: participants.keySet()) {
            views.add(new RemoteView(participants.get(participant), gameId, participant, this, gameController));
        }
        return views;
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
                synchronized (setupLock) {
                    while (gameController.getInitController().getPlayersWithTower().size() == numPlayersWithTower &&
                            gameController.getInitController().getPlayersWithWizard().size() == numPlayersWithWizard) {
                        setupLock.wait();
                    }
                }
            } catch (InterruptedException e) {
                //Don't know what to put here (maybe we can just ignore it?)
            }
            setupTowersDone = gameController.getInitController().getPlayersWithTower().size() == numPlayers;
            setupWizardsDone = gameController.getInitController().getPlayersWithWizard().size() == numPlayers;
        }
        gameController.getActionController().refillClouds();
        sendInitializations();
        gameController.startGame();
    }

    //This method wake up the setupGame Thread because someone has chosen a tower or a wizard
    public void notifySetupChanges() {
        synchronized (setupLock) {
            setupLock.notify();
        }
    }

    private void setupTowers(Map<String, TowerType> playersWithTower) {
        List<String> playersWithoutTower = participants.keySet().stream()
                .filter(p -> !playersWithTower.containsKey(p)).toList();
        List<TowerType> towerTypesNotTaken = Arrays.stream(TowerType.values())
                .filter(t -> !playersWithTower.containsValue(t)).toList();
        ClientConnection connection = participants.get(playersWithoutTower.get(0));
        ServerMessageHeader header = new ServerMessageHeader("TowerTypeRequest", ServerMessageType.GAME_SETUP);
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
        ServerMessageHeader header = new ServerMessageHeader("WizardTypeRequest", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("FreeWizards", wizardTypesNotTaken.toArray(new WizardType[0]));
        MessageFromServer message = new MessageFromServer(header, payload);
        connection.asyncSend(message);
        //TODO: notify other clients that a client is choosing the wizard
    }

    private void sendInitializations() {
        //TODO: decide if game initializations can be sent in more than one message
        gameController.setGame();
        game = gameController.getModel();
        ServerMessageHeader header = new ServerMessageHeader("GameInitializations", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        int motherNaturePosition = game.motherNaturePositionIndex();
        List<SimpleIsland> islandsView = new ArrayList<>();
        for (Island i: game.getIslands()) {
            Integer[] students = RealmType.getIntegerRepresentation(i.getStudents().stream()
                    .map(Student::getStudentType).toList().toArray(new RealmType[0]));
            islandsView.add(new SimpleIsland(students));
        }
        Map<Integer, Integer[]> clouds = new HashMap<>();
        Cloud[] gameClouds = game.getClouds();
        for (int i = 0; i < gameClouds.length; i++) {
            Integer[] students = RealmType.getIntegerRepresentation(Arrays.stream(gameClouds[i].getStudents())
                    .map(Student::getStudentType).toList().toArray(new RealmType[0]));
            clouds.put(i, students);
        }
        List<Player> players = game.getPlayers();
        SimplePlayer[] playersView = new SimplePlayer[players.size()];
        for (int i = 0; i < playersView.length; i++) {
            List<RealmType> entrance = new ArrayList<>();
            for (RealmType r: RealmType.values()) {
                for (int j = 0; j < players.get(i).getSchool().getStudentsEntrance(r); j++) entrance.add(r);
            }
            playersView[i] = new SimplePlayer(players.get(i).getNickName(), entrance.toArray(new RealmType[0]),
                    players.get(i).getSchool().getNumTowers(), players.get(i).getNumCoins());
        }
        SimpleField simpleField;
        if (isExpertGame) {
            List<SimpleCharacter> characters = new ArrayList<>();
            for (CharacterCard c: game.getCharacterCards()) {
                characters.add(new SimpleCharacter(c.getId(), c.getPrice())); //TODO: modify (probably character cards need to be modified)
            }
            simpleField = new SimpleField(islandsView, clouds, characters, motherNaturePosition);
        } else {
            simpleField = new SimpleField(islandsView, clouds, motherNaturePosition);
        }
        payload.setAttribute("Field", simpleField);
        payload.setAttribute("PlayersInfo", playersView);
        broadcast(new MessageFromServer(header, payload));
        //TODO: assistants
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

    public void doEndGameOperations() {
        //TODO
    }

    public String[] getGameParticipants() {
        return participants.keySet().toArray(new String[0]);
    }
}
