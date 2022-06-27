package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;

import java.util.Map;

public class Character3 extends CharacterCard {
    private transient ModelObserver modelObserver;

    public Character3(Game game) {
        super(3, 3);
        this.modelObserver = game;
    }

    @Override
    public void useEffect(Map<String, Object> arguments) {
        Island island = (Island) arguments.get("Island");
        modelObserver.updateIslandTower(island);
    }

    @Override
    public void restoreCharacter(Game game) {
        this.modelObserver = game;
    }
}
