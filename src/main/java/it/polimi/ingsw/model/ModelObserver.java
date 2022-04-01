package it.polimi.ingsw.model;

import it.polimi.ingsw.model.effects.StudentContainer;

public interface ModelObserver {
    void updateProfessorPresence(RealmType studentType);
    void updateIslandTower(Island island);
    void updateIslandUnification(Island island);
    void updateStudentContainer(StudentContainer studentContainer);
}
