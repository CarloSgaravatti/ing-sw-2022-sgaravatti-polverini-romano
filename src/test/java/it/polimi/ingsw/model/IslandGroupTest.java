package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IslandGroupTest extends TestCase {
    @Test
    void islandGroupTest() {
        Island[] islands = new Island[3];
        for (int i = 0; i < 3; i++) {
            islands[i] = new SingleIsland();
            for (int j = 0; j < 2; j++) {
                islands[i].addStudent(new Student(RealmType.values()[i]));
            }
            islands[i].putTower(TowerType.BLACK);
        }
        Island islandGroup = new IslandGroup(islands);
        Assertions.assertEquals(3, islandGroup.getNumTowers());
        Assertions.assertEquals(TowerType.BLACK, islandGroup.getTowerType());
        for (int i = 0; i < 3; i++) {
            Assertions.assertTrue(islandGroup.getStudents().containsAll(islands[i].getStudents()));
            Assertions.assertEquals(islandGroup.getNumStudentsOfType(RealmType.values()[i]),
                    islands[i].getNumStudentsOfType(RealmType.values()[i]));
        }
        islandGroup.putTower(TowerType.WHITE);
        Assertions.assertEquals(TowerType.WHITE, islandGroup.getTowerType());
        Island island = new SingleIsland();
        island.putTower(TowerType.WHITE);
        Island islandGroup2 = new IslandGroup(islandGroup, island);
        Assertions.assertEquals(4, islandGroup2.getNumTowers());
        Assertions.assertEquals(TowerType.WHITE, islandGroup2.getTowerType());
        Student student = new Student(RealmType.YELLOW_GNOMES);
        islandGroup2.addStudent(student);
        Assertions.assertTrue(islandGroup2.getStudents().contains(student));
    }
}