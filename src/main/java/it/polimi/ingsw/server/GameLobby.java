package it.polimi.ingsw.server;

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
import it.polimi.ingsw.server.persistence.PersistenceGameInfo;
import it.polimi.ingsw.server.persistence.SaveGame;
import it.polimi.ingsw.utils.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GameLobby is the part of the Server that ius responsible for handling a particular game. The GameLobby will instanciate
 * the game controller and the game when the lobby become full of players.
 */
public class GameLobby {
    private GameController gameController;
    private Game game;
    private final int numPlayers;
    private final Map<String, ClientConnection> participants = new ConcurrentHashMap<>();
    private boolean started = false;
    private final int gameId;
    private final boolean isExpertGame;
    private final Server server;
    private final boolean isGameRestored;
    private final Object setupLock = new Object();

    /**
     * Constructs a new GameLobby that have the specified id, number of players and rules. The GameLobby will be associated
     * to the server. The lobby can represent a game that was previously saved and was then restored when the server
     * return online.
     *
     * @param gameId the id of the game
     * @param numPlayers the number of players of the game
     * @param isExpertGame true if the game is expert, otherwise false
     * @param server the Server instance
     * @param isGameRestored true if the game is restored, otherwise false
     */
    public GameLobby(int gameId, int numPlayers, boolean isExpertGame, Server server, boolean isGameRestored) {
        this.gameId = gameId;
        if (!isGameRestored) {
            gameController = new GameController(numPlayers, isExpertGame);
        }
        this.numPlayers = numPlayers;
        this.isExpertGame = isExpertGame;
        this.server = server;
        this.isGameRestored = isGameRestored;
    }

    /**
     * Returns true if the game was restored, otherwise false
     *
     * @return true if the game was restored, otherwise false
     */
    public boolean isGameRestored() {
        return isGameRestored;
    }

    /**
     * Insert the specified client in the lobby. If the lobby is full (with also the new client inserted), initializations
     * are sent to all clients after all RemoteViews are created.
     *
     * @param nickname the nickname of the client
     * @param clientConnection the connection to the client
     */
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
                    } catch (EmptyBagException ignored) {}
                    for (String name : participants.keySet()) {
                        initController.addPlayer(name);
                    }
                    gameController.setGame(initController.getGame());
                    game = gameController.getModel();
                    gameController.initializeControllers();
                    server.saveParticipants();
                    setSaveGame();
                } else {
                    PersistenceGameInfo persistenceGameInfo = SaveGame.getPersistenceData(gameId);
                    gameController = persistenceGameInfo.restoreGameState();
                    game = gameController.getModel();
                    server.onGameRestored(gameId);
                }
                gameController.createListeners(assignRemoteViews(), this);
                if (!game.isStarted()) {
                    //Need to create a new thread because this method is done by the thread that reads messages in
                    //SocketClientConnection, we want that when the setupGame method is running all the SocketClientConnections
                    //are reading messages to update setup choices.
                    new Thread(this::setupGame).start();
                } else { //game is restored and started
                    restoreGame();
                }
                setStarted(true);
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    /**
     * Sends all information in broadcast about the restored game state. The message that will be sent will have
     * GameRestoredData as the message name.
     */
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
            simplePlayer.setLastAssistant(player.getTurnEffect().getLastPlayedAssistant());
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

    /**
     * Assign the remote views to the clients after the lobby became full
     *
     * @return the remove views of all clients connected to the lobby
     */
    private List<RemoteView> assignRemoteViews() {
        List<RemoteView> views = new ArrayList<>();
        for (String participant: participants.keySet()) {
            views.add(new RemoteView(participants.get(participant), participant, this, gameController));
        }
        return views;
    }

    /**
     * Returns true if the game is started, otherwise false.
     *
     * @return true if the game is started, otherwise false.
     */
    public synchronized boolean isStarted() {
        return started;
    }

    public synchronized void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Send in broadcast the specified message
     *
     * @param message the message that will be sent
     */
    public void broadcast(MessageFromServer message) {
        for (ClientConnection c: participants.values()) {
            c.asyncSend(message);
        }
    }

    /**
     * Send in multicast the specified message to all clients, except the specified one
     *
     * @param message the message that will be sent
     * @param except the client that will not receive the message
     */
    public void multicast(MessageFromServer message, String except) {
        for (String nickname: participants.keySet()) {
            if (!except.equals(nickname)) {
                participants.get(nickname).asyncSend(message);
            }
        }
    }

    /**
     * Handles the setup phase of the game, the method will be executed by a separated thread a will continue looping until
     * all setup are done. The thread will also wait until someone makes a new choice as a producer/consumer schema with
     * the InitController of the game (that will receive the choices), where the InitController is the producer.
     */
    private void setupGame() {
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
                            gameController.getInitController().getPlayersWithWizard().size() == numPlayersWithWizard &&
                            (gameController.getInitController().getPlayersWithTower().size() != numPlayers ||
                            gameController.getInitController().getPlayersWithWizard().size() != numPlayers)) {
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
        setSaveGame();
    }

    /**
     * Sends the setups that were previously made for games that are restored but were not started (this means that some
     * other setup choices have to be made)
     */
    private void sendRestoredSetup() {
        Map<String, TowerType> playersWithTower = gameController.getInitController().getPlayersWithTower();
        Map<String, WizardType> playersWithWizard = gameController.getInitController().getPlayersWithWizard();
        ServerMessageHeader header = new ServerMessageHeader("RestoredSetup", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        SimpleModel setupModel = new SimpleModel(playersWithTower, playersWithWizard);
        payload.setAttribute("SetupInfo", setupModel);
        broadcast(new MessageFromServer(header, payload));
    }

    /**
     * Sends all game initializations for games that have not been restored
     */
    private void sendInitializations() {
        ServerMessageHeader header = new ServerMessageHeader("GameInitializations", ServerMessageType.GAME_SETUP);
        MessagePayload payload = new MessagePayload();
        Pair<SimpleField, SimplePlayer[]> initializations = getFieldInitializations();
        payload.setAttribute("Field", initializations.getFirst());
        payload.setAttribute("PlayersInfo", initializations.getSecond());
        broadcast(new MessageFromServer(header, payload));
    }

    /**
     * Notifies the thread that is doing the setupGame method that a player has made a setup choice
     */
    //This method wake up the setupGame Thread because someone has chosen a tower or a wizard
    public void notifySetupChanges() {
        synchronized (setupLock) {
            setupLock.notify();
        }
    }

    /**
     * Sends to a random player that has not already made a tower choice a message that notifies him that he has to
     * choose a tower from the towers that are not already taken
     *
     * @param playersWithTower the players that have already made the tower choice, associated with the tower
     *                         choice that they have made
     */
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
    }

    /**
     * Sends to a random player that has not already made a wizard choice a message that notifies him that he has to
     * choose a wizard from the wizards that are not already taken
     *
     * @param playersWithWizard the players that have already made the wizard choice, associated with the wizard
     *                         choice that they have made
     */
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
    }

    /**
     * Get the initializations of the game islands, clouds, characters and players schools. The islands and clouds are
     * inserted in the simple field and the players schools are inserted in the array of simple players.
     *
     * @return the initializations of the game islands, clouds, characters and players schools
     */
    private Pair<SimpleField, SimplePlayer[]> getFieldInitializations() {
        int motherNaturePosition = game.motherNaturePositionIndex();
        List<SimpleIsland> islandsView = new ArrayList<>();
        for (Island i: game.getIslands()) {
            Integer[] students = RealmType.getIntegerRepresentation(i.getStudents().stream()
                    .map(Student::getStudentType).toList().toArray(new RealmType[0]));
            islandsView.add(new SimpleIsland(students, i.getNumTowers(), i.getTowerType(), i.getNoEntryTilePresents()));
        }
        Map<Integer, Integer[]> clouds = new HashMap<>();
        Cloud[] gameClouds = game.getClouds();
        for (int i = 0; i < gameClouds.length; i++) {
            Integer[] students;
            if (gameClouds[i].getStudentsNumber() == 0) {
                students = new Integer[RealmType.values().length];
                Arrays.fill(students, 0);
            } else {
                students = RealmType.getIntegerRepresentation(Arrays.stream(gameClouds[i].getStudents())
                        .map(Student::getStudentType).toList().toArray(new RealmType[0]));
            }
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

    /**
     * Get a simple character that represent the specified character card and that will be added to the initializations
     * of the game
     *
     * @param c the character card
     * @return a simple character that represent the specified character card
     */
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

    /**
     * Returns the number of players of the game that the lobby represent
     *
     * @return the number of players of the game that the lobby represent
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * End the game and informs the server that the game have finished
     */
    public void doEndGameOperations() {
        server.deleteGame(gameId);
    }

    /**
     * Returns the names of the participants of the game
     *
     * @return the names of the participants of the game
     */
    public String[] getGameParticipants() {
        return participants.keySet().toArray(new String[0]);
    }

    /**
     * Returns the participants of the game, each associated with the corresponding connection to the client
     *
     * @return the participants of the game, each associated with the corresponding connection to the client
     */
    protected Map<String, ClientConnection> getClients() {
        return participants;
    }

    /**
     * Informs the participants of the game that the specified player have disconnected
     *
     * @param clientDisconnected the player who has disconnected
     */
    protected void onDisconnection(String clientDisconnected) {
        ServerMessageHeader header = new ServerMessageHeader("PlayerDisconnected", ServerMessageType.SERVER_MESSAGE);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("PlayerName", clientDisconnected);
        MessageFromServer message = new MessageFromServer(header, payload);
        multicast(message, clientDisconnected);
        server.deleteGame(gameId);
    }

    /**
     * Returns true if the game is expert, otherwise false
     *
     * @return true if the game is expert, otherwise false
     */
    public boolean isExpertGame() {
        return isExpertGame;
    }

    /**
     * Asynchronously saves tha game on the disk
     */
    public void setSaveGame(){
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

    /**
     * Informs the participants of the restored game that a previous participant have decided to not resume the game and
     * therefore the game have been deleted
     *
     * @param clientName the name of the one who decided to delete the game
     */
    public void onOtherPlayerDeleteChoice(String clientName) {
        ServerMessageHeader header = new ServerMessageHeader("DeletedGame", ServerMessageType.SERVER_MESSAGE);
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("ChoiceMaker", clientName);
        broadcast(new MessageFromServer(header, payload));
        server.deleteGame(gameId);
    }
}
