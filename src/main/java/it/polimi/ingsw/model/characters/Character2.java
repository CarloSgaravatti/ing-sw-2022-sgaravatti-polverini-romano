package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class Character2 extends CharacterCard {
    private static Character2 instance;

    protected Character2() {
        super(2, 2);
    }

    public static Character2 getInstance() {
        if (instance == null) instance = new Character2();
        return instance;
    }

    @Override
    public void playCard(Player player) throws NotEnoughCoinsException {
        super.playCard(player);
        player.getTurnEffect().setProfessorPrecedence(true);
    }

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        throw new IllegalCharacterActionRequestedException();
    }
}
