package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

public class Character4 extends CharacterCard {
    private static final int MOTHER_NATURE_INCREMENT = 2;

    public Character4() {
        super(1, 4);
    }

    @Override
    public void playCard(Player player) throws NotEnoughCoinsException {
        super.playCard(player);
        player.getTurnEffect().incrementMotherNatureMovement(MOTHER_NATURE_INCREMENT);
    }
}
