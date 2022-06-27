package it.polimi.ingsw.messages.simpleModel;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SimplePlayer implements Serializable {
    private RealmType[] entrance;
    private RealmType[] diningRoom;
    private int numTowers;
    private final String nickname;
    private Map<Integer, Integer> assistants;
    private int numCoins;
    private Pair<Integer, Integer> lastAssistant;

    public SimplePlayer(String nickname) {
        this.nickname = nickname;
    }

    public SimplePlayer(String nickname, RealmType[] entrance, RealmType[] diningRoom, int numTowers, int numCoins) {
        this(nickname, entrance, numTowers, numCoins);
        this.diningRoom = diningRoom;
    }

    public SimplePlayer(String nickname, RealmType[] entrance) {
        this(nickname);
        this.entrance = entrance;
    }

    public SimplePlayer(String nickname, RealmType[] entrance, Map<Integer, Integer> assistants) {
        this(nickname, entrance);
        this.assistants = assistants;
    }

    public SimplePlayer(String nickname, RealmType[] entrance, int numTowers, int numCoins) {
        this(nickname, entrance);
        this.numTowers = numTowers;
        this.numCoins = numCoins;
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

    public Map<Integer, Integer> getAssistants() {
        return assistants;
    }

    public void setAssistants(Map<Integer, Integer> assistants) {
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

    public Pair<Integer, Integer> getLastAssistant() {
        return lastAssistant;
    }

    public void setLastAssistant(Pair<Integer, Integer> lastAssistant) {
        this.lastAssistant = lastAssistant;
    }
}
