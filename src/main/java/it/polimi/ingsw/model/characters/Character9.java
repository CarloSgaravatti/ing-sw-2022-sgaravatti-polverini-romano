package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.TurnEffect;
import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.model.effects.NoStudentInfluenceStrategy;

import java.util.List;
import java.util.Map;

public class Character9 extends CharacterCard {

    public Character9() {
        super(3, 9);
    }

    @Override
    public void useEffect(Map<String, Object> arguments) {
        RealmType studentType = (RealmType) arguments.get("Student");
        TurnEffect activeTurnEffect = super.getPlayerActive().getTurnEffect();
        InfluenceStrategy activeInfluenceStrategy = activeTurnEffect.getInfluenceStrategy();
        activeTurnEffect.setInfluenceStrategy(new NoStudentInfluenceStrategy(activeInfluenceStrategy, studentType));
    }

    @Override
    public void restoreCharacter(Game game) {/*does nothing*/}
}
