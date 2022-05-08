package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class GameTest extends TestCase {
    Game game;
    GameConstants gameConstants;

    @BeforeEach
    void setup() {
        gameConstants = JsonUtils.constantsByNumPlayer(3);
        List<Island> islands = new ArrayList<>();
        Cloud[] clouds = new Cloud[3];
        for (int i = 0; i < 12; i++) {
            islands.add(new SingleIsland());
        }
        for (int i = 0; i < 3; i ++) {
            clouds[i] = new Cloud(gameConstants.getNumStudentsPerCloud());
        }
        game = new Game(islands, clouds, gameConstants);
    }


    @Test
    void addPlayerTest() { //TODO: need to check if is a correct way to test this method
        game.setNumPlayers(4);
        for(int i = 0; i < 4; i++){
            game.addPlayer("generic player");
        }
        game.addPlayer("exceeded player");
        Assertions.assertEquals(4,game.getNumPlayers());
        for (Player p: game.getPlayers()) {
            Assertions.assertEquals("generic player", p.getNickName());
        }
    }

    @Test
    void moveMotherNatureTest() {
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
        game.createCharacterCards();
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
        game.createAllStudentsForBag();
        Assertions.assertEquals(gameConstants.getNumTotalStudents(),game.getBag().getStudent().size());
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