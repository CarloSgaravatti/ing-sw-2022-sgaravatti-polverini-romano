package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.RoundPhase;
import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.ClientMessageHeader;
import it.polimi.ingsw.messages.ClientMessageType;
import it.polimi.ingsw.messages.MessageFromClient;
import it.polimi.ingsw.messages.MessagePayload;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Used when the client is the active player
public class TurnHandler implements PropertyChangeListener {
    private final ConnectionToServer connection;
    private final UserInterface userInterface;
    private final List<String> turnActions = List.of("MoveStudents", "MoveMotherNature", "PickFromCloud", "PlayCharacterCard");
    private final List<TurnPhase> currentTurnActions = new ArrayList<>();
    private final ExecutorService clientTurnHandler = Executors.newSingleThreadExecutor();
    private boolean ackReceived = false;
    private boolean errorReceived = false;

    private final Object waitAckOrErrorLock = new Object();

    public TurnHandler(boolean expertGame, ConnectionToServer connection, UserInterface userInterface) {
        //if (!expertGame) turnActions.remove("Play a character card"); TODO: delete turn actions?
        this.connection = connection;
        this.userInterface = userInterface;
    }

    public void handlePlayerTurn(RoundPhase currentPhase) {
        /*try {
            if (currentPhase == RoundPhase.PLANNING) {
                //planningPhase();
            } else {
                //actionPhase();
            }
            sendEndTurnMessage();
        } catch (InterruptedException e) {
            //TODO
        }*/
    }

   /* private void planningPhase() throws InterruptedException {
        currentTurnActions.clear();
        currentTurnActions.add("PlayAssistant");
        setAckReceived(false);
        do {
            setErrorReceived(false);
            //userInterface.askAction(currentTurnActions);
            synchronized (waitAckOrErrorLock) {
                while (!isAckReceived() && !isErrorReceived()) waitAckOrErrorLock.wait();
            }
            //TODO (or is ok like that)?
        } while (!isAckReceived());
    }

    private void actionPhase() throws InterruptedException {
        currentTurnActions.clear();
        currentTurnActions.addAll(turnActions); //TODO: control characters action (is optional)
        while(!currentTurnActions.isEmpty()) {
            setAckReceived(false);
            setErrorReceived(false);
            //userInterface.askAction(currentTurnActions);
            synchronized (waitAckOrErrorLock) {
                while (!isAckReceived() && !isErrorReceived()) waitAckOrErrorLock.wait();
            }
        }
    }*/

    private void sendEndTurnMessage() {
        ClientMessageHeader header = new ClientMessageHeader("EndTurn", userInterface.getNickname(), ClientMessageType.ACTION);
        connection.asyncWriteToServer(new MessageFromClient(header, new MessagePayload()));
    }

    public void onAckReceived(String actionName) {
        currentTurnActions.remove(actionName);
        setAckReceived(true);
        synchronized (waitAckOrErrorLock) {
            waitAckOrErrorLock.notify();
        }
    }

    public void onErrorReceived() {
        setErrorReceived(true);
        synchronized (waitAckOrErrorLock) {
            waitAckOrErrorLock.notify();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "ActionAck" -> onAckReceived((String) evt.getNewValue());
            case "ActionAckV2" -> onAckReceived(List.of((TurnPhase[]) evt.getNewValue()));
            case "Error" -> onErrorReceived();
            case "ClientTurn" -> clientTurnHandler.submit(() -> handlePlayerTurn((RoundPhase) evt.getNewValue()));
            case "ClientTurnV2" -> clientTurnHandler.submit(() -> handlePlayerTurn(List.of((TurnPhase[]) evt.getNewValue())));
        }
    }

    private synchronized boolean isAckReceived() {
        return ackReceived;
    }

    private synchronized void setAckReceived(boolean ackReceived) {
        this.ackReceived = ackReceived;
    }

    private synchronized boolean isErrorReceived() {
        return errorReceived;
    }

    private synchronized void setErrorReceived(boolean errorReceived) {
        this.errorReceived = errorReceived;
    }

    //---------------------------------------------------------------------------------------------------
    //Possible alternative to handlePlayerTurn (message contains also possible actions (for both planning and action phase)

    public void handlePlayerTurn(final List<TurnPhase> possibleActions) {
        synchronized (currentTurnActions) {
            currentTurnActions.clear();
            currentTurnActions.addAll(possibleActions); //TODO: control characters action (is optional)
            while (!currentTurnActions.isEmpty()) {
                setAckReceived(false);
                setErrorReceived(false);
                userInterface.askAction(currentTurnActions.stream().map(TurnPhase::getActionDescription).toList(),
                        currentTurnActions.stream().map(TurnPhase::getActionCommand).toList());
                try {
                    while (!isAckReceived() && !isErrorReceived()) currentTurnActions.wait();
                } catch (InterruptedException e) {
                    //TODO
                }
            }
        }
    }

    public void onAckReceived(List<TurnPhase> newPossibleActions) {
        setAckReceived(true);
        synchronized (currentTurnActions) {
            currentTurnActions.clear();
            currentTurnActions.addAll(newPossibleActions);
            currentTurnActions.notify();
        }
    }

    public void onErrorReceived2() {
        setErrorReceived(true);
        //TODO: notify user
        synchronized (currentTurnActions) {
            currentTurnActions.notify();
        }
    }

}
