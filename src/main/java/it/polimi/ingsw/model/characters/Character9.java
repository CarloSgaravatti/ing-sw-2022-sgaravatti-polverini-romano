package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.RealmType;
import it.polimi.ingsw.model.TurnEffect;
import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.model.effects.NoStudentInfluenceStrategy;

public class Character9 extends CharacterCard {
    private static Character9 instance;

    protected Character9() {
        super(3, 9);
    }

    public static Character9 getInstance() {
        if (instance == null) instance = new Character9();
        return instance;
    }

    @SuppressWarnings("unused") //accessed with reflection
    public void chooseStudentType(RealmType studentType) {
        TurnEffect activeTurnEffect = super.getPlayerActive().getTurnEffect();
        InfluenceStrategy activeInfluenceStrategy = activeTurnEffect.getInfluenceStrategy();
        activeTurnEffect.setInfluenceStrategy(new NoStudentInfluenceStrategy(activeInfluenceStrategy, studentType));
    }
}
