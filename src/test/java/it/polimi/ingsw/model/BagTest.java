package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BagTest extends TestCase {
    Bag bagToTest;

    @BeforeEach
    void setBagToTest() {
        bagToTest = new Bag();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    void bagTest(int numStudentsToIns) {
        Random rnd = new Random();
        int[] numPerType = new int[RealmType.values().length];
        int[] numPerTypeExtracted = new int[RealmType.values().length];
        for (int i = 0; i < numStudentsToIns; i++) {
            int realmIndex = rnd.nextInt(RealmType.values().length);
            bagToTest.insertStudent(new Student(RealmType.values()[realmIndex]));
            numPerType[realmIndex] ++;
        }
        try {
            for (int i = 0; i < numStudentsToIns; i++) {
                numPerTypeExtracted[bagToTest.pickStudent().getStudentType().ordinal()] ++;
            }
        } catch (EmptyBagException e) {
            Assertions.fail();
        }
        for (int i = 0; i < RealmType.values().length; i++) {
            Assertions.assertEquals(numPerType[i], numPerTypeExtracted[i]);
        }
        Assertions.assertThrows(EmptyBagException.class, () -> bagToTest.pickStudent());
    }
}