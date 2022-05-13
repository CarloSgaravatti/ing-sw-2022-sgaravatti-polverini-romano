package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.RealmType;

import java.io.Serializable;
import java.util.List;

public class SimplePlayer implements Serializable {
    private RealmType[] entrance;
    private RealmType[] diningRoom;
    private int numTowers;
    private final String nickname;
    private List<Integer> assistants;
    private int numCoins;

    public SimplePlayer(String nickname) {
        this.nickname = nickname;
    }

    public SimplePlayer(String nickname, RealmType[] entrance, RealmType[] diningRoom) {
        this(nickname);
        this.entrance = entrance;
        this.diningRoom = diningRoom;
    }

    public RealmType[] getEntrance() {
        return entrance;
    }

    public void setEntrance(RealmType[] entrance) {
        this.entrance = entrance;
    }

    public RealmType[] getDiningRoom() {
        return diningRoom;
    }

    public void setDiningRoom(RealmType[] diningRoom) {
        this.diningRoom = diningRoom;
    }

    public int getNumTowers() {
        return numTowers;
    }

    public void setNumTowers(int numTowers) {
        this.numTowers = numTowers;
    }

    public List<Integer> getAssistants() {
        return assistants;
    }

    public void setAssistants(List<Integer> assistants) {
        this.assistants = assistants;
    }

    public String getNickname() {
        return nickname;
    }

    public int getNumCoins() {
        return numCoins;
    }

    public void setNumCoins(int numCoins) {
        this.numCoins = numCoins;
    }
}
