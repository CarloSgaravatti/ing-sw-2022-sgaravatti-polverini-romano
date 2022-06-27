package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Triplet;

import java.io.Serializable;

public class SimpleIsland implements Serializable {
    private final Triplet<Integer[], Integer, TowerType> islandRepresentation;
    private final int numEntryTiles;

    public SimpleIsland(Integer[] students, Integer towers, TowerType tower, int numEntryTiles) {
        islandRepresentation = new Triplet<>(students, towers, tower);
        this.numEntryTiles = numEntryTiles;
    }

    public SimpleIsland(Integer[] students, int numEntryTiles) {
        this(students, 1, null, 0);
    }

    public Triplet<Integer[], Integer, TowerType> getIslandRepresentation() {
        return islandRepresentation;
    }

    public int getNumEntryTiles() {
        return numEntryTiles;
    }
}
