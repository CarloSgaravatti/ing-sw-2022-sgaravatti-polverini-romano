package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.server.RemoteView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class IslandListener implements PropertyChangeListener {
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
        remoteView.sendMessage(messagePayload, "IslandStudentsUpdate", ServerMessageType.GAME_UPDATE);
    }*/

    public void eventPerformed(TowerType type, int indexIsland) {
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("TowerType", type);
        messagePayload.setAttribute("indexIsland", indexIsland);
        remoteView.sendMessage(messagePayload, "IslandTowerUpdate", ServerMessageType.GAME_UPDATE);
    }

    public void eventPerformed(List<Integer> islandIndexes){
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("islandIndexes", islandIndexes);
        remoteView.sendMessage(messagePayload, "IslandUnificationUpdate", ServerMessageType.GAME_UPDATE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //TODO
    }
}
