package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.EmptyBagException;
import junit.framework.TestCase;

import java.util.Random;

public class GameTest extends TestCase {

    public void testAddPlayer() {
        Game game = new Game(null, null);
        for(int i = 0; i < 4; i++){
            game.addPlayer("generic player");
        }
        game.addPlayer("exceded player");
        assertEquals(4,game.getNumPlayers());
    }


    public void testMoveMotherNature() throws EmptyBagException {
        InitController initcontr = new InitController();
        initcontr.initializeGameComponents();
        Game game = initcontr.getGame();
        Random rnd = new Random();
        int index = rnd.nextInt(game.getIslands().size());
        game.getIslands().get(index).setMotherNaturePresent(true);
        game.moveMotherNature(4);
        assertFalse(game.getIslands().get(index).isMotherNaturePresent());
        assertTrue(game.getIslands().get((index+4)%game.getIslands().size()).isMotherNaturePresent());
    }

    public void testCreateCharacterCard() {
    }
    
    public void testGenStudentForBeginning()throws EmptyBagException{
        Game game = new Game(null, null);
        game.genStudentForBeginning();
        assertEquals(10,game.getBag().getStudent().size());

    }

    public void testCreateAllStudentsForBag() throws EmptyBagException{
        InitController initcontr = new InitController();
        initcontr.initializeGameComponents();
        Game game = initcontr.getGame();
        assertEquals(120,game.getBag().getStudent().size());
    }

}