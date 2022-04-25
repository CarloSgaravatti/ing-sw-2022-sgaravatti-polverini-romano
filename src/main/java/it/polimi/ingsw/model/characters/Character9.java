package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.TurnEffect;
import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.model.effects.NoStudentInfluenceStrategy;

import java.util.List;

public class Character9 extends CharacterCard {

    public Character9() {
        super(3, 9);
    }

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        RealmType studentType;
        try {
            studentType = RealmType.getRealmByAbbreviation(args.get(0));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        chooseStudentType(studentType);
    }

    public void chooseStudentType(RealmType studentType) {
        TurnEffect activeTurnEffect = super.getPlayerActive().getTurnEffect();
        InfluenceStrategy activeInfluenceStrategy = activeTurnEffect.getInfluenceStrategy();
        activeTurnEffect.setInfluenceStrategy(new NoStudentInfluenceStrategy(activeInfluenceStrategy, studentType));
    }
}
