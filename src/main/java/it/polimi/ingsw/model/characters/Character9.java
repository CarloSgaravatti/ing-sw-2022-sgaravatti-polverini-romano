package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.TurnEffect;
import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.model.effects.NoStudentInfluenceStrategy;

import java.util.Map;

/**
 * Character9 is a CharacterCard that can change the influence strategy of the player who plays the character with
 * an NoStudentInfluenceStrategy.
 *
 * @see it.polimi.ingsw.model.CharacterCard
 * @see NoStudentInfluenceStrategy
 */
public class Character9 extends CharacterCard {

    public Character9() {
        super(3, 9);
    }

    /**
     * Uses the effect of the character by binding to the active player of the character a NoStudentInfluenceStrategy. The
     * student type that will not be counted is present in the map with the "Student" key
     * @param arguments the character parameters
     */
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
