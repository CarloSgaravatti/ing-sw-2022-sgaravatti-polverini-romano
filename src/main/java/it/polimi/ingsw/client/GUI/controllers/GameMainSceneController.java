package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.items.*;
import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
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
    private AssistantsTab assistantsTab;
    private final Map<String, Pair<Integer, SchoolBox>> playersSchools = new HashMap<>();
    private Map<Integer, CharacterImage> characters;
    private IslandMap islands;
    private boolean studentsDraggable;
    private boolean motherNatureDraggable;
    private boolean cloudSelectable;
    private boolean characterSelectable;
    private final EventHandler<MouseEvent> assistantEventHandler = mouseEvent -> {
        if (assistantsTab.isAssistantSelectable()) {
            ImageView assistantImage = (ImageView) mouseEvent.getTarget();
            String assistantId = assistantImage.getId().substring("Assistant".length());
            System.out.println("Selected assistant" + assistantId);
            firePropertyChange("PlayAssistant", null, assistantId);
            assistantsTab.setAssistantSelectable(false);
        } else {
            //TODO
        }
    };
    private DragAndDropManager dragAndDropManager;

    @FXML
    void onAccordionButtonPress(ActionEvent event) {
        Button button = (Button) event.getTarget();
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

    @Override
    public void onError(ErrorMessageType error) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*TranslateTransition transition = new TranslateTransition();
        transition.setNode(playersBox);
        transition.setByY(((AnchorPane) playersBox.getChildren().get(1)).getHeight());
        transition.setDuration(Duration.millis(500));
        transition.play();
        double yTranslation = ((AnchorPane) playersBox.getChildren().get(1)).getHeight();
        System.out.println(yTranslation);
        playersBox.setTranslateY(yTranslation);*/
        //accordionButton.fire();
        //root.getChildren().remove(character);
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
        }
        double yTranslation = ((AnchorPane) playersBox.getChildren().get(1)).getHeight();
        System.out.println(yTranslation);
        playersBox.setTranslateY(yTranslation);
        double cloudImageHeight = cloudBox.getHeight();
        int numClouds =  modelView.getField().getCloudStudents().size();
        for (int i = 0; i < numClouds; i++) {
            cloudBox.getChildren().add(new CloudImage(i, modelView.getField(), cloudImageHeight));
        }
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        assistantsTab = new AssistantsTab(assistantsPane, modelView);
        assistantsTab.setEventHandler(MouseEvent.MOUSE_CLICKED, assistantEventHandler);
        Map<String, PlayerView> players = modelView.getPlayers();
        playersSchools.put(clientNickname,
                new Pair<>(1, new SchoolBox(clientNickname, clientSchoolPane, players.get(clientNickname))));
        List<String> otherPlayers = modelView.getPlayers().keySet().stream().filter(p -> !p.equals(clientNickname)).toList();
        playersSchools.put(otherPlayers.get(0),
                new Pair<>(2, new SchoolBox(otherPlayers.get(0), secondPlayerSchoolPane, players.get(otherPlayers.get(0)))));
        tabs.getTabs().get(2).setText(otherPlayers.get(0) + "'s school");
        if (otherPlayers.size() == 2) {
            playersSchools.put(otherPlayers.get(1),
                    new Pair<>(3, new SchoolBox(otherPlayers.get(1), thirdPlayerSchoolPane, players.get(otherPlayers.get(1)))));
            tabs.getTabs().get(3).setText(otherPlayers.get(1) + "'s school");
        } else {
            tabs.getTabs().remove(3);
        }
        islands = new IslandMap(islandsMap, modelView);
        dragAndDropManager = new DragAndDropManager();
        dragAndDropManager.registerEvents();
    }

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
        moveAccordionUp();
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(player).getFirst()));
        for (RealmType student: students) {
            playersSchools.get(player).getSecond().moveStudentFromEntranceToDiningRoom(student);
        }
    }

    public void moveStudentsToIsland(String player, int islandId, RealmType ... students) {
        moveAccordionUp();
        AnchorPane tabPaneFather = (AnchorPane) playersBox.getChildren().get(1);
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(player).getFirst()));
        Pair<Double, Double> tabPaneGlobalPosition = new Pair<>(tabPaneFather.getLayoutX() + tabs.getLayoutX() + playersBox.getLayoutX(),
                tabPaneFather.getLayoutY() + tabs.getLayoutY() + playersBox.getLayoutY());
        Pair<Double, Double> islandLayout = new Pair<>(islandsMap.getLayoutX() + islands.getIslandPosition(islandId).getFirst(),
                islandsMap.getLayoutY() + islands.getIslandPosition(islandId).getSecond());
        moveAccordionUp();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        for (RealmType student: students) {
            SchoolBox playerSchool = playersSchools.get(player).getSecond();
            Pair<Double, Double> studentRelativePosition = playerSchool.getEntranceStudentLayout(student);
            playerSchool.removeFromEntrance(student, false);
            StudentImage fakeStudent = new StudentImage(playerSchool.getDimStudentsRadius(), student);
            AnchorPane.setTopAnchor(fakeStudent, studentRelativePosition.getSecond() + tabPaneGlobalPosition.getSecond());
            AnchorPane.setLeftAnchor(fakeStudent, studentRelativePosition.getFirst() + tabPaneGlobalPosition.getFirst());
            root.getChildren().add(fakeStudent);
            TranslateTransition transition = new TranslateTransition(Duration.millis(2000), fakeStudent);
            transition.setByX(fakeStudent.getLayoutX() - islandLayout.getFirst());
            transition.setByY(fakeStudent.getLayoutY() - islandLayout.getSecond());
            moveAccordionDown();
            transition.play();
            transition.setOnFinished(actionEvent -> {
                root.getChildren().remove(fakeStudent);
                //TODO: add student to island
            });
        }

    }

    public AssistantsTab getAssistantsTab() {
        return assistantsTab;
    }

    public void setAssistantImage(String player, int assistant) {
        Image image = new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/assistants/Assistant" + assistant + ".png")));
        playersSchools.get(player).getSecond().setAssistantImage(image);
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

    public ModelView getModelView() {
        return modelView;
    }

    public SchoolBox getSchoolOfClient() {
        TabPane tabs = (TabPane) ((AnchorPane) playersBox.getChildren().get(1)).getChildren().get(0);
        tabs.getSelectionModel().select(tabs.getTabs().get(playersSchools.get(clientNickname).getFirst()));
        return playersSchools.get(clientNickname).getSecond();
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
        private int studentsMoved;
        //This event handler is assigned to students of entrance
        private final EventHandler<MouseEvent> studentDragStart = mouseEvent -> {
            if (studentsDraggable) {
                StudentImage student = (StudentImage) mouseEvent.getTarget();
                lastStudentDragged = student.getStudentType();
                System.out.println("Drag start");
                if (action == null) {
                    action = new StringBuilder();
                    studentsMoved = 0;
                }
                Dragboard db = student.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(student.getStudentType().getAbbreviation());
                db.setContent(content);
                mouseEvent.consume();
            }
        };
        //TODO: check if useful (otherwise delete)
        private final EventHandler<DragEvent> dragLeaveFromPlayersBox = dragEvent -> {
            VBox playersBox = (VBox) dragEvent.getTarget();
            Button accordionButton = (Button) ((AnchorPane) playersBox.getChildren().get(0)).getChildren().get(0);
            accordionButton.fire();
            dragEvent.consume();
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
            studentsMoved++;
            if (studentsMoved == studentsToMove) {
                studentsDraggable = false;
                firePropertyChange("MoveStudents", null, action.substring(0, action.length() - 1));
                action = null;
                studentsMoved = 0;
            }
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        };
        private final EventHandler<DragEvent> dragOverIsland = dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            dragEvent.consume();
        };
        private final EventHandler<DragEvent> dropOnIsland = dragEvent -> {
            AnchorPane island = (dragEvent.getTarget() instanceof AnchorPane) ? (AnchorPane) dragEvent.getTarget() :
                    (AnchorPane) ((Rectangle) dragEvent.getTarget()).getParent();
            String islandId = island.getId().substring("Island".length());
            if (studentsDraggable) {
                playersSchools.get(clientNickname).getSecond().removeFromEntrance(lastStudentDragged, true);
                action.append(dragEvent.getDragboard().getString()).append(" ToIsland ").append(islandId).append(" ");
                studentsMoved++;
                if (studentsMoved == studentsToMove) {
                    studentsDraggable = false;
                    firePropertyChange("MoveStudents", null, action.substring(0, action.length() - 1));
                    action = null;
                    studentsMoved = 0;
                }
            } else if (motherNatureDraggable) {
                int movement = (Integer.parseInt(islandId) + modelView.getField().getIslandSize()
                        - modelView.getField().getMotherNaturePosition()) % modelView.getField().getIslandSize();
                firePropertyChange("MoveMotherNature", null, Integer.toString(movement));
                motherNatureDraggable = false;
            }
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        };
        private final EventHandler<MouseEvent> dragStartMotherNature = mouseEvent -> {
            if (motherNatureDraggable) {
                Rectangle motherNature = (Rectangle) mouseEvent.getTarget();
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
            //playersBox.setOnDragExited(dragLeaveFromPlayersBox);
            islandsMap.setOnDragOver(dragOverIslands);
            islands.setDragAndDropHandlers(dragOverIsland, dropOnIsland, dragStartMotherNature);
            SchoolBox clientSchool = playersSchools.get(clientNickname).getSecond();
            clientSchool.setDragStartStudentHandler(studentDragStart);
            clientSchool.registerDiningRoomDragAndDrop(dragOverDiningRoom, dropOnDiningRoom);
            Button accordionButton = (Button) ((AnchorPane) playersBox.getChildren().get(0)).getChildren().get(0);
            accordionButton.setOnDragOver(dragOverAccordionButton);
        }
    }
}
