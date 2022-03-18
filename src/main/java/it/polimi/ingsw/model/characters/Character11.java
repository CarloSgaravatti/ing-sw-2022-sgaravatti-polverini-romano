package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;

public class Character11 extends CharacterCard {
    private Student[] students;
    private static Character11 instance;

    protected Character11() {
        super(2);
    }

    public static Character11 getInstance() {
        if (instance == null) instance = new Character11();
        return instance;
    }

    @Override
    public void playCard(Player player) {

    }

    public Student[] getStudents() {
        return students;
    }

    //Send to DiningRoom
    public void pickStudent(Student s){

    }

    public void insertStudent(Student s){

    }
}

