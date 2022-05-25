package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;

import java.util.List;
import java.util.function.Consumer;

public class CharacterWithoutInput extends CharacterCard {
    private final Consumer<Player> dynamicPlayCard;

    /**
     * Construct a CharacterCard which has the specified id and the specified price
     *
     * @param coinPrice character price
     * @param id        character id
     */
    public CharacterWithoutInput(int coinPrice, int id, Consumer<Player> dynamicPlayCard) {
        super(coinPrice, id);
        this.dynamicPlayCard = dynamicPlayCard;
    }

    @Override
    public void playCard(Player player) throws NotEnoughCoinsException {
        super.playCard(player);
        dynamicPlayCard.accept(player);
    }

    @Override
    public boolean requiresInput() {
        return false;
    }

    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        throw new IllegalCharacterActionRequestedException();
    }
}
