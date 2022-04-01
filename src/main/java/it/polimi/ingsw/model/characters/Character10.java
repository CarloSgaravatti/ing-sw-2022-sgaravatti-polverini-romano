package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class Character10 extends CharacterCard {
    private static Character10 instance;

    protected Character10() {
        super(1, 10);
    }

    public static Character10 getInstance() {
        if (instance == null) instance = new Character10();
        return instance;
    }

    @SuppressWarnings("unused") //accessed with reflection
    public void swap(RealmType[] entrance, RealmType[] diningRoom) throws StudentNotFoundException, FullDiningRoomException {
        School school = super.getPlayerActive().getSchool();
        List<Student> removedFromDiningRoom = new ArrayList<>();
        for (RealmType s: diningRoom)
            removedFromDiningRoom.add(school.removeFromDiningRoom(s));
        for (RealmType s: entrance)
            school.moveFromEntranceToDiningRoom(s);
        school.insertEntrance(removedFromDiningRoom.toArray(new Student[0]));
    }
}
