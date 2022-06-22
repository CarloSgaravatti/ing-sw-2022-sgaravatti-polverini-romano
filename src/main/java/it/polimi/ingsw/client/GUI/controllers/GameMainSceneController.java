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

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.*;
import java.util.List;

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
    @FXML private Menu turnManagement;
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
        } else {
            //TODO
        }
    };
    private final EventHandler<MouseEvent> cloudSelectionHandler = mouseEvent -> {
        if (cloudSelectable) {
            CloudImage cloud = (CloudImage) ((ImageView) mouseEvent.getTarget()).getParent();
            int cloudId = cloud.getCloudId();
            lastAction.setFirst("PickFromCloud");
            lastAction.setSecond(Integer.toString(cloudId));
            decisionBox.setVisible(true);
            actionDescriptionLabel.setText("You have chosen cloud " + cloudId + ".");
            cloudSelectable = false;
        } else {
            //TODO
        }
    };

    @FXML
    void onAccordionButtonPress(ActionEvent event) {
        Button button = (Button) event.getTarget();
        //TODO: don't know if synchronization is needed
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

    @Override
    public void onError(ErrorMessageType error, String errorInfo) {

    }

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
            //TODO: if action is MoveStudents, rollback
        });
    }

    @Override
    public void addListener(PropertyChangeListener gui) {
        super.addListener(gui);
        assistantsTab.addListener(gui);
    }

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
        double cloudImageHeight = cloudBox.getHeight();
        int numClouds =  modelView.getField().getCloudStudents().size();
        for (int i = 0; i < numClouds; i++) {
            CloudImage cloud = new CloudImage(i, modelView.getField(), cloudImageHeight);
            cloudBox.getChildren().add(cloud);
            cloud.addEventHandler(MouseEvent.MOUSE_CLICKED, cloudSelectionHandler);
        }
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        assistantsTab = new AssistantsTab(assistantsPane, modelView);
        assistantsTab.setEventHandler(MouseEvent.MOUSE_CLICKED, assistantEventHandler);
        Map<String, PlayerView> players = modelView.getPlayers();
        playersSchools.put(clientNickname,
                new Pair<>(1, new SchoolBox(clientNickname, clientSchoolPane, players.get(clientNickname), modelView.isExpert())));
        List<String> otherPlayers = modelView.getPlayers().keySet().stream().filter(p -> !p.equals(clientNickname)).toList();
        playersSchools.put(otherPlayers.get(0),
                new Pair<>(2, new SchoolBox(otherPlayers.get(0), secondPlayerSchoolPane, players.get(otherPlayers.get(0)), modelView.isExpert())));
        tabs.getTabs().get(2).setText(otherPlayers.get(0) + "'s school");
        if (otherPlayers.size() == 2) {
            playersSchools.put(otherPlayers.get(1),
                    new Pair<>(3, new SchoolBox(otherPlayers.get(1), thirdPlayerSchoolPane, players.get(otherPlayers.get(1)), modelView.isExpert())));
            tabs.getTabs().get(3).setText(otherPlayers.get(1) + "'s school");
        } else {
            tabs.getTabs().remove(3);
        }
        islands = new IslandMap(islandsMap, modelView);
        new DragAndDropManager().registerEvents();
    }

    @Deprecated
    public void onTurn(List<String> actionCommands, List<String> possibleActions) {
        turnManagement.getItems().clear();
        for (String actionCommand : actionCommands) {
            MenuItem action = new MenuItem(actionCommand);
            turnManagement.getItems().add(action);
            switch (actionCommand) {
                case "PlayAssistant" -> action.setOnAction(event -> onStartPlayAssistant());
                case "MoveStudents" -> action.setOnAction(event -> onStartMoveStudents());
                case "MoveMotherNature" -> action.setOnAction(event -> onStartMoveMotherNature());
                case "PickFromCloud" -> action.setOnAction(event -> onStartPickFromCloud());
                case "PlayCharacter" -> action.setOnAction(event -> onStartPlayCharacter());
            }
        }
        MenuItem endTurnItem = new MenuItem("EndTurn");
        turnManagement.getItems().add(endTurnItem);
        endTurnItem.setOnAction(actionEvent -> firePropertyChange("EndTurn", null, null));
    }

    public void onTurnV2(List<String> actionCommands, List<String> possibleActions) {
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
    }

    public void onStartPlayAssistant() {
        assistantsTab.setAssistantSelectable(true);
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(0));
        moveAccordionUp();
    }

    public void onStartMoveStudents() {
        studentsDraggable = true;
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(clientNickname).getFirst()));
        moveAccordionUp();
    }

    public void onStartMoveMotherNature() {
        motherNatureDraggable = true;
        moveAccordionDown();
    }

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

    public void setOnCharactersSelection() {
        EventHandler<MouseEvent> characterEventHandler = mouseEvent -> {
            if (characterSelectable) {
                CharacterImage character = (CharacterImage) ((ImageView) mouseEvent.getTarget()).getParent();
                int characterId = character.getCharacterId();
                this.character.setTranslateX(-root.getWidth());
                characterController.show(characterId, root);
            }
        };
        characters.values().forEach(c -> c.addEventHandler(MouseEvent.MOUSE_CLICKED, characterEventHandler));
    }

    public void moveMotherNature(int oldPosition, int newPosition) {
        moveAccordionDown();
        islands.moveMotherNature(oldPosition, newPosition);
    }

    public void moveStudentsToDiningRoom(String player, RealmType ... students) {
        viewSchoolOf(player);
        for (RealmType student: students) {
            playersSchools.get(player).getSecond().moveStudentFromEntranceToDiningRoom(student);
        }
    }

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
        islands.getIslandsById(islandId).forEach(i -> i.addTower(newTower));
    }

    public void mergeIslands(List<Integer> islands) {
        this.islands.mergeIslands(islands);
    }

    public void notifyNewTurn(String newActivePlayer) {
        Label turnInfo = (Label) turnManagementBox.getChildren().get(1);
        turnInfo.setText("Now is " + newActivePlayer + "'s turn");
        characterBox.getChildren().forEach(c -> c.setEffect(null));
        turnManagementBox.getChildren().subList(2, turnManagementBox.getChildren().size() - 2)
                .forEach(node -> {
                    node.setOpacity(0.5);
                    node.setEffect(null);
                });
    }

    public void setDropShadowOnCharacter(int characterId) {
        CharacterImage character = characters.get(characterId);
        DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.BLUE, 15, 0.2, 0, 0);
        character.setEffect(dropShadow);
    }

    public IslandMap getIslandMap() {
        return islands;
    }

    public AssistantsTab getAssistantsTab() {
        return assistantsTab;
    }

    public CharacterImage getCharacterImage(int characterId) {
        return characters.get(characterId);
    }

    public void setAssistantImage(String player, int assistant) {
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/assistants/Assistant" + assistant + ".png")));
        playersSchools.get(player).getSecond().setAssistantImage(image);
    }

    public void insertProfessorAnimation(String player, RealmType professor) {
        viewSchoolOf(player);
        playersSchools.get(player).getSecond().insertProfessor(professor);
        //TODO: animation (maybe fade transition, whit drop shadow until the end of the transition)
    }

    public void moveAccordionDown() {
        if (accordionButton.getText().equals("v")) {
            accordionButton.fire();
        }
    }

    public void moveAccordionUp() {
        if (accordionButton.getText().equals("^")) {
            accordionButton.fire();
        }
    }

    public void viewSchoolOf(String playerName) {
        moveAccordionUp();
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(playerName).getFirst()));
    }

    public ModelView getModelView() {
        return modelView;
    }

    public SchoolBox getSchoolOfClient() {
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(clientNickname).getFirst()));
        return playersSchools.get(clientNickname).getSecond();
    }

    public SchoolBox getSchoolBox(String nickname) {
        return playersSchools.get(nickname).getSecond();
    }

    public IslandMap getIslands() {
        return islands;
    }

    public String getClientNickname() {
        return clientNickname;
    }

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

        public void registerEvents() {
            islandsMap.setOnDragOver(dragOverIslands);
            islands.setDragAndDropHandlers(dragOverIsland, dropOnIsland, dragStartMotherNature);
            SchoolBox clientSchool = playersSchools.get(clientNickname).getSecond();
            clientSchool.setDragStartStudentHandler(studentDragStart);
            clientSchool.registerDiningRoomDragAndDrop(dragOverDiningRoom, dropOnDiningRoom);
            Button accordionButton = (Button) ((AnchorPane) playersBox.getChildren().get(0)).getChildren().get(0);
            accordionButton.setOnDragOver(dragOverAccordionButton);
        }

        private void onEndStudentsDragAndDropAction() {
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
