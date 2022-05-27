package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.*;

import java.util.List;

public class Character3 extends CharacterCard {
    //Temporary solution, maybe it can be done with less brute force
    private final transient ModelObserver modelObserver;
    private final List<Island> islands;

    public Character3(Game game) {
        super(3, 3);
        this.modelObserver = game;
        islands = game.getIslands();
    }
    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        Island island;
        int islandIndex;
        try {
            islandIndex = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        if (islandIndex < 0 || islandIndex >= islands.size()) throw new IllegalCharacterActionRequestedException();
        island = islands.get(islandIndex);
        chooseIslandToUpdate(island);
    }

    public void chooseIslandToUpdate(Island island) {
        modelObserver.updateIslandTower(island);
    }
}
