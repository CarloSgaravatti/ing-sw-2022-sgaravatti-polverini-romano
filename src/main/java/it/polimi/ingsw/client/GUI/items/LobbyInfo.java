package it.polimi.ingsw.client.GUI.items;

/**
 * LobbyInfo is a bean that represent the information of a specific game that will be printed on the global lobby table.
 */
public class LobbyInfo {
    private int id;
    private int numPlayers;
    private String rules;
    private int connectedPlayers;

    /**
     * Constructs a LobbyInfo for a game that have the specified id, number of players, number of connected players and rules.
     *
     * @param id the id of the game
     * @param numPlayers the number of players
     * @param connectedPlayers the number of connected players
     * @param rules the rules of the game
     */
    public LobbyInfo(int id, int numPlayers, int connectedPlayers,  boolean rules) {
        this.id = id;
        this.numPlayers = numPlayers;
        this.rules = (rules) ? "expert" : "simple";
        this.connectedPlayers = connectedPlayers;
    }

    /**
     * Returns the id of the game
     *
     * @return the id of the game
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id of the game
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the number of players the game
     *
     * @return the number of players the game
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Returns the number of players connected to the game
     *
     * @return the number of players connected to the game
     */
    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    /**
     * Sets the value of the number of players connected of the game
     */
    public void setConnectedPlayers(int connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }

    /**
     * Sets the value of the number of players of the game
     */
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public String getRules() {
        return rules;
    }

    /**
     * Sets the value of the type of rules of the game
     */
    public void setRules(String rules) {
        this.rules = rules;
    }
}
