package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.exceptions.TowerTypeAlreadyTakenException;
import it.polimi.ingsw.exceptions.WizardTypeAlreadyTakenException;
import it.polimi.ingsw.messages.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * RemoteView class act as if the view is present on the server, by receiving messages that are sent by the client (that
 * are read by the ClientConnection) and by forwarding them to the controller. Also, the RemoteView is seen as an
 * interface that send message to the client (even if it is the ClientConnection that sends them).
 * @see View
 * @see ClientConnection
 */
public class RemoteView extends View implements PropertyChangeListener {
    private final ClientConnection connection;
    private final String playerNickname;
    private final GameLobby gameLobby;

    /**
     * Construct a RemoteView that is associated to the specified game controller and game lobby, and will use the
     * specified connection with the client to forward messages from the client and to the client
     *
     * @param connection the connection with the client
     * @param playerNickname the nickname of the client
     * @param gameLobby the game lobby of the game
     * @param controller hte game controller of the game
     */
    public RemoteView(ClientConnection connection, String playerNickname, GameLobby gameLobby, GameController controller) {
        super(controller);
        this.connection = connection;
        connection.addListener("RemoteView", this);
        this.playerNickname = playerNickname;
        this.gameLobby = gameLobby;
    }

    /**
     * Returns the nickname of the client
     *
     * @return the nickname of the client
     */
    public String getPlayerNickname() {
        return playerNickname;
    }

    /**
     * Sends a message to the client that have the specified payload and that will have a header that contain the
     * specified message name and message type
     *
     * @param payload the payload of the message
     * @param messageName the name of the message
     * @param messageType the type of the message
     */
    public void sendMessage(MessagePayload payload, String messageName, ServerMessageType messageType) {
        ServerMessageHeader messageHeader = new ServerMessageHeader(messageName, messageType);
        MessageFromServer messageFromServer = new MessageFromServer(messageHeader, payload);
        connection.asyncSend(messageFromServer);
    }

    /**
     * Sends a message to all clients of the game, with the help of the game lobby
     *
     * @param payload the payload of the message
     * @param messageName the name of the message
     * @param messageType the type of the message
     */
    public void sendBroadcast(MessagePayload payload, String messageName, ServerMessageType messageType) {
        ServerMessageHeader messageHeader = new ServerMessageHeader(messageName, messageType);
        MessageFromServer messageFromServer = new MessageFromServer(messageHeader, payload);
        gameLobby.broadcast(messageFromServer);
    }

    /**
     * Send the specified error, with the specified description to the client
     *
     * @param error the error type that will be sent to the client
     * @param description the description of the error
     */
    public void sendError(ErrorMessageType error, String description) {
        connection.sendError(error, description);
    }

    /**
     * Respond to an event if that contains an event with a message from the client as a new value. The message have to
     * have PLAYER_SETUP or ACTION as the message type otherwise nothing will be done.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        MessageFromClient message = (MessageFromClient) evt.getNewValue();
        ClientMessageType messageType = message.getClientMessageHeader().getMessageType();
        //Message type will always be different from GAME_SETUP
        switch (messageType) {
            case PLAYER_SETUP -> {
                fireSetupMessageEvent(message);
                gameLobby.notifySetupChanges();
            }
            case ACTION -> fireActionMessageEvent(message);
        }
    }
}
