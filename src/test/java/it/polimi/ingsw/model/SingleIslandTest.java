package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.model.characters.Character5;
import it.polimi.ingsw.model.effects.NoEntryTileManager;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SingleIslandTest extends TestCase {
    Island islandToTest;

    @BeforeEach
    void setupIsland() {
        islandToTest = new SingleIsland();
    }

    @ParameterizedTest
    @EnumSource(TowerType.class)
    void putTower(TowerType towerType) {
        Assertions.assertNull(islandToTest.getTowerType());
        islandToTest.putTower(towerType);
        Assertions.assertEquals(towerType, islandToTest.getTowerType());
    }

    @ParameterizedTest
    @CsvSource({"1, 2", "3, 0", "2, 10"})
    void getNumStudentsOfType(int realmIndex, int numToIns) {
        for (int i = 0; i < numToIns; i++) islandToTest.addStudent(new Student(RealmType.values()[realmIndex]));
        Assertions.assertEquals(numToIns, islandToTest.getNumStudentsOfType(RealmType.values()[realmIndex]));
    }

    Game setupGameSimulationEnvironment(int player1Students, int player2Students) {
        List<Island> islands = new ArrayList<>();
        Random rnd = new Random();
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(2);
        int islandToTestIndex = rnd.nextInt(gameConstants.getNumIslands());
        for (int i = 0; i < gameConstants.getNumIslands(); i++) {
            if (i == islandToTestIndex) islands.add(islandToTest);
            else islands.add(new SingleIsland());
        }
        Game game = new Game(islands, null, gameConstants, true); //clouds are not important
        game.setNumPlayers(2);
        game.addPlayer("player1");
        game.addPlayer("player2");
        islandToTest.addObserver(game);
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);
            player.setSchool(new School(8, TowerType.values()[i], gameConstants, player));
        }
        try {
            for (int j = 0; j < player1Students; j++)
                game.getPlayers().get(0).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
            for (int j = 0; j < player2Students; j++)
                game.getPlayers().get(1).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
        } catch (FullDiningRoomException e){
            Assertions.fail();
        }
        islandToTest.addStudent(new Student(RealmType.YELLOW_GNOMES));
        return game;
    }

    @ParameterizedTest
    @CsvSource({"3, 5", "2, 2", "2, 1"})
    void setMotherNaturePresent(int player1Students, int player2Students) {
        Game game = setupGameSimulationEnvironment(player1Students, player2Students);
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        TowerType towerTypeInIsland;
        if (player1Students > player2Students) {
            towerTypeInIsland = player1.getSchool().getTowerType();
            player1.getSchool().insertProfessor(RealmType.YELLOW_GNOMES);
        }
        else if (player1Students < player2Students) {
            towerTypeInIsland = player2.getSchool().getTowerType();
            player2.getSchool().insertProfessor(RealmType.YELLOW_GNOMES);
        }
        else towerTypeInIsland = null;
        islandToTest.setMotherNaturePresent(true);
        Assertions.assertEquals(towerTypeInIsland, islandToTest.getTowerType());
        Assertions.assertEquals(game.getIslands().size(), Island.NUM_ISLANDS);
    }

    @Test
    void insertNoEntryTile() {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(2);
        Game game = new Game(null, null, gameConstants, true);
        CharacterCreator characterCreator = new CharacterCreator(game);
        NoEntryTileManager character5 = (Character5) characterCreator.getCharacter(5);
        islandToTest.insertNoEntryTile(character5);
        islandToTest.insertNoEntryTile(character5);
        Assertions.assertEquals(2, islandToTest.getNoEntryTilePresents());
    }
}