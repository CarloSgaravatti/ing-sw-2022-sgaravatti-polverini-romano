package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.items.*;
import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * GameMainSceneController controls the main scene (gameMainScene.fxml) of the game in the gui
 *
 * @see it.polimi.ingsw.client.GUI.controllers.FXMLController
 * @see javafx.fxml.Initializable
 */
public class GameMainSceneController extends FXMLController implements Initializable {
    private ModelView modelView;
    private String clientNickname;
    @FXML private AnchorPane root;
    @FXML private AnchorPane islandsMap;
    @FXML private HBox cloudBox;
    @FXML private VBox playersBox;
    @FXML private AnchorPane clientSchoolPane;
    @FXML private AnchorPane secondPlayerSchoolPane;
    @FXML private AnchorPane thirdPlayerSchoolPane;
    @FXML private FlowPane assistantsPane;
    @FXML private ImageView bag;
    @FXML private Button accordionButton;
    @FXML private VBox characterBox;
    @FXML private AnchorPane character;
    @FXML private CharacterController characterController;
    @FXML private VBox turnManagementBox;
    private AssistantsTab assistantsTab;
    private final Map<String, Pair<Integer, SchoolBox>> playersSchools = new HashMap<>();
    private Map<Integer, CharacterImage> characters;
    private IslandMap islands;
    private boolean studentsDraggable;
    private boolean motherNatureDraggable;
    private boolean cloudSelectable;
    private boolean characterSelectable;
    private HBox decisionBox;
    private Label actionDescriptionLabel;
    private final Pair<String, String> lastAction = new Pair<>(); //<actionName, actionArguments>
    private final Object accordionButtonLock = new Object();
    private final EventHandler<MouseEvent> assistantEventHandler = mouseEvent -> {
        if (assistantsTab.isAssistantSelectable()) {
            ImageView assistantImage = (ImageView) mouseEvent.getTarget();
            String assistantId = assistantImage.getId().substring("Assistant".length());
            lastAction.setFirst("PlayAssistant");
            lastAction.setSecond(assistantId);
            decisionBox.setVisible(true);
            actionDescriptionLabel.setText("You have chosen assistant " + assistantId + ".");
            moveAccordionDown();
            assistantsTab.setAssistantSelectable(false);
        }
    };
    private final EventHandler<MouseEvent> cloudSelectionHandler = mouseEvent -> {
        if (cloudSelectable) {
            CloudSubScene cloudSubScene = (CloudSubScene) mouseEvent.getTarget();
            int cloudId = cloudSubScene.getCloudId();
            lastAction.setFirst("PickFromCloud");
            lastAction.setSecond(Integer.toString(cloudId));
            decisionBox.setVisible(true);
            actionDescriptionLabel.setText("You have chosen cloud " + cloudId + ".");
            cloudSelectable = false;
        }
    };

    /**
     * Move down or up (in base of the current position) the accordion that contains the school boxes and the assistant
     * images when the accordion button is pressed
     *
     * @param event the event fired when the button is pressed
     */
    @FXML
    void onAccordionButtonPress(ActionEvent event) {
        Button button = (Button) event.getTarget();
        synchronized (accordionButtonLock) {
            String buttonText = button.getText();
            double yTranslation = ((AnchorPane) playersBox.getChildren().get(1)).getHeight();
            TranslateTransition transition = new TranslateTransition();
            transition.setNode(playersBox);
            transition.setDuration(Duration.millis(500));
            if (buttonText.equals("^")) {
                transition.setByY(-yTranslation);
                button.setText("v");
            } else {
                transition.setByY(yTranslation);
                button.setText("^");
            }
            transition.play();
        }
    }

    /**
     * Quits the application after the user select the corresponding item in the quit menu
     *
     * @param event the event fired when the item is pressed
     */
    @FXML
    void quitApplication(ActionEvent event) {
        firePropertyChange("Quit", null, null);
    }

    /**
     * Quits the game and returns to the main menu after the user select the corresponding item in the quit menu
     *
     * @param event the event fired when the item is pressed
     */
    @FXML
    void returnToMainMenu(ActionEvent event) {
        firePropertyChange("QuitGame", null, null);
    }

    /**
     * Display an error popup after the user have made an error in an action
     *
     * @param error the type of error
     * @param errorInfo the description of the error
     */
    @Override
    public void onError(ErrorMessageType error, String errorInfo) {
        if (error == ErrorMessageType.ILLEGAL_ARGUMENT && lastAction.getFirst().equals("MoveStudents")) {
            rollbackMoveStudents();
        }
        ErrorPopupController errorPopup = new ErrorPopupController();
        errorPopup.show(this, errorInfo);
        root.getChildren().add(errorPopup);
    }

    /**
     * Initialize the scene
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Node> actionButtons = turnManagementBox.getChildren().subList(2, turnManagementBox.getChildren().size() - 2);
        decisionBox = (HBox) turnManagementBox.getChildren().get(turnManagementBox.getChildren().size() - 1);
        actionDescriptionLabel = (Label) turnManagementBox.getChildren().get(turnManagementBox.getChildren().size() - 2);
        decisionBox.setVisible(false);
        actionButtons.forEach(node -> {
            Button button = (Button) node;
            button.setOnAction(actionEvent -> {
                if (button.getOpacity() != 1) return;
                if (button.getEffect() == null) return;
                switch (button.getText()) {
                    case "PlayAssistant" -> onStartPlayAssistant();
                    case "MoveStudents" -> onStartMoveStudents();
                    case "MoveMotherNature" -> onStartMoveMotherNature();
                    case "PickFromCloud" -> onStartPickFromCloud();
                    case "PlayCharacter" -> onStartPlayCharacter();
                    case "EndTurn" -> firePropertyChange("EndTurn", null, null);
                }
            });
        });
        Button confirmActionButton = (Button) decisionBox.getChildren().get(0);
        confirmActionButton.setOnAction(actionEvent -> {
            if (decisionBox.isVisible()) {
                firePropertyChange(lastAction.getFirst(), null, lastAction.getSecond());
                System.out.println(lastAction.getFirst() + " " + lastAction.getSecond());
            }
            actionDescriptionLabel.setText("");
            decisionBox.setVisible(false);
        });
        Button cancelActionButton = (Button) decisionBox.getChildren().get(1);
        cancelActionButton.setOnAction(actionEvent -> {
            actionDescriptionLabel.setText("");
            decisionBox.setVisible(false);
            if (lastAction.getFirst().equals("MoveStudents")) {
                rollbackMoveStudents();
            }
        });
        GenericBackgroundController genericBackgroundController = new GenericBackgroundController();
        this.root.getChildren().add(0, genericBackgroundController);
    }

    /**
     * Initialize components that depend on the model view after the game initializations message have arrived
     *
     * @param modelView the model view of the game
     * @param clientNickname the nickname of the user of the application
     */
    public void initializeBoard(ModelView modelView, String clientNickname) {
        this.modelView = modelView;
        this.clientNickname = clientNickname;
        if (modelView.isExpert()) {
            characters = new HashMap<>();
            ExpertFieldView expertField = modelView.getField().getExpertField();
            for (Integer character: expertField.getCharacters().keySet()) {
                CharacterImage characterImage = new CharacterImage(character, characterBox.getWidth() / 1.5, expertField);
                characterBox.getChildren().add(characterImage);
                characters.put(character, characterImage);
            }
            setOnCharactersSelection();
            characterController.init(root, this);
        } else {
            turnManagementBox.getChildren().remove(turnManagementBox.getChildren().get(turnManagementBox.getChildren().size() - 4));
        }
        double yTranslation = ((AnchorPane) playersBox.getChildren().get(1)).getHeight();
        playersBox.setTranslateY(yTranslation);
        int numClouds =  modelView.getField().getCloudStudents().size();
        for (int i = 0; i < numClouds; i++) {
            CloudSubScene cloudSubScene = new CloudSubScene(numClouds == 3, i);
            cloudBox.getChildren().add(cloudSubScene);
            cloudSubScene.initializeStudents(RealmType.getRealmsFromIntegerRepresentation(modelView
                    .getField().getCloudStudents().get(i)));
            cloudSubScene.addEventHandler(MouseEvent.MOUSE_CLICKED, cloudSelectionHandler);
        }
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        assistantsTab = new AssistantsTab(assistantsPane, modelView);
        assistantsTab.setEventHandler(MouseEvent.MOUSE_CLICKED, assistantEventHandler);
        playersSchools.put(clientNickname,
                new Pair<>(1, new SchoolBox(clientNickname, clientSchoolPane, modelView, modelView.isExpert())));
        List<String> otherPlayers = modelView.getPlayers().keySet().stream().filter(p -> !p.equals(clientNickname)).toList();
        playersSchools.put(otherPlayers.get(0),
                new Pair<>(2, new SchoolBox(otherPlayers.get(0), secondPlayerSchoolPane, modelView, modelView.isExpert())));
        tabs.getTabs().get(2).setText(otherPlayers.get(0) + "'s school");
        if (otherPlayers.size() == 2) {
            playersSchools.put(otherPlayers.get(1),
                    new Pair<>(3, new SchoolBox(otherPlayers.get(1), thirdPlayerSchoolPane, modelView, modelView.isExpert())));
            tabs.getTabs().get(3).setText(otherPlayers.get(1) + "'s school");
        } else {
            tabs.getTabs().remove(3);
        }
        islands = new IslandMap(islandsMap, modelView);
        new DragAndDropManager().registerEvents();
        checkAssistants();
    }

    /**
     * Checks if players have already played an assistant when the scene is loaded and, if so, updates the images in the
     * school boxes. This can happen when a restored game that was already started is restarted
     */
    private void checkAssistants() {
        for (String player: modelView.getPlayers().keySet()) {
            Pair<Integer, Integer> lastAssistant = modelView.getPlayers().get(player).getLastPlayedAssistant();
            if (lastAssistant != null && lastAssistant.getFirst() != 0) {
                setAssistantImage(player, lastAssistant.getFirst());
            }
        }
    }

    /**
     * Notifies the user that it is his turn in the round
     *
     * @param actionCommands the actions that the user can do in the turn
     * @param possibleActions the actions that the user can do now (and not only in the turn)
     */
    public void onTurn(List<String> actionCommands, List<String> possibleActions) {
        Label turnInfo = (Label) turnManagementBox.getChildren().get(1);
        turnInfo.setText("Is your turn! Select what to do");
        characterBox.getChildren().forEach(c -> c.setEffect(null));
        List<Node> actionButtons = turnManagementBox.getChildren().subList(2, turnManagementBox.getChildren().size() - 2);
        actionButtons.forEach(node -> {
            Button button = (Button) node;
            if (actionCommands.contains(button.getText()) || button.getText().equals("EndTurn")) button.setOpacity(1);
            else button.setOpacity(0.5);
            if (possibleActions.contains(button.getText()) ||
                    (button.getText().equals("EndTurn") && possibleActions.size() == 1 && possibleActions.contains("PlayCharacter"))) {
                DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.YELLOW, 15, 0.2, 0, 0);
                button.setEffect(dropShadow);
            } else button.setEffect(null);
        });
        assistantsTab.updateAssistants();
    }

    /**
     * Handles the start of a PlayAssistant action by making assistants selectable
     */
    public void onStartPlayAssistant() {
        assistantsTab.setAssistantSelectable(true);
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(0));
        moveAccordionUp();
    }

    /**
     * Handles the start of a MoveStudents action by making students draggable
     */
    public void onStartMoveStudents() {
        studentsDraggable = true;
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(clientNickname).getFirst()));
        moveAccordionUp();
    }

    /**
     * Handles the start of a MoveMotherNature action by making mother nature draggable
     */
    public void onStartMoveMotherNature() {
        motherNatureDraggable = true;
        moveAccordionDown();
    }

    /**
     * Handles the start of a PickFromCloud action by making clouds selectable
     */
    public void onStartPickFromCloud() {
        cloudSelectable = true;
        moveAccordionDown();
    }

    public void onStartPlayCharacter() {
        characterSelectable = true;
        moveAccordionDown();
        for (CharacterImage characterImage: characters.values()) {
            characterImage.getStyleClass().add("selectable-character");
        }
    }

    /**
     * Handles the start of a play character action by making characters images selectable
     */
    public void setOnCharactersSelection() {
        EventHandler<MouseEvent> characterEventHandler = mouseEvent -> {
            if (characterSelectable) {
                CharacterImage character = (CharacterImage) ((ImageView) mouseEvent.getTarget()).getParent();
                int characterId = character.getCharacterId();
                this.character.setTranslateX(-root.getWidth());
                characterController.show(characterId);
            }
        };
        characters.values().forEach(c -> c.addEventHandler(MouseEvent.MOUSE_CLICKED, characterEventHandler));
    }

    /**
     * Moves mother nature from the old position to the new position
     *
     * @param oldPosition the old position of mother nature
     * @param newPosition the new position of mother nature
     */
    public void moveMotherNature(int oldPosition, int newPosition) {
        moveAccordionDown();
        islands.moveMotherNature(oldPosition, newPosition);
    }

    /**
     * Move the specified students to the dining room of the specified player
     *
     * @param player the player that will have the students on the dining room
     * @param students the students moved
     */
    public void moveStudentsToDiningRoom(String player, RealmType ... students) {
        viewSchoolOf(player);
        for (RealmType student: students) {
            playersSchools.get(player).getSecond().moveStudentFromEntranceToDiningRoom(student);
        }
    }

    /**
     * Moves the specified students form the entrance of the player to the island
     *
     * @param player the player who has move students
     * @param islandId the id of the island
     * @param students the students moved
     */
    public void moveStudentsToIsland(String player, int islandId, RealmType ... students) {
        AnchorPane tabPaneFather = (AnchorPane) playersBox.getChildren().get(1);
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(player).getFirst()));
        Pair<Double, Double> tabPaneGlobalPosition = new Pair<>(tabPaneFather.getLayoutX() + tabs.getLayoutX() + playersBox.getLayoutX(),
                tabPaneFather.getLayoutY() + tabs.getLayoutY() + playersBox.getLayoutY());
        AnchorPane borderPaneTop = (AnchorPane) ((BorderPane) islandsMap.getParent()).getTop();
        double islandMapLayoutY = BorderPane.getMargin(islandsMap).getTop() + /*BorderPane.getMargin(borderPaneTop).getTop() +*/ borderPaneTop.getHeight();
        double islandMapLayoutX = BorderPane.getMargin(islandsMap).getLeft() + BorderPane.getMargin(characterBox).getLeft() + characterBox.getWidth();
        Pair<Double, Double> islandLayout = new Pair<>(islandMapLayoutX + islands.getIslandPosition(islandId).getFirst(),
                islandMapLayoutY + islands.getIslandPosition(islandId).getSecond());
        ParallelTransition parallelTransition = null;
        for (RealmType student: students) {
            SchoolBox playerSchool = playersSchools.get(player).getSecond();
            Pair<Double, Double> studentRelativePosition = playerSchool.getEntranceStudentLayout(student);
            playerSchool.removeFromEntrance(student, false);
            StudentImage fakeStudent = new StudentImage(playerSchool.getDimStudentsRadius(), student);
            root.getChildren().add(fakeStudent);
            AnchorPane.setLeftAnchor(fakeStudent, studentRelativePosition.getFirst() + tabPaneGlobalPosition.getFirst());
            AnchorPane.setTopAnchor(fakeStudent,studentRelativePosition.getSecond() + tabPaneGlobalPosition.getSecond());
            TranslateTransition transition = new TranslateTransition(Duration.millis(2000), fakeStudent);
            transition.setByX(islandLayout.getFirst() - AnchorPane.getLeftAnchor(fakeStudent));
            transition.setByY(islandLayout.getSecond() - AnchorPane.getTopAnchor(fakeStudent));
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), fakeStudent);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.5);
            fadeTransition.setCycleCount(4);
            fadeTransition.setAutoReverse(true);
            parallelTransition = new ParallelTransition(fakeStudent, transition, fadeTransition);
            parallelTransition.playFrom(Duration.millis(1000));
            transition.setOnFinished(actionEvent -> {
                root.getChildren().remove(fakeStudent);
                islands.getIslands().get(islandId).addStudent(student);
            });
        }
        if (parallelTransition != null) parallelTransition.setOnFinished(actionEvent -> moveAccordionUp());
    }

    /**
     * Move the specified tower to the island and the last tower, if not null, to the owner school
     *
     * @param lastTower the last tower present on the island
     * @param newTower the new tower of the island
     * @param islandId th id of the island
     */
    public void moveTowers(TowerType lastTower, TowerType newTower, int islandId) {
        int numTowers = modelView.getField().getIsland(islandId).getSecond();
        if (lastTower != null) {
            Optional<PlayerView> lastTowerPlayer = modelView.getPlayers().values().stream()
                    .filter(p -> p.getPlayerTower() == lastTower).findFirst();
            lastTowerPlayer.ifPresent(playerView -> {
                Optional<String> playerName = modelView.getPlayers().keySet().stream()
                        .filter(k -> modelView.getPlayers().get(k).equals(playerView)).findFirst();
                playerName.ifPresent(player -> {
                    SchoolBox playerSchool = playersSchools.get(player).getSecond();
                    for (int i = 0; i < numTowers; i++) playerSchool.insertTower();
                });
            });
        }
        Optional<PlayerView> newTowerPlayer = modelView.getPlayers().values().stream()
                .filter(p -> p.getPlayerTower() == newTower).findFirst();
        newTowerPlayer.ifPresent(playerView -> {
            Optional<String> playerName = modelView.getPlayers().keySet().stream()
                    .filter(k -> modelView.getPlayers().get(k).equals(playerView)).findFirst();
            playerName.ifPresent(player -> {
                SchoolBox playerSchool = playersSchools.get(player).getSecond();
                for (int i = 0; i < numTowers; i++) playerSchool.removeTower();
            });
        });
        islands.getIslandById(islandId).ifPresent(i -> i.addTower(newTower));
    }

    /**
     * Merge the specified islands after an island unification
     *
     * @param islands the unified islands
     */
    public void mergeIslands(List<Integer> islands) {
        this.islands.mergeIslands2(islands);
    }

    /**
     * Notifies the user that it is another player turn (different from the user)
     *
     * @param newActivePlayer the new active player of the turn
     */
    public void notifyNewTurn(String newActivePlayer) {
        Label turnInfo = (Label) turnManagementBox.getChildren().get(1);
        turnInfo.setText("Now is " + newActivePlayer + "'s turn");
        characterBox.getChildren().forEach(c -> c.setEffect(null));
        turnManagementBox.getChildren().subList(2, turnManagementBox.getChildren().size() - 2)
                .forEach(node -> {
                    node.setOpacity(0.5);
                    node.setEffect(null);
                });
        assistantsTab.updateAssistants();
    }

    /**
     * Set a drop shadow effect on the specified character for the rest of the turn when a player plays it
     *
     * @param characterId the id of the character
     */
    public void setDropShadowOnCharacter(int characterId) {
        CharacterImage character = characters.get(characterId);
        DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.BLUE, 15, 0.2, 0, 0);
        character.setEffect(dropShadow);
    }

    /**
     * Returns the anchor pane that contains the islands
     *
     * @return the anchor pane that contains the islands
     */
    public IslandMap getIslandMap() {
        return islands;
    }

    /**
     * Returns the assistant tab, where the assistant images of the client are present
     *
     * @return the assistant tab, where the assistant images of the client are present
     */
    public AssistantsTab getAssistantsTab() {
        return assistantsTab;
    }

    /**
     * Returns the image of the character that have the specified id
     *
     * @param characterId the id of the character
     * @return the image of the character that have the specified id
     */
    public CharacterImage getCharacterImage(int characterId) {
        return characters.get(characterId);
    }

    /**
     * Sets the assistant image of the last played assistant of the player in the player school box
     *
     * @param player the name of the player
     * @param assistant the last assistant played by the player
     */
    public void setAssistantImage(String player, int assistant) {
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/assistants/Assistant" + assistant + ".png")));
        playersSchools.get(player).getSecond().setAssistantImage(image);
    }

    /**
     * Insert a professor of the specified type in the school of the player
     *
     * @param player the player who has owned the professor
     * @param professor the type of professor owned by the player
     */
    public void insertProfessor(String player, RealmType professor) {
        viewSchoolOf(player);
        playersSchools.get(player).getSecond().insertProfessor(professor);
    }

    /**
     * Move students from the specified cloud to the entrance of the specified player
     *
     * @param player the name of the player
     * @param cloudId the id of the cloud
     * @param students the students moved
     */
    public void moveStudentsFromCloud(String player, int cloudId, RealmType[] students) {
        CloudSubScene cloudSubScene = (CloudSubScene) cloudBox.getChildren().get(cloudId + 1);
        cloudSubScene.ResetStudentImage();
        SchoolBox schoolBox = playersSchools.get(player).getSecond();
        schoolBox.updateEntrance();
    }

    /**
     * Move the students to the clouds at the beginning of a new planning phase
     */
    public void moveStudentsToRefillClouds() {
        moveAccordionDown();
        Map<Integer, Integer[]> cloudStudents = modelView.getField().getCloudStudents();
        for (Integer cloudId: cloudStudents.keySet()) {
            RealmType[] students = RealmType.getRealmsFromIntegerRepresentation(cloudStudents.get(cloudId));
            CloudSubScene cloud = (CloudSubScene) cloudBox.getChildren().get(cloudId + 1);
            cloud.initializeStudents(students);
        }
    }

    /**
     * Move the accordion that contains the school boxes down, if it is currently up
     */
    public void moveAccordionDown() {
        if (accordionButton.getText().equals("v")) {
            accordionButton.fire();
        }
    }

    /**
     * Move the accordion that contains the school boxes up, if it is currently down
     */
    public void moveAccordionUp() {
        if (accordionButton.getText().equals("^")) {
            accordionButton.fire();
        }
    }

    /**
     * Move the accordion up and shows the school of the specified player
     *
     * @param playerName the player who owns the school that will be seen
     */
    public void viewSchoolOf(String playerName) {
        moveAccordionUp();
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(playerName).getFirst()));
    }

    /**
     * Returns the model view associated to this controller
     *
     * @return the model view associated to this controller
     */
    public ModelView getModelView() {
        return modelView;
    }

    /**
     * Returns the school box of the user of the client and shows it
     *
     * @return the school box of the user of the client
     */
    public SchoolBox getSchoolOfClient() {
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(clientNickname).getFirst()));
        return playersSchools.get(clientNickname).getSecond();
    }

    /**
     * Returns the school box of the specified player
     *
     * @param nickname the name of the player
     * @return the school box of the specified player
     */
    public SchoolBox getSchoolBox(String nickname) {
        return playersSchools.get(nickname).getSecond();
    }

    /**
     * Returns the islands map of the game
     *
     * @return the islands map of the game
     */
    public IslandMap getIslands() {
        return islands;
    }

    /**
     * Returns the nickname of the user
     *
     * @return the nickname of the user
     */
    public String getClientNickname() {
        return clientNickname;
    }

    /**
     * Rollback the students' movement after an error occurred or after the user decided to cancel the movement
     */
    private void rollbackMoveStudents() {
        String[] actionArguments = lastAction.getSecond().split(" ");
        int i = 0;
        while (i < actionArguments.length) {
            RealmType realmType = RealmType.getRealmByAbbreviation(actionArguments[i]);
            SchoolBox clientSchool = getSchoolOfClient();
            if (actionArguments[i + 1].equals("ToDiningRoom")) {
                clientSchool.removeFromDiningRoom(realmType);
                clientSchool.insertStudentEntrance(realmType);
                i += 2;
            } else {
                clientSchool.insertStudentEntrance(realmType);
                int islandId = Integer.parseInt(actionArguments[i + 2]);
                islands.getIslandById(islandId).ifPresent(islandSubScene -> islandSubScene.updateIsland(modelView.getField()));
                i += 3;
            }
        }
    }

    /**
     * Displays a dialog popup that will inform the player that the game has ended and informs the user on the game result.
     * The user will be asked to choose if he wants to quit or return to the main menu
     *
     * @param evt the end game event
     */
    public void onEndGameEvent(PropertyChangeEvent evt) {
        String popupText = switch (evt.getPropertyName()) {
            case "Winner" -> "You have won!";
            case "Loser" -> "You have lost! " + evt.getNewValue() + " has won";
            case "Tie" -> "It's a tie! These are the tiers: " + Arrays.toString((String[]) evt.getNewValue());
            case "TieLoser" -> "You have lose, but nobody has won, these are the tiers" +
                        Arrays.toString((String[]) evt.getNewValue());
            case "Disconnection" -> "Oh no, " + evt.getNewValue() + " have disconnected!";
            default -> "";
        };
        popupText += "\n\n Decide what to do: you can return to the main menu or exit the application.";
        PopupDialogController popupDialog = new PopupDialogController();
        popupDialog.show(popupText, this);
        root.getChildren().add(popupDialog);
    }

    /**
     * Handles the close event on the error popup by deleting the popup from the scene
     *
     * @param errorPopup the error popup that has to be closed
     */
    public void closeErrorPopup(ErrorPopupController errorPopup) {
        root.getChildren().remove(errorPopup);
    }

    /**
     * DragAndDropManager handles all drag and drop events for the movement of students and mother nature
     */
    class DragAndDropManager {
        private RealmType lastStudentDragged;
        private StringBuilder action = null;
        private final int studentsToMove = (modelView.getPlayers().size() == 3) ? 4 : 3;
        private final List<RealmType> studentsMoved = new ArrayList<>();
        private final EventHandler<MouseEvent> studentDragStart = mouseEvent -> {
            if (studentsDraggable) {
                StudentImage student = (StudentImage) mouseEvent.getTarget();
                lastStudentDragged = student.getStudentType();
                System.out.println("Drag start");
                if (action == null) {
                    action = new StringBuilder();
                    studentsMoved.clear();
                }
                Dragboard db = student.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(student.getStudentType().getAbbreviation());
                db.setContent(content);
                mouseEvent.consume();
            }
        };
        private final EventHandler<DragEvent> dragOverIslands = dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            Button accordionButton = (Button) ((AnchorPane) playersBox.getChildren().get(0)).getChildren().get(0);
            if (accordionButton.getText().equals("v")) accordionButton.fire();
            dragEvent.consume();
        };
        private final EventHandler<DragEvent> dragOverDiningRoom = dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            dragEvent.consume();
        };
        private final EventHandler<DragEvent> dropOnDiningRoom = dragEvent -> {
            if (!studentsDraggable) return;
            playersSchools.get(clientNickname).getSecond().insertInDiningRoom(lastStudentDragged);
            playersSchools.get(clientNickname).getSecond().removeFromEntrance(lastStudentDragged, true);
            action.append(dragEvent.getDragboard().getString()).append(" ToDiningRoom ");
            studentsMoved.add(lastStudentDragged);
            if (studentsMoved.size() == studentsToMove) {
                onEndStudentsDragAndDropAction();
            }
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        };
        private final EventHandler<DragEvent> dragOverIsland = dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            dragEvent.consume();
        };
        private final EventHandler<DragEvent> dropOnIsland = dragEvent -> {
            IslandSubScene island = (IslandSubScene) dragEvent.getTarget();
            int islandId = island.getIslandId();
            if (studentsDraggable) {
                playersSchools.get(clientNickname).getSecond().removeFromEntrance(lastStudentDragged, true);
                action.append(dragEvent.getDragboard().getString()).append(" ToIsland ").append(islandId).append(" ");
                studentsMoved.add(lastStudentDragged);
                island.addStudent(lastStudentDragged);
                if (studentsMoved.size() == studentsToMove) {
                    onEndStudentsDragAndDropAction();
                }
            } else if (motherNatureDraggable) {
                int movement = (islandId + modelView.getField().getIslandSize()
                        - modelView.getField().getMotherNaturePosition()) % modelView.getField().getIslandSize();
                lastAction.setFirst("MoveMotherNature");
                lastAction.setSecond(Integer.toString(movement));
                decisionBox.setVisible(true);
                actionDescriptionLabel.setText("You have moved mother nature by " + movement + " positions.");
                motherNatureDraggable = false;
            }
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        };
        private final EventHandler<MouseEvent> dragStartMotherNature = mouseEvent -> {
            AnchorPane motherNature = (AnchorPane) mouseEvent.getTarget();
            if (motherNatureDraggable && motherNature.isVisible()) {
                Dragboard db = motherNature.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString("Mother nature");
                db.setContent(content);
                mouseEvent.consume();
            }
        };
        private final EventHandler<DragEvent> dragOverAccordionButton = dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            Button accordionButton = (Button) dragEvent.getTarget();
            if (accordionButton.getText().equals("^")) accordionButton.fire();
            dragEvent.consume();
        };

        /**
         * Register the drag and drop events for all components that are involved in such events. These components are the
         * entrance students and mother nature for the drag start event, the island and the dining room for the drag over event
         */
        public void registerEvents() {
            islandsMap.setOnDragOver(dragOverIslands);
            islands.setDragAndDropHandlers(dragOverIsland, dropOnIsland, dragStartMotherNature);
            SchoolBox clientSchool = playersSchools.get(clientNickname).getSecond();
            clientSchool.setDragStartStudentHandler(studentDragStart);
            clientSchool.registerDiningRoomDragAndDrop(dragOverDiningRoom, dropOnDiningRoom);
            Button accordionButton = (Button) ((AnchorPane) playersBox.getChildren().get(0)).getChildren().get(0);
            accordionButton.setOnDragOver(dragOverAccordionButton);
        }

        /**
         * Handles the event that occurs when the user have moved the correct number of students.
         */
        private void onEndStudentsDragAndDropAction() {
            moveAccordionDown();
            studentsDraggable = false;
            lastAction.setFirst("MoveStudents");
            lastAction.setSecond(action.substring(0, action.length() - 1));
            decisionBox.setVisible(true);
            actionDescriptionLabel.setText("You have moved these students: " + studentsMoved + ".");
            action = null;
            studentsMoved.clear();
        }
    }
}
