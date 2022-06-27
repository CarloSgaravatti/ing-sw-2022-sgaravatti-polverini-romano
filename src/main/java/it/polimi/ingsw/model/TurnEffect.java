package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.model.effects.NormalInfluenceStrategy;

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
    private transient final PropertyChangeSupport player = new PropertyChangeSupport(this);

    /**
     * Construct a TurnEffect which has all value reset. The created object is observed by the specified listener
     * @param player the listener which will listen to the object
     */
    public TurnEffect(PropertyChangeListener player) {
        reset();
        this.player.addPropertyChangeListener(player);
    }

    public void reset() {
        motherNatureMovement = 0;
        orderPrecedence = 0;
        additionalInfluence = 0;
        professorPrecedence = false;
        influenceStrategy = new NormalInfluenceStrategy();
        characterPlayed = false;
        isFirstPlayedAssistant = true;
    }

    public int getOrderPrecedence() {
        return orderPrecedence;
    }

    public void setOrderPrecedence(int orderPrecedence) {
        this.orderPrecedence = orderPrecedence;
    }

    public int getMotherNatureMovement() {
        return motherNatureMovement;
    }

    public void incrementMotherNatureMovement(int incr, boolean notify) {
        if (notify) player.firePropertyChange("MotherNatureMovementIncrement", null, incr);
        motherNatureMovement += incr;
    }

    public InfluenceStrategy getInfluenceStrategy() {
        return influenceStrategy;
    }

    public void setInfluenceStrategy(InfluenceStrategy influenceStrategy) {
        this.influenceStrategy = influenceStrategy;
    }

    public boolean isProfessorPrecedence() {
        return professorPrecedence;
    }

    public void setProfessorPrecedence(boolean professorPrecedence) {
        this.professorPrecedence = professorPrecedence;
    }

    public int getInfluence(Island island, Player player) {
        return influenceStrategy.getInfluence(island, player);
    }

    public boolean isCharacterPlayed() {
        return characterPlayed;
    }

    public void setCharacterPlayed(boolean characterPlayed) {
        this.characterPlayed = characterPlayed;
    }

    public int getAdditionalInfluence() {
        return additionalInfluence;
    }

    public void setAdditionalInfluence(int additionalInfluence) {
        this.additionalInfluence = additionalInfluence;
    }

    public boolean isFirstPlayedAssistant() {
        return isFirstPlayedAssistant;
    }

    public void setFirstPlayedAssistant(boolean firstPlayedAssistant) {
        isFirstPlayedAssistant = firstPlayedAssistant;
    }

    protected void restoreTurnEffect(PropertyChangeListener player) {
        this.player.addPropertyChangeListener(player);
    }
}
