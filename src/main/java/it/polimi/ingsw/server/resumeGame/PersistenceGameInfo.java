package it.polimi.ingsw.server.resumeGame;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.Triplet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class PersistenceGameInfo {
    private final int gameId;
    private String[] lastPlayerOrder;
    private RoundPhase lastRoundPhase;
    private TurnPhase lastTurnPhase;
    private boolean lastOrderCalculated;
    private List<TurnPhase> lastTurnRemainingActions;
    private boolean isFirstRound;
    private int numPlayers;
    private boolean started;
    private Player[] players;
    private Bag bag;
    private Island[] islands;
    private Cloud[] clouds;
    private int coinGeneralSupply;
    private int indexActivePlayer;
    private GameConstants gameConstants;
    private boolean isLastRound;
    private boolean isExpertGame;
    private CharacterCard[] characterCards;
    private final Map<Integer, String> lastCharacterActivePlayer = new HashMap<>();

    public PersistenceGameInfo(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameState(GameController gameController) {
        Game game = gameController.getModel();
        if (gameController.isExpertGame()) {
            this.characterCards = game.getCharacterCards();
            Arrays.stream(characterCards)
                    .forEach(c -> {
                        if (c.getPlayerActive() != null) lastCharacterActivePlayer.put(c.getId(), c.getPlayerActive().getNickName());
                    });
        }
        TurnController turnController = gameController.getTurnController();
        this.lastPlayerOrder = Arrays.stream(turnController.getPlayerOrder()).map(Player::getNickName).toList().toArray(new String[0]);
        this.lastRoundPhase = turnController.getCurrentPhase();
        this.lastOrderCalculated = turnController.isOrderCalculated();
        this.isFirstRound = turnController.getPlanningPhaseOrderStrategy().isFirstRound();
        ActionController actionController = gameController.getActionController();
        this.lastTurnPhase = actionController.getTurnPhase();
        this.lastTurnRemainingActions = actionController.getCurrentTurnRemainingActions();
        this.numPlayers = game.getNumPlayers();
        this.started = game.isStarted();
        this.isLastRound = game.isLastRound();
        this.gameConstants = game.getGameConstants();
        this.indexActivePlayer = game.getIndexActivePlayer();
        this.clouds = game.getClouds();
        this.islands = game.getIslands().toArray(new Island[0]);
        this.bag = game.getBag();
        this.players = game.getPlayers().toArray(new Player[0]);
        this.coinGeneralSupply = game.getCoinGeneralSupply();
        this.isExpertGame = game.isExpertGame();
    }

    public GameController restoreGameState() {
        return restoreControllers(restoreModelComponents());
    }

    public void saveGame() throws URISyntaxException, IOException {
        SaveGame.saveGame(this);
    }

    private Game restoreModelComponents() {
        Game game = new Game(new ArrayList<>(Arrays.asList(islands)), clouds, gameConstants, isExpertGame, bag, players);
        game.setNumPlayers(numPlayers);
        game.setLastRound(isLastRound);
        game.insertCoinsInGeneralSupply(coinGeneralSupply);
        game.setIndexActivePlayer(indexActivePlayer);
        if (started) game.start();
        if (isExpertGame) {
            for (CharacterCard charactersWithTransient : characterCards) {
                Player lastActivePlayer = game.getPlayerByNickname(lastCharacterActivePlayer
                        .get(charactersWithTransient.getId()));
                charactersWithTransient.restoreActivePlayer(lastActivePlayer);
            }
        }
        game.restoreCharacters(characterCards);
        game.getPlayers().forEach(player -> player.restorePlayer(game, gameConstants));
        game.getIslands().forEach(island -> island.restoreIsland(game));
        return game;
    }

    private GameController restoreControllers(final Game game) {
        GameController gameController = new GameController(game);
        Player[] lastOrder = Arrays.stream(lastPlayerOrder)
                .map(game::getPlayerByNickname).toList().toArray(new Player[0]);
        gameController.getTurnController().restoreController(lastOrderCalculated, lastOrder, lastRoundPhase, isFirstRound);
        gameController.getActionController().restoreController(lastTurnPhase, lastTurnRemainingActions);
        return gameController;
    }

    public List<String> getParticipants() {
        return Arrays.stream(players).map(Player::getNickName).toList();
    }

    public Triplet<Integer, Boolean, String[]> getGameInfo() {
        return new Triplet<>(numPlayers, isExpertGame, getParticipants().toArray(new String[0]));
    }
}
