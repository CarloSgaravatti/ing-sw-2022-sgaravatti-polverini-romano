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

/**
 * CloudListener is a PropertyChangeListener that listen to events that come from clouds after a player pick students
 * from them. The listener is associated to a RemoteView, that will forward messages to the client
 *
 * @see java.beans.PropertyChangeListener
 */
public class CloudListener implements PropertyChangeListener {
    private final RemoteView remoteView;

    /**
     * Constructs a CloudListener that is associated to the specified remote view
     *
     * @param remoteView the remote view to which messages will be forwarded
     */
    public CloudListener(RemoteView remoteView) {
        this.remoteView = remoteView;
    }

    /**
     * Responds to an event that was fired from a cloud after someone a picked students from it
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Pair<?, ?> cloud = (Pair<?, ?>) evt.getNewValue(); //Pair<Integer, Student[]>
        RealmType[] students = Arrays.stream((Student[]) cloud.getSecond())
                .map(Student::getStudentType).toList().toArray(new RealmType[0]);
        MessagePayload messagePayload = new MessagePayload();
        messagePayload.setAttribute("CloudId", cloud.getFirst());
        messagePayload.setAttribute("PlayerName", evt.getSource());
        messagePayload.setAttribute("Students", students);
        remoteView.sendMessage(messagePayload, "PickFromCloud", ServerMessageType.GAME_UPDATE);
    }
}
