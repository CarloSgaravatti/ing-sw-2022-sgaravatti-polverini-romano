package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.StudentContainer;

public interface ModelObserver {
    void updateProfessorPresence(RealmType studentType);
    void updateIslandTower(Island island);
    //called online if there is an update in island tower
    void updateIslandUnification(Island island);
    void updateStudentContainer(StudentContainer studentContainer);
}
