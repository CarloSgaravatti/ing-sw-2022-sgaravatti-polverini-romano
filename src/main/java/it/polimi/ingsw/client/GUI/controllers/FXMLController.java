package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.messages.ErrorMessageType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class FXMLController {
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    public void addListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    public void addListener(PropertyChangeListener listener, String propertyName) {
        listeners.addPropertyChangeListener(propertyName, listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        listeners.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(PropertyChangeEvent evt) {
        listeners.firePropertyChange(evt);
    }

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

    public abstract void onError(ErrorMessageType error, String errorInfo);
}
