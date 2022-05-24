package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.*;

public class ModelView {
    private final Map<String, PlayerView> players = new HashMap<>();
    private FieldView field;
    private RoundPhase currentPhase;
    private String currentActivePlayer;
    private final boolean isExpert;
    private Map<Integer, Integer> clientPlayerAssistants = new HashMap<>();

    public ModelView(boolean isExpert) {
        this.isExpert = isExpert;
    }

    //Need to change, otherwise view knows game logic
    public void updateIslandTower(int islandId, TowerType tower) {
        Optional<TowerType> islandTower = Optional.ofNullable(field.getIsland(islandId).getThird());
        islandTower.ifPresent(t -> {
            Optional<String> playerWithIsland = getPlayerWithTower(t);
            playerWithIsland.ifPresent(p -> {
                int newTowers = players.get(p).getNumTowers() + field.getIsland(islandId).getSecond();
                players.get(p).updateNumTowers(newTowers);
            });
        });
        Optional<String> playerTower = getPlayerWithTower(tower);
        playerTower.ifPresent(p -> {
            int newTowers = players.get(p).getNumTowers() - field.getIsland(islandId).getSecond();
            players.get(p).updateNumTowers(newTowers);
        });
        field.getIsland(islandId).setThird(tower);
    }

    private Optional<String> getPlayerWithTower(TowerType t) {
        return players.keySet().stream()
                .filter(p -> players.get(p).getPlayerTower() == t).findAny();
    }

    public Map<String, PlayerView> getPlayers() {
        return players;
    }

    public FieldView getField() {
        return field;
    }

    public void setField(FieldView field) {
        this.field = field;
    }

    public RoundPhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(RoundPhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public String getCurrentActivePlayer() {
        return currentActivePlayer;
    }

    public void setCurrentActivePlayer(String currentActivePlayer) {
        this.currentActivePlayer = currentActivePlayer;
    }

    public boolean isExpert() {
        return isExpert;
    }

    public static void insertStudents(Integer[] toIns, RealmType[] students) {
        for (RealmType r: students) {
            toIns[r.ordinal()]++;
        }
    }

    public static void removeStudents(Integer[] toRemove, RealmType[] students) {
        for (RealmType r: students) {
            toRemove[r.ordinal()]--;
        }
    }

    public Map<Integer, Integer> getClientPlayerAssistants() {
        return clientPlayerAssistants;
    }

    public void setClientPlayerAssistants(Map<Integer, Integer> clientPlayerAssistants) {
        this.clientPlayerAssistants = clientPlayerAssistants;
    }

    public void removeAssistant(int assistant) {
        this.clientPlayerAssistants.remove(assistant);
    }
}
