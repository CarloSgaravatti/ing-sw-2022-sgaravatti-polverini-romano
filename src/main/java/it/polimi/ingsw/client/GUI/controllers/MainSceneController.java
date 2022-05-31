package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.messages.ErrorMessageType;

public class MainSceneController extends FXMLController {
    private ModelView modelView;

    public void initializeBoard(ModelView modelView) {
        this.modelView = modelView;

    }
    @Override
    public void onError(ErrorMessageType error) {

    }
}
