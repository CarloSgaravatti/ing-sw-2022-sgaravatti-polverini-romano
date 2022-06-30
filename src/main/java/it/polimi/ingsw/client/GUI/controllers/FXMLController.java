package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.messages.ErrorMessageType;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * FXMLController represents a generic controller of a javafx scene or sub scene.
 */
public abstract class FXMLController {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    /**
     * Adds a PropertyChangeListener to the fxml controller
     *
     * @param listener the listener that will listen the controller
     */
    public void addListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Adds a PropertyChangeListener that will listen the specified property of the fxml controller
     *
     * @param listener the listener that will listen the controller
     * @param propertyName the name of the property
     */
    public void addListener(PropertyChangeListener listener, String propertyName) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Fire an event that have the specified name, old value and new value
     *
     * @param propertyName the name of the property
     * @param oldValue the old value of the property
     * @param newValue the new value of the property
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        listeners.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire the specified PropertyChangeEvent
     *
     * @param evt the event that will be fired
     */
    public void firePropertyChange(PropertyChangeEvent evt) {
        listeners.firePropertyChange(evt);
    }

    /**
     * Displays an alert modal window over the scene that will contain the specified message
     *
     * @param message the message of the modal window
     */
    public void displayAlert(String message) {
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Error");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.getDialogPane().lookupButton(okButton).setDisable(false);
        dialog.showAndWait();
    }

    /**
     * Handles an error that have the specified type and the specified description
     *
     * @param error the type of error
     * @param errorInfo the description of the error
     */
    public abstract void onError(ErrorMessageType error, String errorInfo);

    /**
     * Displays a dialog that informs the user that the connection with the server has been closed unexpectedly and so
     * the application will close when the dialog is closed
     */
    public void displayShutdownAlert() {
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Error");
        dialog.setContentText("An error as occurred in the server, the application will now close");
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.getDialogPane().lookupButton(okButton).setDisable(false);
        dialog.setOnCloseRequest(dialogEvent -> System.exit(0));
        dialog.showAndWait();
    }
}
