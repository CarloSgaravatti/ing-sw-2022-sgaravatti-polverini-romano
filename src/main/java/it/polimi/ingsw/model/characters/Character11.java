package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.util.List;

public class Character11 extends CharacterCard {
    private final int MAX_NUM_STUDENTS = 4;
    private final StudentContainer studentContainer;

    public Character11(ModelObserver observer) {
        super(2, 11);
        studentContainer = new StudentContainer(MAX_NUM_STUDENTS, observer);
    }

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        RealmType studentType;
        try {
            studentType = RealmType.getRealmByAbbreviation(args.get(0));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalCharacterActionRequestedException();
        }
        try {
            pickAndSendToDiningRoom(studentType);
        } catch (StudentNotFoundException | FullDiningRoomException e) {
            throw new IllegalCharacterActionRequestedException();
        }
    }

    public void pickAndSendToDiningRoom(RealmType studentType) throws StudentNotFoundException, FullDiningRoomException {
        School school = super.getPlayerActive().getSchool();
        school.insertDiningRoom(studentContainer.pickStudent(studentType, true));
    }

    //For testing
    public List<Student> getStudents() {
        return studentContainer.getStudents();
    }
}

