package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Character3Test extends TestCase {
    Character3 character3;
    Game game;
    GameConstants gameConstants;

    @BeforeEach
    void setupCharacter3() {
        gameConstants = JsonUtils.constantsByNumPlayer(3);
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < gameConstants.getNumIslands(); i++) {
            islands.add(new SingleIsland());
        }
        game = new Game(islands, null, gameConstants, true);
        for (int i = 0; i < gameConstants.getNumIslands(); i++) {
            islands.get(i).addObserver(game);
        }
        CharacterCreator characterCreator = new CharacterCreator(game);
        character3 = (Character3) characterCreator.getCharacter(3);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 5, 11})
    void useEffectTest(int islandIndex) {
        List<Island> islands = game.getIslands();
        game.setNumPlayers(2);
        game.addPlayer("player1");
        game.addPlayer("player2");
        game.getPlayers().get(0).setSchool(new School(8, TowerType.BLACK, gameConstants, null));
        game.getPlayers().get(1).setSchool(new School(8, TowerType.WHITE, gameConstants, null));
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
        Map<String, Object> input = Map.of("Island", islands.get(islandIndex));
        character3.useEffect(input);
        Assertions.assertEquals(10, islands.size());
        Assertions.assertEquals(3, islands.get(minIndexIslandToUnify).getNumTowers());
        Assertions.assertEquals(TowerType.BLACK, islands.get(minIndexIslandToUnify).getTowerType());
    }
}