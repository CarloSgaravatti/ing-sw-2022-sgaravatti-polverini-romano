package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.SingleIsland;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Character5Test extends TestCase {
    Character5 character5;
    Island island;

    @BeforeEach
    void setupCharacter5() {
        character5 = (Character5) CharacterCreator.getInstance().getCharacter(5);
        island = new SingleIsland();
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