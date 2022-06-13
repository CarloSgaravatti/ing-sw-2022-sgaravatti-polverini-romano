package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.School;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Character10 extends CharacterCard {

    public Character10() {
        super(1, 10);
    }

    @Override
    public void useEffect(Map<String, Object> arguments) throws IllegalCharacterActionRequestedException {
        RealmType[] entrance = (RealmType[]) arguments.get("EntranceStudents");
        RealmType[] diningRoom = (RealmType[]) arguments.get("DiningRoomStudents");
        School school = super.getPlayerActive().getSchool();
        List<Student> studentsFromDiningRoom = new ArrayList<>();
        List<Student> studentsFromEntrance = new ArrayList<>();
        try {
            for (int i = 0; i < entrance.length; i++) {
                studentsFromEntrance.add(school.removeStudentEntrance(entrance[i]));
                studentsFromDiningRoom.add(school.removeFromDiningRoom(diningRoom[i], false));
            }
            school.insertDiningRoom(studentsFromEntrance.toArray(new Student[0]), false, true);
        } catch (StudentNotFoundException | FullDiningRoomException e) {
            studentsFromEntrance.forEach(school::insertEntrance);
            try {
                school.insertDiningRoom(studentsFromDiningRoom.toArray(new Student[0]), false, false);
            } catch (FullDiningRoomException ignored) {}
        }
        studentsFromDiningRoom.forEach(school::insertEntrance);
        firePropertyChange(new PropertyChangeEvent(this, "SchoolSwap", diningRoom, entrance));
    }
}
