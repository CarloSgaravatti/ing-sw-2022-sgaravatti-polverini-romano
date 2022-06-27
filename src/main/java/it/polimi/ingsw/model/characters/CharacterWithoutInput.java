package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A CharacterWithoutInput is a CharacterCard that don't take any parameter from the player to play the card. This class
 * represent characters 2, 4, 6 and 8. All these characters have a different action that they will perform during the game if
 * the character is played: this action is injected in the character at the creation moment.
 */
public class CharacterWithoutInput extends CharacterCard {
    private Consumer<Player> dynamicPlayCard;

    /**
     * Construct a CharacterCard which has the specified id, the specified price and that will perform the specified
     * action when played in a turn. The action will modify the player turn effect.
     *
     * @param coinPrice character price
     * @param id        character id
     * @param dynamicPlayCard the effect of the character
     */
    public CharacterWithoutInput(int coinPrice, int id, Consumer<Player> dynamicPlayCard) {
        super(coinPrice, id);
        this.dynamicPlayCard = dynamicPlayCard;
    }

    /**
     *
     * @param player the player who wants to play the character
     * @throws NotEnoughCoinsException if the player doesn't have enough coins to play the character
     * @see CharacterCard
     */
    @Override
    public void playCard(Player player) throws NotEnoughCoinsException {
        super.playCard(player);
        dynamicPlayCard.accept(player);
    }

    @Override
    public boolean requiresInput() {
        return false;
    }

    @Override
    public void restoreCharacter(Game game) {
        this.dynamicPlayCard = new CharacterCreator(game).getCharacterWithNoInputAction(this.getId());
    }

    /**
     *
     * @param arguments the character parameters
     * @throws IllegalCharacterActionRequestedException
     */
    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        throw new IllegalCharacterActionRequestedException("This character doesn't have any input");
    }

    //TODO: decide if dynamicPlayCard.accept() have to be putted in the use effect method (so play card is not
    // reimplemented, which is better)
}
