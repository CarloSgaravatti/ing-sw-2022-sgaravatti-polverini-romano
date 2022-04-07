package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Character3Test extends TestCase {
    Character3 character3;
    Game game;

    @BeforeEach
    void setupCharacter3() {
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            islands.add(new SingleIsland());
        }
        game = new Game(islands, null);
        for (int i = 0; i < Island.NUM_ISLANDS; i++) {
            islands.get(i).addObserver(game);
        }
        CharacterCreator characterCreator = new CharacterCreator(game);
        character3 = (Character3) characterCreator.getCharacter(3);
    }

    @ParameterizedTest
    @ValueSource(ints = {0})
    void chooseIslandToUpdate(int islandIndex) {
        List<Island> islands = game.getIslands();
        game.addPlayer("player1");
        game.addPlayer("player2");
        game.getPlayers().get(0).setSchool(new School(8, TowerType.BLACK));
        game.getPlayers().get(1).setSchool(new School(8, TowerType.WHITE));
        game.getPlayers().get(0).getSchool().addObserver(game);
        game.getPlayers().get(1).getSchool().addObserver(game);
        try {
            game.getPlayers().get(0).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
        } catch (FullDiningRoomException e) {
            Assertions.fail();
        }
        islands.get(islandIndex).addStudent(new Student(RealmType.YELLOW_GNOMES));
        islands.get((islandIndex + islands.size() - 1) % islands.size()).putTower(TowerType.BLACK);
        islands.get((islandIndex + 1) % islands.size()).putTower(TowerType.BLACK);
        int minIndexIslandToUnify = Math.min(Math.min(islandIndex, (islandIndex + 1) % islands.size()),
                (islandIndex + islands.size() - 1) % islands.size());
        character3.chooseIslandToUpdate(islands.get(islandIndex));
        Assertions.assertEquals(10, islands.size());
        Assertions.assertEquals(3, islands.get(minIndexIslandToUnify).getNumTowers());
        Assertions.assertEquals(TowerType.BLACK, islands.get(minIndexIslandToUnify).getTowerType());
    }

    @Test
    void useEffectTest() {
        List<Island> islands = game.getIslands();
        game.addPlayer("player1");
        game.addPlayer("player2");
        game.getPlayers().get(0).setSchool(new School(8, TowerType.BLACK));
        game.getPlayers().get(1).setSchool(new School(8, TowerType.WHITE));
        game.getPlayers().get(0).getSchool().addObserver(game);
        game.getPlayers().get(1).getSchool().addObserver(game);
        try {
            game.getPlayers().get(0).getSchool().insertDiningRoom(new Student(RealmType.YELLOW_GNOMES));
        } catch (FullDiningRoomException e) {
            Assertions.fail();
        }
        islands.get(0).addStudent(new Student(RealmType.YELLOW_GNOMES));
        islands.get((islands.size() - 1) % islands.size()).putTower(TowerType.BLACK);
        islands.get(1).putTower(TowerType.BLACK);
        List<String> args = new ArrayList<>();
        args.add("0");
        try {
            character3.useEffect(args);
        } catch (IllegalCharacterActionRequestedException e) {
            Assertions.fail();
        }
        Assertions.assertEquals(10, islands.size());
        Assertions.assertEquals(3, islands.get(0).getNumTowers());
        Assertions.assertEquals(TowerType.BLACK, islands.get(0).getTowerType());
    }
}