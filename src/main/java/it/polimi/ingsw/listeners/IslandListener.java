package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.RealmType;
import it.polimi.ingsw.model.TowerType;
import it.polimi.ingsw.server.RemoteView;

import java.util.List;

public class IslandListener implements ModelListener {
    private final RemoteView remoteView;

    public IslandListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    //TODO: missing method of remote view for sending the message
    //  and missing header name


    //TODO: carlo questo lo implementi te
    /*public void eventPerformed(List<RealmType> realmTypes){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("RealmTypes", realmTypes);
    }*/
    public void eventPerformed(TowerType type, int indexIsland){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("TowerType", type);
        messagePayload.setAttribute("indexIsland", indexIsland);
    }

    public void eventPerformed(List<Integer> islandIndexes){
        MessagePayload messagePayload= new MessagePayload();
        messagePayload.setAttribute("islandIndexes", islandIndexes);
    }
}
