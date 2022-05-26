package it.polimi.ingsw.model.enumerations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RealmTypeTest {

    @BeforeEach
    void setup() {}

    @Test
    void getIntegerRepresentationTest() {
        RealmType[] realmTypes = new RealmType[] {
                RealmType.YELLOW_GNOMES, RealmType.BLUE_UNICORNS, RealmType.RED_DRAGONS, RealmType.RED_DRAGONS
        };
        Integer[] integerRepresentation = RealmType.getIntegerRepresentation(realmTypes);
        Assertions.assertEquals(1, integerRepresentation[0]);
        Assertions.assertEquals(1, integerRepresentation[1]);
        Assertions.assertEquals(0, integerRepresentation[2]);
        Assertions.assertEquals(2, integerRepresentation[3]);
        Assertions.assertEquals(0, integerRepresentation[4]);
    }

    @Test
    void getRealmTypeByIntegerRepresentationTest() {
        Integer[] integerRepresentation = new Integer[] {0, 1, 3, 7, 1};
        RealmType[] realmTypes = RealmType.getRealmsFromIntegerRepresentation(integerRepresentation);
        Integer[] res = new Integer[RealmType.values().length];
        Arrays.fill(res, 0);
        for (RealmType realmType : realmTypes) {
            res[realmType.ordinal()]++;
        }
        for (int i = 0; i < res.length; i++) {
            Assertions.assertEquals(integerRepresentation[i], res[i]);
        }
    }
}