package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.ClientMessageType;
import it.polimi.ingsw.messages.MessagePayload;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ActionMessageConstructor will receive action from the ActionInputParser (by observing that class). From these actions
 * a new message will be constructed in base of the action name. The message will be passed to the ConnectionToServer of
 * the client.
 */
public class ActionMessageConstructor implements PropertyChangeListener {
    private final ConnectionToServer connection;

    /**
     * Constructs a new instance of ActionMessageConstructor that is associated to the specified ConnectionToServer
     *
     * @param connection the connection from the client to the server that will send messages
     */
    public ActionMessageConstructor(ConnectionToServer connection) {
        this.connection = connection;
    }

    /**
     * Responds to an action event that is fired by the ActionInputParser. The action have an action name, which is contained
     * in the propertyName of the specified event and some arguments that are present in the newValue of the event. The
     * method creates the MessagePayload of the message that will be sent, and then it passes the payload to the
     * connection specifying the message name; the connection will create the header.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //TODO: delete try catch when everything is ok
        try {
            String userAction = (String) evt.getNewValue();
            MessagePayload payload = switch (evt.getPropertyName()) {
                case "MoveStudents" -> createMoveStudentMessage(userAction);
                case "MoveMotherNature" -> createMoveMotherNatureMessage(userAction);
                case "PickFromCloud" -> createPickFromCloudMessage(userAction);
                case "PlayCharacter" -> createPlayCharacterMessage(userAction);
                case "PlayAssistant" -> createPlayAssistantMessage(userAction);
                case "EndTurn" -> new MessagePayload();
                default -> null;
            };
            if (payload != null) {
                connection.sendMessage(payload, evt.getPropertyName(), ClientMessageType.ACTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the payload for a PlayAssistant message to the server
     *
     * @param userAction the arguments of the request, represented as a single String
     * @return the message payload of the message that will be sent
     */
    private MessagePayload createPlayAssistantMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Assistant", Integer.parseInt(userAction));
        return payload;
    }

    /**
     * Creates the payload for a MoveStudents message to the server
     *
     * @param userAction the arguments of the request, represented as a single String
     * @return the message payload of the message that will be sent
     */
    private MessagePayload createMoveStudentMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        String[] args = userAction.split(" ");
        List<RealmType> studentsToDR = new ArrayList<>();
        List<Pair<RealmType, Integer>> studentsToIsland = new ArrayList<>();
        int i = 0;
        while (i < args.length) {
            RealmType student = RealmType.getRealmByAbbreviation(args[i]);
            if (args[i + 1].equals("ToDiningRoom")) studentsToDR.add(student);
            else {
                int islandId = Integer.parseInt(args[i + 2]);
                studentsToIsland.add(new Pair<>(student, islandId));
                i++;
            }
            i += 2;
        }
        payload.setAttribute("StudentsToDR", studentsToDR);
        payload.setAttribute("StudentsToIslands", studentsToIsland);
        return payload;
    }

    /**
     * Creates the payload for a MoveMotherNature message to the server
     *
     * @param userAction the arguments of the request, represented as a single String
     * @return the message payload of the message that will be sent
     */
    private MessagePayload createMoveMotherNatureMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("MotherNature", Integer.parseInt(userAction));
        return payload;
    }

    /**
     * Creates the payload for a PickFromCloud message to the server
     *
     * @param userAction the arguments of the request, represented as a single String
     * @return the message payload of the message that will be sent
     */
    private MessagePayload createPickFromCloudMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Cloud", Integer.parseInt(userAction));
        return payload;
    }

    /**
     * Creates the payload for a PlayCharacter message to the server
     *
     * @param userAction the arguments of the request, represented as a single String
     * @return the message payload of the message that will be sent
     */
    private MessagePayload createPlayCharacterMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        String[] args = userAction.split(" ");
        int characterId = Integer.parseInt(args[0]);
        Optional<String> arguments = Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).reduce((s1, s2) -> s1 + " " + s2);
        payload.setAttribute("CharacterId", characterId);
        payload.setAttribute("Arguments", arguments.orElse(null));
        return payload;
    }
}
