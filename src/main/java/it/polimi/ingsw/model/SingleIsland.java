package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.ArrayList;
import java.util.List;

/**
 * SingleIsland is an Island implementation that represent a non-unified island in the game
 */
public class SingleIsland extends Island {
    private final List<Student> students;
    private static final int NUM_TOWERS_PER_ISLAND = 1;

    /**
     * Constructs a new empty single island with no students in it
     */
    public SingleIsland() {
        super();
        students = new ArrayList<>();
    }

    /**
     * Insert a tower in the SingleIsland
     *
     * @param t the inserted tower
     */
    @Override
    public void putTower(TowerType t){
        super.setTowerType(t);
    }

    /**
     * Returns the students that are present in the single island
     *
     * @return the students that are present in the single island
     */
    @Override
    public List<Student> getStudents(){
        return students;
    }

    /**
     * Adds a students in the single island
     *
     * @param s the student that will be added
     */
    @Override
    public void addStudent(Student s){
        this.students.add(s);
    }

    /**
     * Returns the number of students of the specified type that are present on the single island
     *
     * @param studentType the type of students
     * @return the number of students of the specified type that are present on the single island
     */
    @Override
    public int getNumStudentsOfType (RealmType studentType) {
        return (int) this.students.stream()
                .filter((s) -> s.getStudentType() == studentType)
                .count();
    }

    /**
     * Returns the number of tower that a single island can take. This is always 1
     *
     * @return the number of tower that a single island can take
     */
    @Override
    public int getNumTowers(){
        return NUM_TOWERS_PER_ISLAND;
    }

    /**
     * Set the students of the single island
     *
     * @param students the students of the single island
     */
    public void setStudents(List<Student> students) {
        this.students.addAll(students);
    }
}
