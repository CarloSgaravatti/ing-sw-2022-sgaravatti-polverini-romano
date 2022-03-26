package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.model.effects.NormalInfluenceStrategy;
import it.polimi.ingsw.model.effects.StudentContainer;

public class TurnEffect {
    private int motherNatureMovement;
    private int orderPrecedence;
    private boolean professorPrecedence;
    private InfluenceStrategy influenceStrategy;
    private StudentContainer studentContainer;
    private boolean characterPlayed;
    private boolean characterEffectConsumed; //for those characters who have an active effect for all turn

    //TODO: there has to be a way to distinguish who has played an assistant first if they have the same value
    public TurnEffect(){
        reset();
    }

    public void reset() {
        motherNatureMovement = 0;
        orderPrecedence = 0;
        professorPrecedence = false;
        influenceStrategy = new NormalInfluenceStrategy();
        studentContainer = null;
        characterPlayed = false;
        characterEffectConsumed = true;
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

    public void incrementMotherNatureMovement(int incr) {
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

    public StudentContainer getStudentContainer() {
        return studentContainer;
    }

    public void setStudentContainer(StudentContainer studentContainer) {
        this.studentContainer = studentContainer;
    }

    //TODO: eliminate influence gain strategy and add an attribute influence gain
    public int getInfluence(Island island, Player player) {
        return influenceStrategy.getInfluence(island, player);
    }

    public boolean isCharacterPlayed() {
        return characterPlayed;
    }

    public void setCharacterPlayed(boolean characterPlayed) {
        this.characterPlayed = characterPlayed;
    }

    public boolean isCharacterEffectConsumed() {
        return characterEffectConsumed;
    }

    public void setCharacterEffectConsumed(boolean characterEffectConsumed) {
        this.characterEffectConsumed = characterEffectConsumed;
    }
}
