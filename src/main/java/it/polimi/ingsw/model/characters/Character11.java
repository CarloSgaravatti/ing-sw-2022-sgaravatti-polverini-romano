package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;

import java.util.ArrayList;
import java.util.List;

public class Character11 extends CharacterCard implements StudentContainer {
    private final List<Student> students;
    private static Character11 instance;

    protected Character11() {
        super(2, 11);
        students = new ArrayList<>();
    }

    public static Character11 getInstance() {
        if (instance == null) instance = new Character11();
        return instance;
    }

    @Override
    public void playCard(Player player) {

    }

    @Override
    public List<Student> getStudents() {
        return null;
    }

    @Override
    public Student pickStudent(RealmType studentType) {
        return null;
    }

    @Override
    public void insertStudent(Student student) {

    }
}

