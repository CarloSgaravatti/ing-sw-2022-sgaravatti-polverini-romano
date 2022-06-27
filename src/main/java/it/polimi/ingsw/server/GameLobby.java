package it.polimi.ingsw.server;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.messages.MessageFromServer;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageHeader;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.messages.simpleModel.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.characters.Character1;
import it.polimi.ingsw.model.characters.Character11;
import it.polimi.ingsw.model.characters.Character5;
import it.polimi.ingsw.model.characters.Character7;
import it.polimi.ingsw.model.enumerations.*;
import it.polimi.ingsw.server.resumeGame.PersistenceGameInfo;
import it.polimi.ingsw.server.resumeGame.SaveGame;
import it.polimi.ingsw.utils.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameLobby {
    private GameController gameController;
    private Game game;
    private final int numPlayers;
    private final Map<String, ClientConnection> participants = new ConcurrentHashMap<>();
    private boolean started = false;
    private final int gameId;
    private final boolean isExpertGame;
    private final Server server;
    //private SaveGame saveGame;
    private final boolean isGameRestored;
    private final Object setupLock = new Object();

    public GameLobby(int gameId, int numPlayers, boolean isExpertGame, Server server, boolean isGameRestored) {
        this.gameId = gameId;
        if (!isGameRestored) {
            gameController = new GameController(numPlayers, isExpertGame);
        }
        this.numPlayers = numPlayers;
        this.isExpertGame = isExpertGame;
        this.server = server;
        this.isGameRestored = isGameRestored;
        /*try {
            this.saveGame = new SaveGame(gameId, this);
        }catch (IOException | URISyntaxException e){
            e.printStackTrace();
        }*/
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
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (participants.size() == numPlayers) {
            try {
                header = new ServerMessageHeader("GameStarted", ServerMessageType.GAME_SETUP);
                payload.setAttribute("Opponents", participants.keySet().toArray(new String[0]));
                broadcast(new MessageFromServer(header, payload));
                if (!isGameRestored) {
                    InitController initController = gameController.getInitController();
                    try {
                        initController.initializeGameComponents();
                    } catch (
                            EmptyBagException e) {/*At this point it shouldn't be thrown, maybe it should be handled before*/}
                    for (String name : participants.keySet()) {
                        initController.addPlayer(name);
                    }
                    gameController.setGame(initController.getGame());
                    game = gameController.getModel();
                    gameController.initializeControllers();
                } else {
                    PersistenceGameInfo persistenceGameInfo = new PersistenceGameInfo(gameId);
                    gameController = persistenceGameInfo.restoreGameState();
                }
                gameController.createListeners(assignRemoteViews(), this);
                if (!game.isStarted()) {
                    //Need to create a new thread because this method is done by the thread that reads messages in
                    //SocketClientConnection, we want that when the setupGame method is running all the SocketClientConnections
                    //are reading messages to update setup choices.
                    new Thread(this::setupGame).start();
                    setStarted(true);
                } else { //game is restored and started
                    restoreGame();
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    private void restoreGame() {
        Pair<SimpleField, SimplePlayer[]> fieldInitializations = getFieldInitializations();
        Map<String, TowerType> playersTowers = new HashMap<>();
        Map<String, WizardType> playersWizards = new HashMap<>();
        String[] professorOwners = new String[RealmType.values().length];
        game.getPlayers().forEach(player -> {
            playersTowers.put(player.getNickName(), player.getSchool().getTowerType());
            playersWizards.put(player.getNickName(), player.getWizardType());
        });
        List<SimplePlayer> players = Arrays.stream(fieldInitializations.getSecond()).toList();
        players.forEach(simplePlayer -> {
            Player player = game.getPlayerByNickname(simplePlayer.getNickname());
            List<RealmType> diningRoom = new ArrayList<>();
            for (RealmType r: RealmType.values()) {
                for (int j = 0; j < player.getSchool().getNumStudentsDiningRoom(r); j++) diningRoom.add(r);
                if (player.getSchool().isProfessorPresent(r)) professorOwners[r.ordinal()] = player.getNickName();
            }
            simplePlayer.setDiningRoom(diningRoom.toArray(new RealmType[0]));
            TurnEffect turnEffect = player.getTurnEffect();
            simplePlayer.setLastAssistant(new Pair<>(turnEffect.getOrderPrecedence(), turnEffect.getMotherNatureMovement()));
        });
        SimpleModel simpleModel = new SimpleModel();
        simpleModel.setField(fieldInitializations.getFirst());
        simpleModel.setSchools(players);
        simpleModel.setProfessorOwners(professorOwners);
        simpleModel.setTowers(playersTowers);
        simpleModel.setWizards(playersWizards);
        ServerMessageHeader header = new ServerMessageHeader("GameRestoredData", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("SimpleModel", simpleModel);
        broadcast(new MessageFromServer(header, payload));
        gameController.restartGame();
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
        if (gameController.getInitController().getPlayersWithTower().size() != 0) {
            sendRestoredSetup();
        }
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
        sendInitializations();
        gameController.startGame();
    }

    private void sendRestoredSetup() {
        Map<String, TowerType> playersWithTower = gameController.getInitController().getPlayersWithTower();
        Map<String, WizardType> playersWithWizard = gameController.getInitController().getPlayersWithWizard();
        ServerMessageHeader header = new ServerMessageHeader("RestoredSetup", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        SimpleModel setupModel = new SimpleModel(playersWithTower, playersWithWizard);
        payload.setAttribute("SetupInfo", setupModel);
        broadcast(new MessageFromServer(header, payload));
    }

    private void sendInitializations() {
        ServerMessageHeader header = new ServerMessageHeader("GameInitializations", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        Pair<SimpleField, SimplePlayer[]> initializations = getFieldInitializations();
        payload.setAttribute("Field", initializations.getFirst());
        payload.setAttribute("PlayersInfo", initializations.getSecond());
        broadcast(new MessageFromServer(header, payload));
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

    private Pair<SimpleField, SimplePlayer[]> getFieldInitializations() {
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
            Integer[] students = new Integer[RealmType.values().length];
            Arrays.fill(students, 0);
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
                characters.add(getSimpleCharacter(c));
            }
            simpleField = new SimpleField(islandsView, clouds, characters, motherNaturePosition);
        } else {
            simpleField = new SimpleField(islandsView, clouds, motherNaturePosition);
        }
        return new Pair<>(simpleField, playersView);
    }

    //TODO: modify when characters are reimplemented
    private SimpleCharacter getSimpleCharacter(CharacterCard c) {
        return switch (c.getId()) {
            case 5 -> new SimpleCharacter(((Character5) c).getNoEntryTiles(), c.getId(), c.getPrice());
            case 1 -> {
                RealmType[] students = ((Character1) c).getStudents().stream()
                        .map(Student::getStudentType).toList().toArray(new RealmType[0]);
                yield new SimpleCharacter(students, c.getId(), c.getPrice());
            }
            case 7 -> {
                RealmType[] students = ((Character7) c).getStudents().stream()
                        .map(Student::getStudentType).toList().toArray(new RealmType[0]);
                yield new SimpleCharacter(students, c.getId(), c.getPrice());
            }
            case 11 -> {
                RealmType[] students = ((Character11) c).getStudents().stream()
                        .map(Student::getStudentType).toList().toArray(new RealmType[0]);
                yield new SimpleCharacter(students, c.getId(), c.getPrice());
            }
            default -> new SimpleCharacter(c.getId(), c.getPrice());
        };
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
        //TODO: maybe there are other things to do
        //saveGame.deleteFile();
        server.deleteGame(gameId);
    }

    public String[] getGameParticipants() {
        return participants.keySet().toArray(new String[0]);
    }

    protected Map<String, ClientConnection> getClients() {
        return participants;
    }

    protected void onDisconnection(String clientDisconnected) {
        ServerMessageHeader header = new ServerMessageHeader("PlayerDisconnected", ServerMessageType.SERVER_MESSAGE);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", clientDisconnected);
        MessageFromServer message = new MessageFromServer(header, payload);
        multicast(message, clientDisconnected);
        server.deleteGame(gameId);
    }

    public boolean isExpertGame() {
        return isExpertGame;
    }

    public void setSaveGame(){
        //new Thread(()->saveGame.createJson()).start();
        new Thread(() -> {
            PersistenceGameInfo gameInfo = new PersistenceGameInfo(gameId);
            gameInfo.setGameState(gameController);
            try {
                gameInfo.saveGame();
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
