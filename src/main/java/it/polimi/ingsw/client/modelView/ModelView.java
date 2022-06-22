package it.polimi.ingsw.client.modelView;

import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;

import java.util.*;

/**
 * ModelView is a light version of the model that is present on the client. The class contains all information that is
 * useful for the client in order to let the user know every information of the game without requesting it to the server.
 */
public class ModelView {
    private final Map<String, PlayerView> players = new HashMap<>();
    private FieldView field;
    private RoundPhase currentPhase;
    private String currentActivePlayer;
    private final boolean isExpert;
    private Map<Integer, Integer> clientPlayerAssistants = new HashMap<>();

    /**
     * Construct a new ModelView for a game with the specified rules
     *
     * @param isExpert the type of rules of the game (true if expert, otherwise false)
     */
    public ModelView(boolean isExpert) {
        this.isExpert = isExpert;
    }

    /**
     * Returns a map containing all players view, each associated with the corresponding nickname of the player
     *
     * @return a map containing all players view, each associated with the corresponding nickname of the player
     */
    public Map<String, PlayerView> getPlayers() {
        return players;
    }

    /**
     * Returns the field view of this model view
     *
     * @return the field view of this model view
     */
    public FieldView getField() {
        return field;
    }

    /**
     * Set the value of the FieldView that will be associated to this ModelView
     *
     * @param field the FieldView that will be associated to this ModelView
     */
    public void setField(FieldView field) {
        this.field = field;
    }

    /**
     * Return the value of the current RoundPhase
     *
     * @return the value of the current RoundPhase
     */
    public RoundPhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Set the value of the current RoundPhase
     *
     * @param currentPhase  the value of the current RoundPhase
     */
    public void setCurrentPhase(RoundPhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     * Return the nickname of the current active player
     *
     * @return the nickname of the current active player
     */
    public String getCurrentActivePlayer() {
        return currentActivePlayer;
    }

    /**
     * Sets the nickname of the current active player
     *
     * @param currentActivePlayer  the nickname of the current active player
     */
    public void setCurrentActivePlayer(String currentActivePlayer) {
        this.currentActivePlayer = currentActivePlayer;
    }

    /**
     * Returns true if the game is expert, otherwise false
     *
     * @return true if the game is expert, otherwise false
     */
    public boolean isExpert() {
        return isExpert;
    }

    /**
     * Insert the specified students (expressed as a RealmType[]) into the specified container (expressed as Integer[])
     *
     * @param toIns the container on which the students will be inserted
     * @param students the students that will be inserted
     */
    public static void insertStudents(Integer[] toIns, RealmType[] students) {
        for (RealmType r: students) {
            toIns[r.ordinal()]++;
        }
    }

    /**
     * Remove the specified students (expressed as a RealmType[]) into the specified container (expressed as Integer[])
     *
     * @param toRemove the container on which the students will be removed
     * @param students the students that will be removed
     */
    public static void removeStudents(Integer[] toRemove, RealmType[] students) {
        for (RealmType r: students) {
            toRemove[r.ordinal()]--;
        }
    }

    /**
     * Returns the assistants that are associated to the client
     *
     * @return the assistants that are associated to the client
     */
    public Map<Integer, Integer> getClientPlayerAssistants() {
        return clientPlayerAssistants;
    }

    /**
     * Set the assistants that are associated to the client
     *
     * @param clientPlayerAssistants  the assistants that are associated to the client
     */
    public void setClientPlayerAssistants(Map<Integer, Integer> clientPlayerAssistants) {
        this.clientPlayerAssistants = clientPlayerAssistants;
    }

    /**
     * Removes an assistant from the set of assistants of the client
     *
     * @param assistant the assistant that will be removed
     */
    public void removeAssistant(int assistant) {
        this.clientPlayerAssistants.remove(assistant);
    }
}
