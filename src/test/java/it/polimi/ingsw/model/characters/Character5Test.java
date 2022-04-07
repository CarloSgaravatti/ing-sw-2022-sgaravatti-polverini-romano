package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.SingleIsland;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Character5Test extends TestCase {
    Character5 character5;
    Island island;

    @BeforeEach
    void setupCharacter5() {
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            islands.add(new SingleIsland());
        }
        Game game = new Game(islands, null);
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            islands.get(i).addObserver(game);
        }
        CharacterCreator characterCreator = new CharacterCreator(game);
        character5 = (Character5) characterCreator.getCharacter(5);
        island = game.getIslands().get(new Random().nextInt(12));
    }

    @Test
    void noEntryTilesTest() {
        character5.putNoEntryTileInIsland(island);
        Assertions.assertEquals(3, character5.getNoEntryTiles());
        island.setMotherNaturePresent(true);
        Assertions.assertEquals(4, character5.getNoEntryTiles());
    }

    @Test
    void noEntryTilesExceptionTest() {
        for (int i = 0; i < 4; i++) character5.putNoEntryTileInIsland(island);
        try {
            character5.putNoEntryTileInIsland(island);
            Assertions.fail();
        } catch (IllegalStateException e) {
            //OK
        }
        for (int i = 0; i < 4; i++) {
            island.setMotherNaturePresent(true);
            island.setMotherNaturePresent(false);
        }
        try {
            character5.insertNoEntryTile();
            Assertions.fail();
        } catch (IllegalStateException e) {
            //OK
        }
    }
}