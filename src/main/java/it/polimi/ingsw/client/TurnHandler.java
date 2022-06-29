package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.TurnPhase;
import it.polimi.ingsw.messages.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Used when the client is the active player

/**
 * Class TurnHandler is used to help the client when he is the current turn active player. The class suggest to the player
 * which actions can be done in the turn and which actions must be done at the moment. ALso, TurnHandler will automatically
 * end the turn of the client (by sending an EndTurn message to the sever) if the client have done all possible actions
 * that he can do. The class implements the PropertyChangeListener interface because the TurnHandler will listen to
 * some message handlers in order to know when it is the client turn and if the action made by the client have been
 * acknowledged or not.
 *
 * The class uses a SingleThreadExecutor to perform the turn handling: the handlePlayerTurn method will be submitted to
 * this executor when a new turn of the client starts.
 */
public class TurnHandler implements PropertyChangeListener {
    private final ConnectionToServer connection;
    private final UserInterface userInterface;
    private final List<TurnPhase> currentTurnActions = new ArrayList<>();
    private final ExecutorService clientTurnHandler = Executors.newSingleThreadExecutor();
    private boolean ackReceived = false;
    private boolean errorReceived = false;
    private boolean turnAlreadyEnded = false;
    private boolean isInputErrorReceived = false;

    public TurnHandler(ConnectionToServer connection, UserInterface userInterface) {
        this.connection = connection;
        this.userInterface = userInterface;
    }

    /**
     * Receives an event fired by a message handler.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "ActionAck" -> onAckReceived(List.of((TurnPhase[]) evt.getNewValue()), (String) evt.getOldValue());
            case "Error" -> onErrorReceived();
            case "InputError" -> onInputError();
            case "ClientTurn" -> clientTurnHandler.submit(() -> handlePlayerTurn(List.of((TurnPhase[]) evt.getNewValue())));
        }
    }

    /**
     * Checks if the last action have already been acknowledged or not.
     *
     * @return true if the action have been acknowledged, otherwise false
     */
    private synchronized boolean isAckReceived() {
        return ackReceived;
    }

    /**
     * Set the value of the ack received property, that notifies the turn handler if an acknowledgement has arrived or not.
     *
     * @param ackReceived true if the action have been acknowledged, otherwise false
     */
    private synchronized void setAckReceived(boolean ackReceived) {
        this.ackReceived = ackReceived;
    }

    /**
     * Checks if the last action have been provoked an error message by the server or not.
     *
     * @return true if an error was received, otherwise false
     */
    private synchronized boolean isErrorReceived() {
        return errorReceived;
    }

    /**
     * Set the value of the error received property, that notifies the turn handler if an error has arrived or not.
     *
     * @param errorReceived true if an error was received, otherwise false
     */
    private synchronized void setErrorReceived(boolean errorReceived) {
        this.errorReceived = errorReceived;
    }

    /**
     * Handles the client turn that can do all the specified possible actions. This method is done in parallel to the
     * actual turn that the client sees from the UserInterface. The method will run until an EndTurn acknowledgement
     * message arrives from the server. This is done with a producer - consumer pattern, where this method is the consumer
     * part and the other on* methods are the producers part. Possible actions are updated every time a producer receives
     * an acknowledgement.
     *
     * @param possibleActions the possible actions of the turn
     */
    public void handlePlayerTurn(final List<TurnPhase> possibleActions) {
        setTurnAlreadyEnded(false);
        setInputErrorReceived(false);
        setErrorReceived(false);
        setAckReceived(false);
        synchronized (currentTurnActions) {
            currentTurnActions.clear();
            currentTurnActions.addAll(possibleActions);
            while (!currentTurnActions.isEmpty()) {
                List<String> currentTurnPossibleActions = new ArrayList<>();
                currentTurnPossibleActions.add(currentTurnActions.get(0).getActionCommand());
                if (currentTurnActions.contains(TurnPhase.PLAY_CHARACTER_CARD) && currentTurnActions.size() != 1) {
                    currentTurnPossibleActions.add(TurnPhase.PLAY_CHARACTER_CARD.getActionCommand());
                }
                if (!isErrorReceived() && !isInputErrorReceived()) {
                    userInterface.printTurnMenu(currentTurnActions.stream().map(TurnPhase::getActionDescription).toList(),
                            currentTurnActions.stream().map(TurnPhase::getActionCommand).toList(), currentTurnPossibleActions);
                }
                setInputErrorReceived(false);
                setErrorReceived(false);
                setAckReceived(false);
                userInterface.askAction(currentTurnActions.stream().map(TurnPhase::getActionDescription).toList(),
                        currentTurnActions.stream().map(TurnPhase::getActionCommand).toList(), currentTurnPossibleActions);
                try {
                    while (!isAckReceived() && !isErrorReceived() && !isInputErrorReceived()) currentTurnActions.wait();
                } catch (InterruptedException ignored) {}
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
    }

    /**
     * Handle the event of receiving an acknowledgement from the server.
     *
     * @param newPossibleActions the new actions that the clients can to do before ending the turn
     * @param actionToAck the action that was acknowledged
     */
    public void onAckReceived(List<TurnPhase> newPossibleActions, String actionToAck) {
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

    /**
     * Handle the event of receiving an error from the server.
     */
    public void onErrorReceived() {
        setErrorReceived(true);
        synchronized (currentTurnActions) {
            currentTurnActions.notify();
        }
    }

    /**
     * Handle the event of receiving an error from the ActionInputParser.
     */
    public void onInputError() {
        setInputErrorReceived(true);
        synchronized (currentTurnActions) {
            currentTurnActions.notify();
        }
    }

    /**
     * Checks if the client have already sent an EndTurn message that was acknowledged by the server
     *
     * @return true if the client has ended his turn, otherwise false
     */
    public synchronized boolean isTurnAlreadyEnded() {
        return turnAlreadyEnded;
    }

    public synchronized void setTurnAlreadyEnded(boolean turnAlreadyEnded) {
        this.turnAlreadyEnded = turnAlreadyEnded;
    }

    /**
     * Checks if the last action of the client was blocked by the ActionInputParser because it was not correctly written.
     *
     * @return true if there was an error recognized by the ActionInputParser, otherwise false
     */
    public synchronized boolean isInputErrorReceived() {
        return isInputErrorReceived;
    }

    public synchronized void setInputErrorReceived(boolean inputErrorReceived) {
        isInputErrorReceived = inputErrorReceived;
    }
}
