package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.*;

import java.util.List;

public class Character3 extends CharacterCard {
    private static Character3 instance;
    //Temporary solution, maybe it can be done with less brute force
    private final ModelObserver modelObserver;
    //TODO: same as character 1
    private final List<Island> islands;

    protected Character3(Game game) {
        super(3, 3);
        this.modelObserver = game;
        islands = game.getIslands();
    }

    public static Character3 getInstance(Game game) {
        if (instance == null) instance = new Character3(game);
        return instance;
    }

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        Island island;
        try {
            island = islands.get(Integer.parseInt(args.get(0)));
        } catch (NumberFormatException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        chooseIslandToUpdate(island);
    }

    public void chooseIslandToUpdate(Island island) {
        modelObserver.updateIslandTower(island);
    }
}
