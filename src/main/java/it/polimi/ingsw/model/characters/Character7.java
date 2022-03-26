package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.ArrayList;
import java.util.List;

public class Character7 extends CharacterCard implements StudentContainer {
    private final int NUM_STUDENTS = 6;
    private final List<Student> students;
    private static Character7 instance;

    protected Character7() {
        super(1, 7);
        students = new ArrayList<>();
    }

    public static Character7 getInstance() {
        if (instance == null) instance = new Character7();
        return instance;
    }

    @Override
    public void playCard(Player player) {
        player.getTurnEffect().setStudentContainer(this);
    }

    @Override
    public Student pickStudent(RealmType studentType) {
        return null;
    }

    @Override
    public void insertStudent(Student student) {

    }

    @Override
    public List<Student> getStudents() {
        return null;
    }
}
