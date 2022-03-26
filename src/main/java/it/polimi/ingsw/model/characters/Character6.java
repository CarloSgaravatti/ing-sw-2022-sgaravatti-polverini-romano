package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.effects.NoTowerInfluenceStrategy;
import it.polimi.ingsw.model.Player;

public class Character6 extends CharacterCard {
    private static Character6 instance;

    protected Character6() {
        super(3, 6);
    }

    public static Character6 getInstance() {
        if (instance == null) instance = new Character6();
        return instance;
    }

    @Override
    public void playCard(Player player) {
        player.getTurnEffect().setInfluenceStrategy(new NoTowerInfluenceStrategy(player.getTurnEffect().getInfluenceStrategy()));
    }
}
