package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.utils.Colors;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

public class ActionInputParser implements PropertyChangeListener {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private final UserInterface userInterface;
    private final ModelView modelView;
    private static final String ASSISTANT_NOT_FOUND = "There isn't such an assistant in your deck";
    private static final String NOT_A_NUMBER = "You have to insert a number";
    private static final String NOT_A_STUDENT = "Student abbreviation not valid";
    private static final String ISLAND_NOT_FOUND = "There isn't such an island";
    private static final String UNRECOGNISED_COMMAND = "Not a valid command";
    private static final String WRONG_STUDENT_NUMBER = "You have to select exactly 3 students";
    private static final String WRONG_MOTHER_NATURE_MOVEMENT = "You can only move mother nature between 1 and ";
    private static final String CLOUD_NOT_FOUND = "There isn't such a cloud";
    private static final String CHARACTER_NOT_FOUND = "There isn't such a character";

    public ActionInputParser(PropertyChangeListener messageConstructor, TurnHandler turnHandler, UserInterface userInterface, ModelView modelView) {
        this.listeners.addPropertyChangeListener(messageConstructor);
        this.listeners.addPropertyChangeListener("InputError", turnHandler);
        this.userInterface = userInterface;
        this.modelView = modelView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //check input, if ok notify the message constructor
        String move = (String) evt.getNewValue();
        System.out.println("You ordered: " + evt.getPropertyName() + " " + move);
        try {
            String error = switch (evt.getPropertyName()) {
                case "MoveStudents" -> checkMoveStudents(move);
                case "MoveMotherNature" -> checkMoveMotherNature(move);
                case "PickFromCloud" -> checkPickFromCloud(move);
                case "PlayCharacter" -> checkPlayCharacter(move);
                case "PlayAssistant" -> checkPlayAssistant(move);
                default -> UNRECOGNISED_COMMAND;
            };
            if (error == null) listeners.firePropertyChange(evt);
            else {
                userInterface.displayStringMessage(Colors.RED + error + Colors.RESET);
                //userInterface.insertAction(); //TODO: do better
                listeners.firePropertyChange("InputError", null, evt.getPropertyName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getMoveArgs(String move) {
        return Arrays.stream(move.split("\n"))
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .filter(arg -> (arg.length() != 0 && !arg.equals(" ")))
                .toList().toArray(new String[0]);
    }

    private String checkPlayAssistant(String move) {
        int assistantId;
        try {
            assistantId = Integer.parseInt(move);
        } catch (NumberFormatException e) {
            return NOT_A_NUMBER;
        }
        if (!modelView.getClientPlayerAssistants().containsKey(assistantId)) return ASSISTANT_NOT_FOUND;
        return null;
    }

    private String checkMoveStudents(String move) {
        String[] moveArgs = getMoveArgs(move);
        //String[] moveArgs = move.split(" ");
        int i = 0;
        int numStudentsMoved = 0;
        while(i < moveArgs.length) {
            try {
                RealmType student = RealmType.getRealmByAbbreviation(moveArgs[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                return NOT_A_STUDENT;
            }
            i++;
            if (moveArgs[i].equals("ToIsland")) {
                int islandId;
                try {
                    islandId = Integer.parseInt(moveArgs[i + 1]);
                } catch (NumberFormatException e) {
                    return NOT_A_NUMBER;
                }
                if (modelView.getField().getIslandSize() < islandId) return ISLAND_NOT_FOUND;
                i++;
            }
            else if (!moveArgs[i].equals("ToDiningRoom")) return UNRECOGNISED_COMMAND;
            i++;
            numStudentsMoved++;
        }
        if (numStudentsMoved != 3) return WRONG_STUDENT_NUMBER;
        return null;
    }

    private String checkMoveMotherNature(String move) {
        int motherNatureMovement;
        try {
            motherNatureMovement = Integer.parseInt(move);
        } catch (NumberFormatException e) {
            return NOT_A_NUMBER;
        }
        int maxMovement = modelView.getPlayers().get(userInterface.getNickname()).getLastPlayedAssistant().getSecond();
        if (motherNatureMovement < 1 || motherNatureMovement > maxMovement) {
            return WRONG_MOTHER_NATURE_MOVEMENT + maxMovement;
        }
        return null;
    }

    private String checkPickFromCloud(String move) {
        int cloudId;
        try {
            cloudId = Integer.parseInt(move);
        } catch (NumberFormatException e) {
            return NOT_A_NUMBER;
        }
        if (!modelView.getField().getCloudStudents().containsKey(cloudId)) return CLOUD_NOT_FOUND;
        return null;
    }

    private String checkPlayCharacter(String move) {
        //String[] moveArgs = move.split(" ");
        String[] moveArgs = getMoveArgs(move);
        int characterId;
        try {
            characterId = Integer.parseInt(moveArgs[0]);
        } catch (NumberFormatException e) {
            return NOT_A_NUMBER;
        }
        if (!modelView.getField().getExpertField().getCharacters().containsKey(characterId)) return CHARACTER_NOT_FOUND;
        return null;
    }
}
