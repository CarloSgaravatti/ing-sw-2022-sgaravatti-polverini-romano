package it.polimi.ingsw.client;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

public interface UserInterface extends PropertyChangeListener{

    String getNickname(); //Don't know if it is useful

    void askNickname();

    void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo);

    void displayStringMessage(String message);

    void askTowerChoice(TowerType[] freeTowers);

    void askWizardChoice(WizardType[] freeWizards);

    void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers);

    void askAction(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions);

    void addListener(PropertyChangeListener listener, String propertyName);

    void onGameInitialization(ModelView modelView);

    void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions); //maybe this is not correct and have to be done with listeners
}
