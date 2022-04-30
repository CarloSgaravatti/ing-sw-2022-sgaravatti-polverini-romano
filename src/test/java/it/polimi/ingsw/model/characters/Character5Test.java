package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.SingleIsland;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
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
    Game game;
    GameConstants gameConstants;

    @BeforeEach
    void setupCharacter5() {
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            islands.add(new SingleIsland());
        }
        gameConstants = JsonUtils.constantsByNumPlayer(3);
        game = new Game(islands, null, gameConstants);
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            islands.get(i).addObserver(game);
        }
        CharacterCreator characterCreator = new CharacterCreator(game);
        character5 = (Character5) characterCreator.getCharacter(5);
        island = game.getIslands().get(new Random().nextInt(12));
    }

    @Test
    void useEffectTest() {
        List<String> args = new ArrayList<>();
        args.add(Integer.toString(game.getIslands().indexOf(island)));
        try {
            character5.useEffect(args);
        } catch (IllegalCharacterActionRequestedException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(3, character5.getNoEntryTiles());
        island.setMotherNaturePresent(true);
        Assertions.assertEquals(4, character5.getNoEntryTiles());
    }

    @Test
    void useEffectExceptionTest() {
        List<String> args = new ArrayList<>();
        args.add(Integer.toString(20));
        Assertions.assertThrows(IllegalCharacterActionRequestedException.class,
                () -> character5.useEffect(args));
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
            //Test passed
        }
    }
}