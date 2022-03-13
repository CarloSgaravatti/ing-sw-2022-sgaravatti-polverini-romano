package it.polimi.ingsw.model;

public class SingleIsland extends Island {
    @Override
    public int getInfluence(Player p) {
        return 0;
    }

    @Override
    public void putTower(TowerType t){

    }
}
