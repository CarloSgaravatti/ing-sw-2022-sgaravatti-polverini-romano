package it.polimi.ingsw.model;

public interface ModelObserver {
    void updateProfessorPresence(RealmType studentType);
    void updateIslandTower(Island island);
    void updateIslandUnification(Island island);
}
