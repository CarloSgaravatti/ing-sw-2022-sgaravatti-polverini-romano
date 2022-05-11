package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.School;
import it.polimi.ingsw.model.gameConstants.GameConstants;
import it.polimi.ingsw.utils.JsonUtils;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

class InfluenceStrategyTest extends TestCase {

    @ParameterizedTest
    @ArgumentsSource(InfluenceStrategyArgumentProvider.class)
    void getNormalInfluence(InfluenceStrategy influenceStrategy, RealmType realmType, int expected) {
        GameConstants gameConstants = JsonUtils.constantsByNumPlayer(2);
        Player player = new Player("player");
        Island island = new SingleIsland();
        for (int i = 0; i < RealmType.values().length; i++) {
            island.addStudent(new Student(RealmType.values()[i]));
        }
        player.setSchool(new School(8, TowerType.BLACK, gameConstants, player));
        try {
            player.getSchool().insertDiningRoom(new Student(realmType));
        } catch (FullDiningRoomException e) {
            Assertions.fail();
        }
        player.getSchool().insertProfessor(realmType); //this will be automatically done by game in real situations
        player.getSchool().sendTowerToIsland(island);
        player.getTurnEffect().setAdditionalInfluence(2); //for the last test
        int influence = influenceStrategy.getInfluence(island, player);
        Assertions.assertEquals(expected, influence);
    }
}

class InfluenceStrategyArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(new NormalInfluenceStrategy(), RealmType.YELLOW_GNOMES, 2),
                Arguments.of(new NoTowerInfluenceStrategy(new NormalInfluenceStrategy()), RealmType.YELLOW_GNOMES, 1),
                Arguments.of(new NoStudentInfluenceStrategy(new NormalInfluenceStrategy(), RealmType.YELLOW_GNOMES),
                        RealmType.YELLOW_GNOMES, 1),
                Arguments.of(new GainInfluenceStrategy(new NormalInfluenceStrategy()), RealmType.YELLOW_GNOMES, 4)
        );
    }
}