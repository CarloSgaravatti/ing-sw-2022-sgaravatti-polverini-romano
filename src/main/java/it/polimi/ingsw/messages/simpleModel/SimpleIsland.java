package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Triplet;

import java.io.Serializable;

public class SimpleIsland implements Serializable {
    private final Triplet<Integer[], Integer, TowerType> islandRepresentation;
    private int numEntryTiles;

    public SimpleIsland(Integer[] students, Integer towers, TowerType tower) {
        islandRepresentation = new Triplet<>(students, towers, tower);
    }

    public SimpleIsland(Integer[] students, Integer towers) {
        this(students, towers, null);
    }

    public Triplet<Integer[], Integer, TowerType> getIslandRepresentation() {
        return islandRepresentation;
    }

    public int getNumEntryTiles() {
        return numEntryTiles;
    }

    public void setNumEntryTiles(int numEntryTiles) {
        this.numEntryTiles = numEntryTiles;
    }
}
