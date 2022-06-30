package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.model.effects.NormalInfluenceStrategy;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Class Turn effect represent all information of a player during his turn; this information is valid only during the
 * player turn and is reset at the end of the turn. This class contains: the last assistant card value that is played
 * (for order calculation), the maximum value of mother nature movement that the player can request, the addition influence
 * that a player have during the turn (if he plays character 8), a boolean that specifies if the player has a precedence
 * in the professor assignment (if he plays character 2), a boolean that specifies if the player has already played
 * a character during the turn and the current way of calculating the influence of an island (characters can modify
 * the way to calculate the influence).
 */
public class TurnEffect {
    private int motherNatureMovement;
    private int orderPrecedence;
    private boolean isFirstPlayedAssistant; //for when a player plays an assistant which is already played in the round
    private boolean professorPrecedence;
    private InfluenceStrategy influenceStrategy;
    private int additionalInfluence;
    private boolean characterPlayed;
    private transient final PropertyChangeSupport player;
    private Pair<Integer, Integer> lastPlayedAssistant;

    /**
     * Construct a TurnEffect which has all value reset. The created object is observed by the specified listener
     *
     * @param player the listener which will listen to the object
     */
    public TurnEffect(PropertyChangeListener player) {
        this();
        reset();
        this.player.addPropertyChangeListener(player);
        lastPlayedAssistant = new Pair<>(0, 0);
    }

    /**
     * Constructs a new turn effect that is not associated to any listener and that has all value reset
     */
    public TurnEffect() {
        player = new PropertyChangeSupport(this);
    }

    /**
     * Reset the values of the turn effect
     */
    public void reset() {
        motherNatureMovement = 0;
        orderPrecedence = 0;
        additionalInfluence = 0;
        professorPrecedence = false;
        influenceStrategy = new NormalInfluenceStrategy();
        characterPlayed = false;
        isFirstPlayedAssistant = true;
    }

    /**
     * Returns the value that will be used to calculate the order in the action phase. The value corresponds to the last
     * assistant played.
     *
     * @return the value that will be used to calculate the order in the action phase
     */
    public int getOrderPrecedence() {
        return orderPrecedence;
    }

    /**
     * Sets the value that will be used to calculate the order in the action phase
     *
     * @param orderPrecedence the value that will be used to calculate the order in the action phase
     */
    public void setOrderPrecedence(int orderPrecedence) {
        this.orderPrecedence = orderPrecedence;
        this.lastPlayedAssistant.setFirst(orderPrecedence);
    }

    /**
     * Returns the maximum movements of mother nature that the player associated to this turn effect can do
     *
     * @return the maximum movements of mother nature that the player associated to this turn effect can do
     */
    public int getMotherNatureMovement() {
        return motherNatureMovement;
    }

    /**
     * Increments the maximum movements of mother nature that the player associated to this turn effect can do
     *
     * @param incr the increment of the maximum mother nature movement
     * @param notify true if the event have to be notified, otherwise false
     */
    public void incrementMotherNatureMovement(int incr, boolean notify) {
        if (motherNatureMovement == 0) lastPlayedAssistant.setSecond(incr);
        if (notify) player.firePropertyChange("MotherNatureMovementIncrement", null, incr);
        motherNatureMovement += incr;
    }

    /**
     * Returns the influence strategy that the player will use to calculate influences during his turn
     *
     * @return the influence strategy that the player will use to calculate influences during his turn
     */
    public InfluenceStrategy getInfluenceStrategy() {
        return influenceStrategy;
    }

    /**
     * Sets the value of the influence strategy that the player will use to calculate influences during his turn
     *
     * @param influenceStrategy the value of the influence strategy that the player will use to
     *                          calculate influences during his turn
     */
    public void setInfluenceStrategy(InfluenceStrategy influenceStrategy) {
        this.influenceStrategy = influenceStrategy;
    }

    /**
     * Returns true if the player have a precedence in ties when calculating the player who can take the professor, otherwise
     * false.
     *
     * @return true if the player have a precedence in ties when calculating the player who can take the professor, otherwise
     *      false.
     */
    public boolean isProfessorPrecedence() {
        return professorPrecedence;
    }

    public void setProfessorPrecedence(boolean professorPrecedence) {
        this.professorPrecedence = professorPrecedence;
    }

    /**
     * Returns the influence of the specified player on the specified island, according to the influence strategy present
     * on this turn effect
     *
     * @param island the island on which the influence is calculated
     * @param player the player of which the returned influence regard
     * @return the influence of the specified player on the specified island
     */
    public int getInfluence(Island island, Player player) {
        return influenceStrategy.getInfluence(island, player);
    }

    /**
     * Returns true if a character has already been played by this player during the turn, otherwise false
     *
     * @return true if a character has already been played by this player during the turn, otherwise false
     */
    public boolean isCharacterPlayed() {
        return characterPlayed;
    }

    public void setCharacterPlayed(boolean characterPlayed) {
        this.characterPlayed = characterPlayed;
    }

    /**
     * Returns the additional influence that the player have in the influence calculation
     *
     * @return the additional influence that the player have in the influence calculation
     */
    public int getAdditionalInfluence() {
        return additionalInfluence;
    }

    /**
     * Sets the value of the additional influence that the player have in the influence calculation
     *
     * @param additionalInfluence the value of the additional influence that the player have in the influence calculation
     */
    public void setAdditionalInfluence(int additionalInfluence) {
        this.additionalInfluence = additionalInfluence;
    }

    /**
     * Returns true if the player is the first one who have played the assistant that he has played. This is used in the
     * order calculation in case players have played the same assistants (in situations that permit this)
     *
     * @return true if the player is the first one who have played the assistant that he has played
     */
    public boolean isFirstPlayedAssistant() {
        return isFirstPlayedAssistant;
    }

    public void setFirstPlayedAssistant(boolean firstPlayedAssistant) {
        isFirstPlayedAssistant = firstPlayedAssistant;
    }

    /**
     * Restore the turn effect listener after the game was restored from the persistence data
     *
     * @param player the listener of the turn effect
     */
    protected void restoreTurnEffect(PropertyChangeListener player) {
        this.player.addPropertyChangeListener(player);
    }

    /**
     * Returns the last played assistant that the player have played in the last turn, associated with the maximum
     * movement of mother nature that the assistant permit. This property isn't reset at the end of the turn, so it permit
     * to retrieve the last assistant even if the turn effect values are reset.
     *
     * @return the last played assistant that the player have played in the last turn, associated with the maximum
     *      movement of mother nature that the assistant permit
     */
    public Pair<Integer, Integer> getLastPlayedAssistant() {
        return lastPlayedAssistant;
    }
}
