package it.polimi.ingsw.client;

import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Pair;

import java.util.List;
import java.util.Map;

public interface UserInterface {

    String getNickname(); //Don't know if it is useful

    String askNickname();

    void displayGameLobby(int numGames, Map<Integer, Pair<Integer, List<String>>> gamesInfo);

    Pair<String, Integer> askGameToPlay();

    void displayStringMessage(String message);

    TowerType askTowerChoice(List<TowerType> freeTowers);

    WizardType askWizardChoice(List<WizardType> freeWizards);

    void displayLobbyInfo();

    void askAction();
}
