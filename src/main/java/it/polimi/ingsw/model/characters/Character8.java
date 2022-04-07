package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.effects.GainInfluenceStrategy;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class Character8 extends CharacterCard {
    private static final int ADDITIONAL_INFLUENCE = 2;

    public Character8() {
        super(2, 8);
    }

    @Override
    public void playCard(Player player) throws NotEnoughCoinsException {
        super.playCard(player);
        player.getTurnEffect().setInfluenceStrategy(new GainInfluenceStrategy(player.getTurnEffect().getInfluenceStrategy()));
        player.getTurnEffect().setAdditionalInfluence(ADDITIONAL_INFLUENCE);
    }

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        throw new IllegalCharacterActionRequestedException();
    }
}
