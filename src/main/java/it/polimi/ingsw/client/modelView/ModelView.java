package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelView {
    private final Map<String, PlayerView> players = new HashMap<>();
    private FieldView field;
    private RoundPhase currentPhase;
    private String currentActivePlayer;
    private final boolean isExpert;

    public ModelView(boolean isExpert) {
        this.isExpert = isExpert;
    }

    //Need to change, otherwise view knows game logic
    public void updateIslandTower(int islandId, TowerType tower) {
        Optional<TowerType> islandTower = Optional.of(field.getIsland(islandId).getThird());
        islandTower.ifPresent(t -> {
            Optional<String> playerWithIsland = players.keySet().stream()
                    .filter(p -> players.get(p).getPlayerTower() == t).findAny();
            playerWithIsland.ifPresent(p -> {
                int newTowers = players.get(p).getNumTowers() + field.getIsland(islandId).getSecond();
                players.get(p).updateNumTowers(newTowers);
            });
        });
        field.getIsland(islandId).setThird(tower);
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
}
