package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;

public class Character1 extends CharacterCard {
    private Student[] students;
    private static Character1 instance;

    protected Character1() {
        super(1);
    }

    public static Character1 getInstance() {
        if (instance == null) instance = new Character1();
        return instance;
    }

    @Override
    public void playCard(Player player) {

    }

    public Student[] getStudents() {
        return students;
    }

    //Send to island
    public void pickStudent(Student s){

    }

    public void insertStudent(Student s){

    }
}
