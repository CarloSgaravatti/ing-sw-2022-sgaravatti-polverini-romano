package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.*;

import java.util.List;
import java.util.Map;

public class Character3 extends CharacterCard {
    //Temporary solution, maybe it can be done with less brute force
    private final transient ModelObserver modelObserver;

    public Character3(Game game) {
        super(3, 3);
        this.modelObserver = game;
    }

    @Override
    public void useEffect(Map<String, Object> arguments) {
        Island island = (Island) arguments.get("Island");
        modelObserver.updateIslandTower(island);
    }
}
