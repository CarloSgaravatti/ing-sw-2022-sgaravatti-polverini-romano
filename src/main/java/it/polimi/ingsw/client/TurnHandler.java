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
    private final List<TurnPhase> currentTurnActions = new ArrayList<>();
    private final ExecutorService clientTurnHandler = Executors.newSingleThreadExecutor();
    private boolean ackReceived = false;
    private boolean errorReceived = false;
    private boolean turnAlreadyEnded = false;
    private boolean isInputErrorReceived = false;

    private final Object waitAckOrErrorLock = new Object();

    public TurnHandler(ConnectionToServer connection, UserInterface userInterface) {
        this.connection = connection;
        this.userInterface = userInterface;
    }

    /*@Deprecated
    public void handlePlayerTurn(RoundPhase currentPhase) {
        try {
            if (currentPhase == RoundPhase.PLANNING) {
                //planningPhase();
            } else {
                //actionPhase();
            }
            sendEndTurnMessage();
        } catch (InterruptedException e) {
            //TODO
        }
    }

   private void planningPhase() throws InterruptedException {
        currentTurnActions.clear();
        currentTurnActions.add("PlayAssistant");
        setAckReceived(false);
        do {
            setErrorReceived(false);
            //userInterface.askAction(currentTurnActions);
            synchronized (waitAckOrErrorLock) {
                while (!isAckReceived() && !isErrorReceived()) waitAckOrErrorLock.wait();
            }
        } while (!isAckReceived());
    }

    private void actionPhase() throws InterruptedException {
        currentTurnActions.clear();
        currentTurnActions.addAll(turnActions);
        while(!currentTurnActions.isEmpty()) {
            setAckReceived(false);
            setErrorReceived(false);
            //userInterface.askAction(currentTurnActions);
            synchronized (waitAckOrErrorLock) {
                while (!isAckReceived() && !isErrorReceived()) waitAckOrErrorLock.wait();
            }
        }
    }*/

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "ActionAck" -> onAckReceived(List.of((TurnPhase[]) evt.getNewValue()), (String) evt.getOldValue());
            case "Error" -> onErrorReceived();
            case "InputError" -> onInputError();
            case "ClientTurn" -> clientTurnHandler.submit(() -> handlePlayerTurn(List.of((TurnPhase[]) evt.getNewValue())));
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

    public void handlePlayerTurn(final List<TurnPhase> possibleActions) {
        System.out.println("Turn handler is starting");
        setTurnAlreadyEnded(false);
        synchronized (currentTurnActions) {
            currentTurnActions.clear();
            currentTurnActions.addAll(possibleActions);
            while (!currentTurnActions.isEmpty()) {
                setAckReceived(false);
                setErrorReceived(false);
                if (!isInputErrorReceived()) {
                    //TODO: separate printing menu and getting action, so if an input error is received
                    //  the printing menu is not printed on user interface
                }
                userInterface.askAction(currentTurnActions.stream().map(TurnPhase::getActionDescription).toList(),
                        currentTurnActions.stream().map(TurnPhase::getActionCommand).toList());
                try {
                    while (!isAckReceived() && !isErrorReceived() && !isInputErrorReceived()) currentTurnActions.wait();
                } catch (InterruptedException e) {
                    //TODO
                }
            }
            if (isTurnAlreadyEnded()) {
                userInterface.displayStringMessage("You have ended your turn");
            } else {
                userInterface.displayStringMessage("You have nothing more to do, your turn has ended");
                setAckReceived(false);
                do {
                    connection.sendMessage(new MessagePayload(), "EndTurn", ClientMessageType.ACTION);
                    try {
                        while (!isAckReceived() && !isErrorReceived()) currentTurnActions.wait();
                    } catch (InterruptedException e) {
                        //TODO
                    }
                } while (!isAckReceived());
            }
        }
        System.out.println("Turn handler has quit the turn handling");
    }

    public void onAckReceived(List<TurnPhase> newPossibleActions, String actionToAck) {
        System.out.println("AckOK");
        setAckReceived(true);
        if (actionToAck.equals("EndTurn")) {
            setTurnAlreadyEnded(true);
        }
        synchronized (currentTurnActions) {
            currentTurnActions.clear();
            currentTurnActions.addAll(newPossibleActions);
            currentTurnActions.notify();
        }
    }

    public void onErrorReceived() {
        setErrorReceived(true);
        //TODO: notify user
        synchronized (currentTurnActions) {
            currentTurnActions.notify();
        }
    }

    public void onInputError() {
        setInputErrorReceived(true);
        synchronized (currentTurnActions) {
            currentTurnActions.notify();
        }
    }

    public synchronized boolean isTurnAlreadyEnded() {
        return turnAlreadyEnded;
    }

    public synchronized void setTurnAlreadyEnded(boolean turnAlreadyEnded) {
        this.turnAlreadyEnded = turnAlreadyEnded;
    }

    public synchronized boolean isInputErrorReceived() {
        return isInputErrorReceived;
    }

    public synchronized void setInputErrorReceived(boolean inputErrorReceived) {
        isInputErrorReceived = inputErrorReceived;
    }
}
