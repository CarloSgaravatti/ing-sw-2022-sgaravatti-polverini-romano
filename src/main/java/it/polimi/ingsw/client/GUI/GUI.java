package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.model.enumerations.WizardType;
import it.polimi.ingsw.utils.Triplet;
import javafx.application.Application;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

public class GUI extends Application implements UserInterface {
    private String nickname;
    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public String getNickname() {
        return null;
    }

    @Override
    public void askNickname() {

    }

    @Override
    public void displayGlobalLobby(int numGames, Map<Integer, Triplet<Integer, Boolean, String[]>> gamesInfo) {

    }

    @Override
    public void displayStringMessage(String message) {

    }

    @Override
    public void askTowerChoice(TowerType[] freeTowers) {

    }

    @Override
    public void askWizardChoice(WizardType[] freeWizards) {

    }

    @Override
    public void displayLobbyInfo(int numPlayers, boolean rules, String[] waitingPlayers) {

    }

    @Override
    public void askAction(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {

    }

    @Override
    public void addListener(PropertyChangeListener listener, String propertyName) {

    }

    @Override
    public void onGameInitialization(ModelView modelView) {

    }

    @Override
    public void printTurnMenu(List<String> actions, List<String> actionCommands, List<String> currentPossibleActions) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
