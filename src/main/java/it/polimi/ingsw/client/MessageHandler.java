package it.polimi.ingsw.client;

public class MessageHandler {
    private final ConnectionToServer connection;

    public MessageHandler(ConnectionToServer connection) {
        this.connection = connection;
    }

    /*public void handleMessage(MessageFromServer message) {
        if (message.getServerAnswer() != null) {
            handleServerAnswer(message.getServerAnswer());
            return;
        }
        if (message.getSetupPlayerMessage() != null) {
            handleSetupPlayerMessage(message.getSetupPlayerMessage());
            return;
        }
        if (message.getGameUpdateMessage() != null) {
            handleGameUpdateMessage(message.getGameUpdateMessage());
        }
    }

    public void handleServerAnswer(ServerAnswer message) {

    }

    public void handleSetupPlayerMessage(SetupPlayerMessage message) {
        //Just an example with RequestUsernameMessage, we should use listeners
        System.out.println(message.getMessage());
        Scanner sc = new Scanner(System.in);
        PlayerSetupMessage setupMessage = new NicknameMessage(sc.next());
        connection.asyncWriteToServer(setupMessage);
    }

    public void handleGameUpdateMessage(GameUpdateMessage message) {

    }*/
}
