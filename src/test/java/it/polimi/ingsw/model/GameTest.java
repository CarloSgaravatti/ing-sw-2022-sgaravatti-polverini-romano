package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.exceptions.EmptyBagException;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TODO: setup method
class GameTest extends TestCase {
    Game game;
    //TODO: don't use controller in model tests
    @BeforeEach
    void setup() {
        List<Island> islands = new ArrayList<>();
        Cloud[] clouds = new Cloud[3];
        for (int i = 0; i < 12; i++) {
            islands.add(new SingleIsland());
        }
        for (int i = 0; i < 3; i ++) {
            clouds[i] = new Cloud(3);
        }
        game = new Game(islands, clouds);
    }

    /*
    @Test
    void addPlayerTest() {
        Game game = new Game(null, null);
        game.setNumPlayers(4);
        for(int i = 0; i < 4; i++){
            game.addPlayer("generic player");
        }
        game.addPlayer("exceeded player");
        Assertions.assertEquals(4,game.getNumPlayers());
        for (Player p: game.getPlayers()) {
            Assertions.assertEquals("generic player", p.getNickName());
        }
    }*/

    @Test
    void moveMotherNatureTest() {
        /*InitController initcontr = new InitController();
        try {
            initcontr.initializeGameComponents(); //TODO: all the tests that do this doesn't end or have errors
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        Game game = initcontr.getGame();*/
        Random rnd = new Random();
        int index = rnd.nextInt(game.getIslands().size());
        game.getIslands().get(index).setMotherNaturePresent(true);
        game.moveMotherNature(4);
        Assertions.assertFalse(game.getIslands().get(index).isMotherNaturePresent());
        Assertions.assertTrue(game.getIslands().get((index+4)%game.getIslands().size()).isMotherNaturePresent());
    }

    @Test
    void createCharacterCardTest() {
        game.createAllStudentsForBag();
        game.createCharacterCard();
        Assertions.assertEquals(3, game.getCharacterCards().length);
        for (int i = 0; i < 3; i++) {
            Assertions.assertTrue(game.getCharacterCards()[i].getId() > 0
                    && game.getCharacterCards()[i].getId() <= 12);
        }
    }

    @Test
    void genStudentForBeginningTest() {
        game.genStudentForBeginning();
        Assertions.assertEquals(10,game.getBag().getStudent().size());
    }

    @Test
    void createAllStudentsForBagTest() {
        /*InitController initcontr = new InitController();
        try {
            initcontr.initializeGameComponents();
        } catch (EmptyBagException e) {
            Assertions.fail();
        }*/
        game.createAllStudentsForBag();
        //Game game = initcontr.getGame();
        Assertions.assertEquals(120,game.getBag().getStudent().size());
    }

    @Test
    void setupIslandsTest() {
        game.genStudentForBeginning();
        try {
            game.setupIslands();
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        List<Island> islands = game.getIslands();
        int motherNaturePosition = game.motherNaturePositionIndex();
        for (int i = 0; i < islands.size(); i++) {
            if (i == motherNaturePosition || i == (motherNaturePosition + 6) % islands.size()) {
                Assertions.assertTrue(islands.get(i).getStudents().isEmpty());
            } else {
                Assertions.assertEquals(1, islands.get(i).getStudents().size());
            }
        }
    }
}