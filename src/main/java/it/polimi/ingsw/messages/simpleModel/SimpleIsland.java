package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Triplet;

import java.io.Serializable;

/**
 * SimpleIsland is an island representation that is used to send island updates during the game
 */
public class SimpleIsland implements Serializable {
    private final Triplet<Integer[], Integer, TowerType> islandRepresentation;
    private final int numEntryTiles;

    public SimpleIsland(Integer[] students, Integer towers, TowerType tower, int numEntryTiles) {
        islandRepresentation = new Triplet<>(students, towers, tower);
        this.numEntryTiles = numEntryTiles;
    }

    public Triplet<Integer[], Integer, TowerType> getIslandRepresentation() {
        return islandRepresentation;
    }

    public int getNumEntryTiles() {
        return numEntryTiles;
    }
}
