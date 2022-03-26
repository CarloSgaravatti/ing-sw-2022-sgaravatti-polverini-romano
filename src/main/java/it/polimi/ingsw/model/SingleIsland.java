package it.polimi.ingsw.model;

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
    public int getInfluence(Player p) {
        int res = 0;
        for (RealmType r: RealmType.values()) {
            if (p.getSchool().isProfessorPresent(r))
                res += getNumStudentsOfType(r);
        }
        if (this.getTowerType() == p.getSchool().getTowerType())
            res ++;
        return res;
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
}
