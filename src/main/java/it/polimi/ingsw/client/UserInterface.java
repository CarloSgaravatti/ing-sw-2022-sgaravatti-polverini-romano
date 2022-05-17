package it.polimi.ingsw.client;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

public interface UserInterface /*extends PropertyChangeListener*/{

    String getNickname(); //Don't know if it is useful

    void askNickname();

    void displayGlobalLobby(int numGames, Map<Integer, Pair<Integer,String[]>> gamesInfo);

    Pair<String, Integer> askGameToPlay();

    void displayStringMessage(String message);

    void askTowerChoice(TowerType[] freeTowers);

    void askWizardChoice(WizardType[] freeWizards);

    void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers);

    void askAction(List<String> actions);

    void askLobbyDecision();

    void addListener(PropertyChangeListener listener, String propertyName);

    void onGameInitialization(ModelView modelView);
}
