package it.polimi.ingsw.client.GUI.items;

public class LobbyInfo {
    private int id;
    private int numPlayers;
    private String rules;
    private int connectedPlayers;

    public LobbyInfo(int id, int numPlayers, int connectedPlayers,  boolean rules) {
        this.id = id;
        this.numPlayers = numPlayers;
        this.rules = (rules) ? "expert" : "simple";
        this.connectedPlayers = connectedPlayers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    public void setConnectedPlayers(int connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
