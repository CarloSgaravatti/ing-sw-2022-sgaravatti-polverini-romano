package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.messages.simpleModel.SimpleCharacter;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.util.*;

/**
 * ExpertFieldView contains all information that are present only in an expert game, therefore it is used only for
 * expert games.
 */
public class ExpertFieldView {
    private final Map<Integer, Integer> characters = new HashMap<>(); //id and price
    private boolean noEntryTilesPresent = false;
    private Pair<Integer, Integer> numNoEntryTilesOnCharacter; //id and no entry tiles
    private final Map<Integer, Integer> islandsWithNoEntryTiles = new HashMap<>(); //islandId and no entry tiles on island
    private final Map<Integer, Integer[]> characterStudents = new HashMap<>(); //id and students

    /**
     * Constructs an ExpertFieldView that have the specified characters
     *
     * @param characters all information about the characters of the game
     * @see SimpleCharacter
     */
    public ExpertFieldView(List<SimpleCharacter> characters) {
        for (SimpleCharacter c : characters) {
            this.characters.put(c.getId(), c.getPrice());
            Optional<Integer> noEntryTiles = Optional.ofNullable(c.getNumNoEntryTiles());
            noEntryTiles.ifPresent(numNoEntryTiles -> {
                noEntryTilesPresent = true;
                numNoEntryTilesOnCharacter = new Pair<>(c.getId(), numNoEntryTiles);
            });
            Optional<RealmType[]> studentsOptional = Optional.ofNullable(c.getStudents());
            studentsOptional.ifPresent(students -> {
                Integer[] characterStud = new Integer[RealmType.values().length];
                Arrays.fill(characterStud, 0);
                ModelView.insertStudents(characterStud, students);
                characterStudents.put(c.getId(), characterStud);
            });
        }
    }

    /**
     * Returns true if the game deals with no entry tiles on islands, otherwise false
     *
     * @return true if the game deals with no entry tiles on islands, otherwise false
     */
    public boolean areNoEntryTilesPresents() {
        return noEntryTilesPresent;
    }

    /**
     * Returns the id character that contains no entry tiles associated with the number of no entry tiles that he contain
     *
     * @return the id character that contains no entry tiles associated with the number of no entry tiles that he contain
     */
    public Pair<Integer, Integer> getNumNoEntryTilesOnCharacter() {
        return numNoEntryTilesOnCharacter;
    }

    /**
     * Returns the number of no entry tiles present on the specified island
     *
     * @param island the id of the island
     * @return the number of no entry tiles present on the island
     */
    public Integer getNoEntryTilesOnIsland(int island) {
        return (islandsWithNoEntryTiles.get(island) != null) ? islandsWithNoEntryTiles.get(island) : 0;
    }

    /**
     * Set the number of no entry tiles to 0 on the specified island
     *
     * @param island the id of the island
     */
    public void resetNoEntryTilesOnIsland(int island) {
        islandsWithNoEntryTiles.remove(island);
    }

    /**
     * Returns the current price that have to be paid in order to play the specified character
     *
     * @param characterId the id of the character
     * @return the price of the character
     */
    public int getCharacterPrice(int characterId) {
        return characters.get(characterId);
    }

    /**
     * Returns true if the specified character contains students, otherwise false
     *
     * @param characterId the id of the character
     * @return true if the character contains students, otherwise false
     */
    public boolean isCharacterWithStudents(int characterId) {
        return characterStudents.containsKey(characterId);
    }

    /**
     * Returns the students that are present on the specified character
     *
     * @param characterId the id of the character
     * @return the students that are present on the character
     */
    public Integer[] characterStudents(int characterId) {
        return characterStudents.get(characterId);
    }

    /**
     * Updates the number of no entry tiles of the specified island
     *
     * @param newIslandNoEntryTiles the new number of no entry tiles
     * @param island the id of the island
     */
    public void updateIslandNoEntryTiles(int newIslandNoEntryTiles, int island) {
        if (islandsWithNoEntryTiles.containsKey(island)) {
            islandsWithNoEntryTiles.replace(island, newIslandNoEntryTiles);
        } else {
            islandsWithNoEntryTiles.put(island, newIslandNoEntryTiles);
        }
    }

    /**
     * Insert a no entry tile on the island
     *
     * @param island the id of the island
     */
    public void insertNoEntryTileOnIsland(int island) {
        if (islandsWithNoEntryTiles.containsKey(island)) {
            updateIslandNoEntryTiles(islandsWithNoEntryTiles.get(island) + 1, island);
        } else {
            islandsWithNoEntryTiles.put(island, 1);
        }
        numNoEntryTilesOnCharacter = new Pair<>(numNoEntryTilesOnCharacter.getFirst(), numNoEntryTilesOnCharacter.getSecond() - 1);
    }

    /**
     * Updates the students of the specified characters by replacing them with the specified students
     *
     * @param characterId the id of the character
     * @param students the new students that are present on the character
     */
    public void updateCharacterStudents(int characterId, RealmType[] students) {
        Integer[] newStudents = new Integer[RealmType.values().length];
        Arrays.fill(newStudents, 0);
        ModelView.insertStudents(newStudents, students);
        characterStudents.replace(characterId, newStudents);
    }

    /**
     * Updates the number of no entry tiles that are present on a character
     *
     * @param newNoEntryTiles the new number of no entry tiles present on a character
     */
    public void updateNoEntryTilesOnCharacter(int newNoEntryTiles) {
        numNoEntryTilesOnCharacter.setSecond(newNoEntryTiles);
    }

    /**
     * Returns all characters associated with their prices
     *
     * @return all characters associated with their prices
     */
    public Map<Integer, Integer> getCharacters() {
        return characters;
    }
}
