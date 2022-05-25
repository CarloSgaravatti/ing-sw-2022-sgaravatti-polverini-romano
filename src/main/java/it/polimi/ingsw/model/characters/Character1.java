package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class Character1 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;
    //TODO: decide if this is necessary or it can be done better
    private final List<Island> islands;

    public Character1(Game game) {
        super(1, 1);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, game);
        this.islands = game.getIslands();
    }

    //To call this character you need to pass an RT abbreviation and an island index
    //I am ignoring additional arguments, but is just an idea
    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        RealmType studentType;
        Island island;
        try {
            studentType = RealmType.getRealmByAbbreviation(args.get(0));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        try {
            island = islands.get(Integer.parseInt(args.get(1)));
        } catch (NumberFormatException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        try {
            pickAndSendToIsland(studentType, island);
        } catch (StudentNotFoundException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        firePropertyChange(new PropertyChangeEvent(
                this, "Students", null, studentContainer.getStudents().toArray(new Student[0])));
    }

    public void pickAndSendToIsland(RealmType studentType, Island island) throws StudentNotFoundException {
        Student student = studentContainer.pickStudent(studentType, true);
        island.addStudents(student);
    }

    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}
