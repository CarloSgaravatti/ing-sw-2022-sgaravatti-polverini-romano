package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.model.characters.Character5;
import it.polimi.ingsw.model.effects.NoEntryTileManager;
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
        int islandToTestIndex = rnd.nextInt(Island.NUM_ISLANDS);
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            if (i == islandToTestIndex) islands.add(islandToTest);
            else islands.add(new SingleIsland());
        }
        Game game = new Game(islands, null); //clouds are not important
        CharacterCreator characterCreator = CharacterCreator.getInstance();
        characterCreator.setGame(game);
        game.addPlayer("player1");
        game.addPlayer("player2");
        islandToTest.addObserver(game);
        for (int i = 0; i < game.getPlayers().size(); i++) {
            game.getPlayers().get(i).setSchool(new School(8, TowerType.values()[i]));
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
        NoEntryTileManager character5 = (Character5) CharacterCreator.getInstance().getCharacter(5);
        islandToTest.insertNoEntryTile(character5);
        islandToTest.insertNoEntryTile(character5);
        Assertions.assertEquals(2, islandToTest.getNoEntryTilePresents());
    }
}