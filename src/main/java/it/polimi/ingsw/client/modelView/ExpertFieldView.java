package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.messages.simpleModel.SimpleCharacter;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.util.*;

public class ExpertFieldView {
    private final Map<Integer, Integer> characters = new HashMap<>(); //id and price
    private boolean noEntryTilesPresent = false;
    private Pair<Integer, Integer> numNoEntryTilesOnCharacter; //id and no entry tiles
    private Map<Integer, Integer> islandsWithNoEntryTiles; //islandId and no entry tiles on island
    private final Map<Integer, Integer[]> characterStudents = new HashMap<>(); //id and students

    public ExpertFieldView(List<SimpleCharacter> characters) {
        for (SimpleCharacter c : characters) {
            this.characters.put(c.getId(), c.getPrice());
            Optional<Integer> noEntryTiles = Optional.of(c.getNumNoEntryTiles());
            noEntryTiles.ifPresent(numNoEntryTiles -> {
                noEntryTilesPresent = true;
                numNoEntryTilesOnCharacter = new Pair<>(c.getId(), numNoEntryTiles);
                islandsWithNoEntryTiles = new HashMap<>();
            });
            Optional<RealmType[]> studentsOptional = Optional.of(c.getStudents());
            studentsOptional.ifPresent(students -> {
                Integer[] characterStud = new Integer[RealmType.values().length];
                ModelView.insertStudents(characterStud, students);
                characterStudents.put(c.getId(), characterStud);
            });
        }
    }

    public boolean areNoEntryTilesPresents() {
        return noEntryTilesPresent;
    }

    public Pair<Integer, Integer> getNumNoEntryTilesOnCharacter() {
        return numNoEntryTilesOnCharacter;
    }

    public int getNoEntryTilesOnIsland(int island) {
        return islandsWithNoEntryTiles.get(island);
    }

    public int getCharacterPrice(int characterId) {
        return characters.get(characterId);
    }

    public boolean isCharacterWithStudents(int characterId) {
        return characterStudents.containsKey(characterId);
    }

    public Integer[] characterStudents(int characterId) {
        return characterStudents.get(characterId);
    }

    public void updateIslandNoEntryTiles(int newIslandNoEntryTiles, int island) {
        islandsWithNoEntryTiles.replace(island, newIslandNoEntryTiles);
    }
}
