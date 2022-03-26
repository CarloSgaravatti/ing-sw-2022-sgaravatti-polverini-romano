package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Character1 extends CharacterCard implements StudentContainer {
    private final List<Student> students;
    private static Character1 instance;
    private final int MAX_NUM_STUDENTS = 4;

    protected Character1() {
        super(1, 1);
        students = new ArrayList<>();
    }

    public static Character1 getInstance() {
        if (instance == null) instance = new Character1();
        return instance;
    }

    @Override
    public void playCard(Player player) {
        player.getTurnEffect().setStudentContainer(this);
    }

    @Override
    public List<Student> getStudents() {
        return students;
    }

    @Override
    public Student pickStudent(RealmType studentType) throws StudentNotFoundException {
        Optional<Student> student = students.stream()
                .filter(s -> s.getStudentType() == studentType)
                .findAny();
        if (student.isEmpty()) throw new StudentNotFoundException();
        return student.get();
    }

    @Override
    public void insertStudent(Student student) {
        students.add(student);
    }
}
