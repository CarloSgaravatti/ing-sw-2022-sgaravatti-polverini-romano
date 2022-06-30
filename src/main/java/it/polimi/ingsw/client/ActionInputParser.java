package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.utils.Colors;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Optional;

/**
 * ActionInputParser receives an action request from the UserInterface during his turn. The request is parsed in order
 * to check if all parameters are present: if so the action is passed to the ActionMessageConstructor, otherwise the
 * UserInterface and the TurnHandler are notified about the error.
 */
public class ActionInputParser implements PropertyChangeListener {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private final UserInterface userInterface;
    private final ModelView modelView;
    private static final String ASSISTANT_NOT_FOUND = "There isn't such an assistant in your deck";
    private static final String NOT_A_NUMBER = "You have to insert a number";
    private static final String NOT_A_STUDENT = "Student abbreviation not valid";
    private static final String ISLAND_NOT_FOUND = "There isn't such an island";
    private static final String UNRECOGNISED_COMMAND = "Not a valid command";
    private static final String WRONG_STUDENT_NUMBER_2 = "You have to select exactly 3 students";
    private static final String WRONG_STUDENT_NUMBER_3 = "You have to select exactly 4 students";
    private static final String WRONG_MOTHER_NATURE_MOVEMENT = "You can only move mother nature between 1 and ";
    private static final String CLOUD_NOT_FOUND = "There isn't such a cloud";
    private static final String CHARACTER_NOT_FOUND = "There isn't such a character";
    private final int studentsToMove;

    /**
     * Construct a new instance of ActionInputParser. The new object will be bound with the specified PropertyChangeListener
     * (the MessageConstructor that will construct the message that will be sent to the server), the specified TurnHandler,
     * the specified UserInterface that will be notified about errors and the specified ModelView that will be used to
     * recognise errors.
     *
     * @param messageConstructor the listener on which correct actions will be passed
     * @param turnHandler the turn handler of the client
     * @param userInterface the user interface of the client
     * @param modelView the model view of the client
     */
    public ActionInputParser(PropertyChangeListener messageConstructor, TurnHandler turnHandler, UserInterface userInterface, ModelView modelView) {
        this.listeners.addPropertyChangeListener(messageConstructor);
        this.listeners.addPropertyChangeListener("InputError", turnHandler);
        this.userInterface = userInterface;
        this.modelView = modelView;
        studentsToMove = (modelView.getPlayers().size() == 2) ? 3 : 4;
    }

    /**
     * Responds to an action event that is fired by the UserInterface. The action have an action name, which is contained
     * in the propertyName of the specified event and some arguments that are present in the newValue of the event.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String move = (String) evt.getNewValue();
        System.out.println("You ordered: " + evt.getPropertyName() + " " + move);
        Optional<String> error = switch (evt.getPropertyName()) {
            case "MoveStudents" -> checkMoveStudents(move);
            case "MoveMotherNature" -> checkMoveMotherNature(move);
            case "PickFromCloud" -> checkPickFromCloud(move);
            case "PlayCharacter" -> checkPlayCharacter(move);
            case "PlayAssistant" -> checkPlayAssistant(move);
            default -> Optional.of(UNRECOGNISED_COMMAND);
        };
        error.ifPresentOrElse(err -> {
            userInterface.onError(null, err);
            listeners.firePropertyChange("InputError", null, evt.getPropertyName());
        }, () -> listeners.firePropertyChange(evt));
    }

    /**
     * Get all arguments that an action contains, these are separated by a space in the action that the UserInterface sends.
     *
     * @param move the action arguments
     * @return all action arguments separated in a specific String and putted together in an array
     */
    private String[] getMoveArgs(String move) {
        return Arrays.stream(move.split("\n"))
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .filter(arg -> (arg.length() != 0 && !arg.equals(" ")))
                .toList().toArray(new String[0]);
    }

    /**
     * Checks if the PlayAssistant request can be sent to the server. The request have to contain a number to be sent.
     *
     * @param move the action arguments
     * @return an empty Optional if the action was correct, otherwise an Optional containing the error
     */
    private Optional<String> checkPlayAssistant(String move) {
        int assistantId;
        try {
            assistantId = Integer.parseInt(move);
        } catch (NumberFormatException e) {
            return Optional.of(NOT_A_NUMBER);
        }
        if (!modelView.getClientPlayerAssistants().containsKey(assistantId)) return Optional.of(ASSISTANT_NOT_FOUND);
        return Optional.empty();
    }

    /**
     * Checks if the MoveStudents request can be sent to the server. All students contained in the request must be
     * of an existing RealmType and all islands must have a valid id.
     *
     * @param move the action arguments
     * @return an empty Optional if the action was correct, otherwise an Optional containing the error
     */
    private Optional<String> checkMoveStudents(String move) {
        String[] moveArgs = getMoveArgs(move);
        //String[] moveArgs = move.split(" ");
        int i = 0;
        int numStudentsMoved = 0;
        while(i < moveArgs.length) {
            try {
                RealmType student = RealmType.getRealmByAbbreviation(moveArgs[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                return Optional.of(NOT_A_STUDENT);
            }
            i++;
            if (moveArgs[i].equals("ToIsland")) {
                int islandId;
                try {
                    islandId = Integer.parseInt(moveArgs[i + 1]);
                } catch (NumberFormatException e) {
                    return Optional.of(NOT_A_NUMBER);
                }
                if (modelView.getField().getIslandSize() < islandId) return Optional.of(ISLAND_NOT_FOUND);
                i++;
            }
            else if (!moveArgs[i].equals("ToDiningRoom")) return Optional.of(UNRECOGNISED_COMMAND);
            i++;
            numStudentsMoved++;
        }
        if (numStudentsMoved != studentsToMove) {
            if (modelView.getPlayers().size() == 2) return Optional.of(WRONG_STUDENT_NUMBER_2);
            return Optional.of(WRONG_STUDENT_NUMBER_3);
        }
        return Optional.empty();
    }

    /**
     * Checks if the MoveMotherNature request can be sent to the server. The movement have to be a number and have to be
     * a feasible movement that the player can perform.
     *
     * @param move the action arguments
     * @return an empty Optional if the action was correct, otherwise an Optional containing the error
     */
    private Optional<String> checkMoveMotherNature(String move) {
        int motherNatureMovement;
        try {
            motherNatureMovement = Integer.parseInt(move);
        } catch (NumberFormatException e) {
            return Optional.of(NOT_A_NUMBER);
        }
        int maxMovement = modelView.getPlayers().get(userInterface.getNickname()).getLastPlayedAssistant().getSecond();
        if (motherNatureMovement < 1 || motherNatureMovement > maxMovement) {
            return Optional.of(WRONG_MOTHER_NATURE_MOVEMENT + maxMovement + "positions");
        }
        return Optional.empty();
    }

    /**
     * Checks if the PickFromCloud request can be sent to the server. The request must contain a number that represent
     * an existing cloud id.
     *
     * @param move the action arguments
     * @return an empty Optional if the action was correct, otherwise an Optional containing the error
     */
    private Optional<String> checkPickFromCloud(String move) {
        int cloudId;
        try {
            cloudId = Integer.parseInt(move);
        } catch (NumberFormatException e) {
            return Optional.of(NOT_A_NUMBER);
        }
        if (!modelView.getField().getCloudStudents().containsKey(cloudId)) return Optional.of(CLOUD_NOT_FOUND);
        return Optional.empty();
    }

    /**
     * Checks if the PlayCharacter request can be sent to the server. The request must contain a number that represent
     * an existing character.
     *
     * @param move the action arguments
     * @return an empty Optional if the action was correct, otherwise an Optional containing the error
     */
    private Optional<String> checkPlayCharacter(String move) {
        String[] moveArgs = getMoveArgs(move);
        int characterId;
        try {
            characterId = Integer.parseInt(moveArgs[0]);
        } catch (NumberFormatException e) {
            return Optional.of(NOT_A_NUMBER);
        }
        if (!modelView.getField().getExpertField().getCharacters().containsKey(characterId)) return Optional.of(CHARACTER_NOT_FOUND);
        return Optional.empty();
    }
}
