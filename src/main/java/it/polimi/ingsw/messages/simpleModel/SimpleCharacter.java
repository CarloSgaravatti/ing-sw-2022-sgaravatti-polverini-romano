package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.RealmType;

import java.io.Serializable;

public class SimpleCharacter implements Serializable {
    private RealmType[] students = null;
    private int numNoEntryTiles;
    private final int id;
    private final int price;

    public SimpleCharacter(int id, int price) {
        this.id = id;
        this.price = price;
    }

    public SimpleCharacter(RealmType[] students, int id, int price) {
        this(id, price);
        this.students = students;
    }

    public SimpleCharacter(int numEntryTiles, int id, int price) {
        this(id, price);
        this.numNoEntryTiles = numEntryTiles;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public RealmType[] getStudents() {
        return students;
    }

    public int getNumNoEntryTiles() {
        return numNoEntryTiles;
    }
}
