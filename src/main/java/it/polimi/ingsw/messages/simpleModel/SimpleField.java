package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.RealmType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleField implements Serializable {
    private final List<SimpleIsland> islands;
    private final Map<Integer, Integer[]> clouds;
    private final List<SimpleCharacter> characters;
    private final String[] professorsOwners = new String[RealmType.values().length];
    private final int motherNaturePosition;

    public SimpleField(List<SimpleIsland> islands, Map<Integer, Integer[]> clouds,
                       List<SimpleCharacter> characters, int motherNaturePosition) {
        this.islands = islands;
        this.clouds = clouds;
        this.characters = characters;
        this.motherNaturePosition = motherNaturePosition;
    }

    public SimpleField(List<SimpleIsland> islands, Map<Integer, Integer[]> clouds, int motherNaturePosition) {
        this(islands, clouds, new ArrayList<>(), motherNaturePosition);
    }

    public List<SimpleIsland> getIslands() {
        return islands;
    }

    public Map<Integer, Integer[]> getClouds() {
        return clouds;
    }

    public List<SimpleCharacter> getCharacters() {
        return characters;
    }

    public int getMotherNaturePosition() {
        return motherNaturePosition;
    }

    public String[] getProfessorsOwners() {
        return professorsOwners;
    }

    public void setOwnerOf(RealmType realm, String owner) {
        professorsOwners[realm.ordinal()] = owner;
    }
}
