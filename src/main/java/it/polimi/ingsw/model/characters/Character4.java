package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

import java.util.List;

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

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        throw new IllegalCharacterActionRequestedException();
    }
}
