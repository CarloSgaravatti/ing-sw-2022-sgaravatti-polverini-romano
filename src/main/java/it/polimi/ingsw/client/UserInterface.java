package it.polimi.ingsw.client;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;

import java.beans.PropertyChangeListener;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * UserInterface is a common interface for both CLI and GUI that defines all methods that these have in common
 */
public interface UserInterface extends PropertyChangeListener{

    /**
     * Returns the nickname of the client
     *
     * @return the nickname of the client
     */
    String getNickname(); //Don't know if it is useful

    /**
     * Ask a nickname to the client
     */
    void askNickname();

    /**
     * Display all global lobby information that the client will use to choose if he wants to play an existing game or if
     * he wants to create a new game.
     *
     * @param numGames the number of games on the server that are not already started
     * @param gamesInfo all the games not started information (number of players, rules, players nicknames)
     */
    void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo);

    /**
     * Display a message in the user interface that notifies some events to the user.
     *
     * @param message the message to be notified
     */
    void displayStringMessage(String message);

    /**
     * Ask the client to choose a tower in the setup phase of a game
     *
     * @param freeTowers the towers that the client can choose
     */
    void askTowerChoice(TowerType[] freeTowers);

    /**
     * Ask the client to choose a wizard in the setup phase of a game
     *
     * @param freeWizards the wizards that the client can choose
     */
    void askWizardChoice(WizardType[] freeWizards);

    /**
     * Display all the info of the game lobby on which the client enter
     *
     * @param numPlayers the number of players of the game
     * @param rules the type of rules
     * @param waitingPlayers the players that are already connected to the game
     */
    void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers);

    /**
     * Ask an action to the client during his turn.
     *
     * @param actions all the actions descriptions that the player can do
     * @param actionCommands all the actions command to call the actions that the player can do
     * @param currentPossibleActions all the actions commands of the actions that the client can do without doing
     *                               anything before
     */
    void askAction(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions);

    /**
     * Adds the specified PropertyChangeListener to the user interface that will listen to the specified property name
     *
     * @param listener the PropertyChangeListener to be added
     * @param propertyName the property name that the listener will listen to
     */
    void addListener(PropertyChangeListener listener, String propertyName);

    /**
     * Performs all operations that need to be done when the game start
     *
     * @param modelView the ModelView of the game
     */
    void onGameInitialization(ModelView modelView);

    /**
     * Notify the client about what he can do during the turn.
     *
     * @param actions all the actions descriptions that the player can do
     * @param actionCommands all the actions command to call the actions that the player can do
     * @param currentPossibleActions all the actions commands of the actions that the client can do without doing
     *                               anything before
     */
    void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions); //maybe this is not correct and have to be done with listeners

    /**
     * Notifies the client that an error from the server has occurred
     *
     * @param error the error type
     * @param info the error description
     */
    void onError(ErrorMessageType error, String info);

    /**
     * Notifies the client to choose a new game or one previous game
     *
     * @param numPlayers the number of players in the saved game
     * @param rules the rules of the saved games
     * @param participants array of participants' name
     */
    void onResumeGame(int numPlayers, boolean rules, String[] participants);
}
