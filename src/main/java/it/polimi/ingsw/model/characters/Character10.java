package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.FullDiningRoomException;
import it.polimi.ingsw.exceptions.IllegalCharacterActionRequestedException;
import it.polimi.ingsw.exceptions.StudentNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.util.ArrayList;
import java.util.List;

public class Character10 extends CharacterCard {

    public Character10() {
        super(1, 10);
    }

    @Override
    public void useEffect(List<String> args) throws IllegalCharacterActionRequestedException {
        int studentsToPick = Integer.parseInt(args.get(0));
        if (studentsToPick > 2 || studentsToPick <= 0) {
            throw new IllegalCharacterActionRequestedException();
        }
        RealmType[] fromEntrance = RealmType.getRealmsByAbbreviations(args.subList(1, studentsToPick + 1));
        RealmType[] toDiningRoom = RealmType.getRealmsByAbbreviations(args.
                subList(studentsToPick + 1, (2 * studentsToPick) + 1));
        try {
            swap(fromEntrance, toDiningRoom);
        } catch (StudentNotFoundException | FullDiningRoomException e) {
            throw new IllegalCharacterActionRequestedException(); //TODO: these exception will have different return messages
        }
    }

    public void swap(RealmType[] entrance, RealmType[] diningRoom) throws StudentNotFoundException,
            FullDiningRoomException {
        if (entrance.length != diningRoom.length || entrance.length <= 0 || entrance.length > 2) {
            throw new IllegalArgumentException();
        }
        School school = super.getPlayerActive().getSchool();
        List<Student> removedFromDiningRoom = new ArrayList<>();
        for (RealmType s: diningRoom) {
            removedFromDiningRoom.add(school.removeFromDiningRoom(s));
        }
        for (RealmType s: entrance) {
            school.moveFromEntranceToDiningRoom(s);
        }
        school.insertEntrance(removedFromDiningRoom.toArray(new Student[0]));
    }
}
