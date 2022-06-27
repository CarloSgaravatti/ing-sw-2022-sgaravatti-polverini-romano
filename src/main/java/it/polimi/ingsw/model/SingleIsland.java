package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.ArrayList;
import java.util.List;

public class SingleIsland extends Island {
    private final List<Student> students;
    private static final int NUM_TOWERS_PER_ISLAND = 1;

    public SingleIsland() {
        super();
        students = new ArrayList<>();
    }

    @Override
    public void putTower(TowerType t){
        super.setTowerType(t);
    }

    @Override
    public List<Student> getStudents(){
        return students;
    }

    @Override
    public void addStudent(Student s){
        this.students.add(s);
    }

    @Override
    public int getNumStudentsOfType (RealmType studentType) {
        return (int) this.students.stream()
                .filter((s) -> s.getStudentType() == studentType)
                .count();
    }

    @Override
    public int getNumTowers(){
        return NUM_TOWERS_PER_ISLAND;
    }

    public void setStudents(List<Student> students) {
        this.students.addAll(students);
    }
}
