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

public class ActionMessageConstructor implements PropertyChangeListener {
    private final ConnectionToServer connection;

    public ActionMessageConstructor(ConnectionToServer connection) {
        this.connection = connection;
    }

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

    private MessagePayload createPlayAssistantMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Assistant", Integer.parseInt(userAction));
        return payload;
    }

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

    private MessagePayload createMoveMotherNatureMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("MotherNature", Integer.parseInt(userAction));
        return payload;
    }

    private MessagePayload createPickFromCloudMessage(String userAction) {
        MessagePayload payload = new MessagePayload();
        payload.setAttribute("Cloud", Integer.parseInt(userAction));
        return payload;
    }

    private MessagePayload createPlayCharacterMessage(String action) {
        MessagePayload payload = new MessagePayload();
        String[] args = action.split(" ");
        int characterId = Integer.parseInt(args[0]);
        Optional<String> arguments = Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).reduce((s1, s2) -> s1 + " " + s2);
        payload.setAttribute("CharacterId", characterId);
        payload.setAttribute("Arguments", arguments.orElse(null));
        return payload;
    }
}
