package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;

public class Character3 extends CharacterCard {
    private static Character3 instance;
    //Temporary solution, maybe it can be done with less brute force
    private final ModelObserver modelObserver;

    protected Character3(ModelObserver modelObserver) {
        super(3, 3);
        this.modelObserver = modelObserver;
    }

    public static Character3 getInstance(ModelObserver modelObserver) {
        if (instance == null) instance = new Character3(modelObserver);
        return instance;
    }

    @SuppressWarnings("unused") //accessed with reflection
    public void chooseIslandToUpdate(Island island) {
        modelObserver.updateIslandTower(island);
        modelObserver.updateIslandUnification(island);
    }
}
