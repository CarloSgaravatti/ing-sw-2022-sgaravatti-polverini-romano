package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.effects.GainInfluenceStrategy;
import it.polimi.ingsw.model.Player;

public class Character8 extends CharacterCard {
    private static Character8 instance;

    protected Character8() {
        super(2, 8);
    }

    public static Character8 getInstance() {
        if (instance == null) instance = new Character8();
        return instance;
    }

    @Override
    public void playCard(Player player) {
        player.getTurnEffect().setInfluenceStrategy(new GainInfluenceStrategy(player.getTurnEffect().getInfluenceStrategy()));
    }
}
