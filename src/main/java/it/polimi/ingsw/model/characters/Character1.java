package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Student;

public class Character1 extends CharacterCard {
    private Student[] students;

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
