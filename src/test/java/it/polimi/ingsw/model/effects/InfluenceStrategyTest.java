package it.polimi.ingsw.model.effects;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.model.*;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InfluenceStrategyTest extends TestCase {

    @ParameterizedTest
    @ArgumentsSource(InfluenceStrategyArgumentProvider.class)
    void getNormalInfluence(InfluenceStrategy influenceStrategy, RealmType realmType, int expected) {
        Player player = new Player("player");
        Island island = new SingleIsland();
        for (int i = 0; i < RealmType.values().length; i++) {
            island.addStudent(new Student(RealmType.values()[i]));
        }
        player.setSchool(new School(8, TowerType.BLACK));
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