package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.messages.MessageFromClient;

import javax.swing.event.EventListenerList;

public abstract class View {
    private final EventListenerList controllerListeners = new EventListenerList();

    public View(GameController gameController) {
        addListener(gameController);
        addListener(gameController.getInitController());
    }

    protected void addListener(InitController initController) {
        controllerListeners.add(InitController.class, initController);
    }

    protected void addListener(GameController gameController) {
        controllerListeners.add(GameController.class, gameController);
    }

    protected void fireSetupMessageEvent(MessageFromClient message) {
        for (InitController initController: controllerListeners.getListeners(InitController.class)) {
            initController.eventPerformed(message);
        }
    }

    protected void fireActionMessageEvent(MessageFromClient message) {
        for (GameController gameController: controllerListeners.getListeners(GameController.class)) {
            gameController.eventPerformed(message);
        }
    }
}
