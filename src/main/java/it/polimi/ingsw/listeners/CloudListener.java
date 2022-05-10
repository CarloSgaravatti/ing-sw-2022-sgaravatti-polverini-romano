package it.polimi.ingsw.listeners;

import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.messages.ServerMessageType;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.server.RemoteView;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;


public class CloudListener implements PropertyChangeListener {
    private final RemoteView remoteView;

    public CloudListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Pair<?, ?> cloud = (Pair<?, ?>) evt.getNewValue(); //Pair<Integer, Student[]>
        RealmType[] students = Arrays.stream((Student[]) cloud.getSecond())
                .map(Student::getStudentType).toList().toArray(new RealmType[0]);
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CloudIndex", cloud.getFirst());
        messagePayload.setAttribute("NamePlayer", evt.getSource());
        messagePayload.setAttribute("Students", students);
        remoteView.sendMessage(messagePayload, "PickFromCloud", ServerMessageType.GAME_UPDATE);
    }
}
