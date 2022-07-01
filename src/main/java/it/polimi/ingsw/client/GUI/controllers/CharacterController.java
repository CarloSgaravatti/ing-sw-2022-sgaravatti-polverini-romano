package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.items.IslandSubScene;
import it.polimi.ingsw.client.GUI.items.StudentImage;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.messages.ErrorMessageType;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.JsonUtils;
import it.polimi.ingsw.utils.Pair;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * CharacterController controls the character sub scene that appears in the main scene when a player selects a character
 * image.
 *
 * @see it.polimi.ingsw.client.GUI.controllers.FXMLController
 */
public class CharacterController extends FXMLController {
    @FXML private Label characterTitle;
    @FXML private HBox studentsBox;
    @FXML private Label characterDescription;
    @FXML private ImageView characterImage;
    @FXML private VBox characterOptions;
    @FXML private AnchorPane root;
    @FXML private Label errorLabel;
    @FXML private Button playButton;
    private GameMainSceneController gameMainSceneController;
    private int characterId;
    private double translation;
    private final CharacterInputManager inputManager = new CharacterInputManager();
    private ChoiceBox<RealmType> realmSelection;
    private ChoiceBox<Integer> numStudentsSelection;
    private boolean characterPlayable;

    /**
     * Shows the character dialog in the center of the main game scene
     *
     * @param characterId the id of the character that will be shown
     */
    public void show(int characterId) {
        characterImage.setImage(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/characters/character" + characterId + ".jpg"))));
        this.characterId = characterId;
        ModelView modelView = gameMainSceneController.getModelView();
        studentsBox.getChildren().clear();
        if (modelView.getField().getExpertField().isCharacterWithStudents(characterId)) {
            RealmType[] students = RealmType
                    .getRealmsFromIntegerRepresentation(modelView.getField().getExpertField().characterStudents(characterId));
            for (RealmType student: students) {
                StudentImage studentImage = new StudentImage(studentsBox.getHeight() / 2, student);
                studentsBox.getChildren().add(studentImage);
            }
        } else if (modelView.getField().getExpertField().areNoEntryTilesPresents() &&
                modelView.getField().getExpertField().getNumNoEntryTilesOnCharacter().getFirst() == characterId) {
            int numNoEntryTilesOnCharacter = modelView.getField().getExpertField().getNumNoEntryTilesOnCharacter().getSecond();
            for (int i = 0; i < numNoEntryTilesOnCharacter; i++) {
                ImageView imageNoEntryTiles = new ImageView(new Image(Objects.requireNonNull(getClass()
                        .getResourceAsStream("/images/deny_island_icon.png"))));
                imageNoEntryTiles.setFitHeight(studentsBox.getHeight());
                imageNoEntryTiles.setPreserveRatio(true);
                studentsBox.getChildren().add(imageNoEntryTiles);
            }
        }
        String description = JsonUtils.getCharacterDescription(characterId).getFirst();
        characterDescription.setMaxWidth(characterDescription.getWidth());
        characterDescription.setWrapText(true);
        characterDescription.setText(description);
        characterTitle.setText("Character " + characterId);
        root.setTranslateX(0);
        characterPlayable = false;
        root.setOpacity(1);
        playButton.setOpacity(0.5);
        setOptions();
    }

    /**
     * Initialize the character dialog by adding it to the specified root of the main scene in a position that cannot be
     * seen by the user and by binding this controller with the controller of the main scene
     *
     * @param mainSceneRoot the root anchor pane of the main scene
     * @param gameMainSceneController the controller of the main scene
     */
    public void init(AnchorPane mainSceneRoot, GameMainSceneController gameMainSceneController) {
        this.gameMainSceneController = gameMainSceneController;
        root.setOpacity(0);
        root.setLayoutX((mainSceneRoot.getWidth() - root.getWidth()) / 2);
        root.setLayoutY((mainSceneRoot.getHeight() - root.getHeight()) / 2);
        translation = mainSceneRoot.getWidth();
        root.setTranslateX(translation);
    }

    /**
     * Makes the character not visible in the main scene after the close button is pressed
     *
     * @param event the event fired when pressing the close button
     */
    @FXML
    void closeCharacterView(ActionEvent event) {
        inputManager.resetEvents();
        root.setOpacity(0);
        root.setTranslateX(translation);
        inputManager.reset();
    }

    /**
     * If all inputs that the character needs to have are inserted, the character is played and the action string that
     * will be passed to the core part of the client is constructed.
     *
     * @param event the event fired when pressing the play character button
     */
    @FXML
    void playCharacter(ActionEvent event) {
        if (!characterPlayable) return;
        StringBuilder action = new StringBuilder();
        action.append(characterId);
        if (inputManager.actionsNeeded.contains(CharacterInputManager.ActionType.SELECT_REALM)) {
            RealmType selectedRealm = realmSelection.getSelectionModel().getSelectedItem();
            if (selectedRealm == null) {
                onError(null, null);
                return;
            }
            action.append(" ").append(selectedRealm.getAbbreviation());
            inputManager.actionsNeeded.remove(CharacterInputManager.ActionType.SELECT_REALM);
        }
        if (inputManager.actionsNeeded.contains(CharacterInputManager.ActionType.SELECT_NUMBER)) {
            Integer selectedNumber = numStudentsSelection.getSelectionModel().getSelectedItem();
            if (selectedNumber == null) {
                onError(null, null);
                return;
            }
            action.append(" ").append(selectedNumber);
            inputManager.actionsNeeded.remove(CharacterInputManager.ActionType.SELECT_NUMBER);
        }
        for (CharacterInputManager.ActionType actionType: inputManager.actionsOrder) {
            List<String> args = inputManager.actions.get(actionType);
            args.forEach(arg -> action.append(" ").append(arg));
        }
        if (inputManager.actionsNeeded.isEmpty()) {
            gameMainSceneController.firePropertyChange("PlayCharacter", null, action.toString());
            System.out.println("PlayCharacter " + action);
            root.setOpacity(0);
            root.setTranslateX(translation);
            inputManager.reset();
        } else {
            onError(null, null);
        }
    }

    /**
     * Display an error i n the dialog that informs that the user have to do something before playing the character
     *
     * @param error the type of error
     * @param errorInfo the description of the error
     */
    @Override
    public void onError(ErrorMessageType error, String errorInfo) {
        errorLabel.setText("You haven't selected all the options");
    }

    /**
     * Sets the options that need to be selected by the user in order to play the character. The user will see them
     * on the right side of the character image
     */
    private void setOptions() {
        characterOptions.getChildren().clear();
        ModelView modelView = gameMainSceneController.getModelView();
        if (modelView.getField().getExpertField().getCharacterPrice(characterId) >
                modelView.getPlayers().get(gameMainSceneController.getClientNickname()).getPlayerCoins()) {
            Label label = new Label("You don't have enough coins to play this character");
            label.setWrapText(true);
            label.setTextFill(Color.RED);
            characterOptions.getChildren().add(label);
            return;
        }
        switch (characterId) {
            case 1 -> {
                characterOptions.getChildren().add(getSelectCharacterStudentsButton(1, null));
                characterOptions.getChildren().add(getIslandSelectionButton("Insert the student destination (island id)"));
                inputManager.actionsNeeded.addAll(List.of(CharacterInputManager.ActionType.SELECT_STUDENTS_FROM_CHARACTER,
                        CharacterInputManager.ActionType.SELECT_ISLAND));
            }
            case 3 -> {
                characterOptions.getChildren().add(getIslandSelectionButton("Select an island to update"));
                inputManager.actionsNeeded.add(CharacterInputManager.ActionType.SELECT_ISLAND);
            }
            case 5 -> {
                characterOptions.getChildren().add(getIslandSelectionButton("Select an island to put an entry tile"));
                inputManager.actionsNeeded.add(CharacterInputManager.ActionType.SELECT_ISLAND);
            }
            case 7 -> {
                Pair<VBox, ChoiceBox<Integer>> pair = getStudentsNumberSelectionButton(3);
                this.numStudentsSelection = pair.getSecond();
                characterOptions.getChildren().add(pair.getFirst());
                characterOptions.getChildren().add(getSelectCharacterStudentsButton(3, pair.getSecond()));
                characterOptions.getChildren().add(getSelectEntranceStudentsButton(3, pair.getSecond()));
                inputManager.actionsNeeded.addAll(List.of(CharacterInputManager.ActionType.SELECT_STUDENTS_FROM_CHARACTER,
                        CharacterInputManager.ActionType.SELECT_STUDENTS_FROM_ENTRANCE, CharacterInputManager.ActionType.SELECT_NUMBER));
            }
            case 9 -> {
                Pair<VBox, ChoiceBox<RealmType>> pair = getSelectRealmChoiceBox("Select a realm that will not be counted in the influence");
                characterOptions.getChildren().add(pair.getFirst());
                this.realmSelection = pair.getSecond();
                inputManager.actionsNeeded.add(CharacterInputManager.ActionType.SELECT_REALM);
                makePlayCharacterButtonClickable();
            }
            case 10 -> {
                Pair<VBox, ChoiceBox<Integer>> pair = getStudentsNumberSelectionButton(2);
                this.numStudentsSelection = pair.getSecond();
                characterOptions.getChildren().add(pair.getFirst());
                characterOptions.getChildren().add(getSelectEntranceStudentsButton(2, pair.getSecond()));
                characterOptions.getChildren().add(getSelectDiningRoomStudentsButton(3, pair.getSecond()));
                inputManager.actionsNeeded.addAll(List.of(CharacterInputManager.ActionType.SELECT_STUDENTS_FROM_ENTRANCE,
                        CharacterInputManager.ActionType.SELECT_STUDENTS_FROM_DINING_ROOM, CharacterInputManager.ActionType.SELECT_NUMBER));
            }
            case 11 -> {
                characterOptions.getChildren().add(getSelectCharacterStudentsButton(1, null));
                inputManager.actionsNeeded.add(CharacterInputManager.ActionType.SELECT_STUDENTS_FROM_CHARACTER);
            }
            case 12 -> {
                Pair<VBox, ChoiceBox<RealmType>> pair = getSelectRealmChoiceBox("Select a realm");
                characterOptions.getChildren().add(pair.getFirst());
                this.realmSelection = pair.getSecond();
                inputManager.actionsNeeded.add(CharacterInputManager.ActionType.SELECT_REALM);
                makePlayCharacterButtonClickable();
            }
            case 2, 4, 6, 8 -> makePlayCharacterButtonClickable();
        }
        inputManager.start();
    }

    /**
     * Make the play character button clickable after the user have selected all the options for playing the character
     */
    private void makePlayCharacterButtonClickable() {
        characterPlayable = true;
        playButton.setOpacity(1);
    }

    /**
     * Returns a VBox that contains a form for selecting an island that will be added to the character options
     *
     * @param labelText the text of the label for the form
     * @return  a VBox that contains a form for selecting an island that will be added to the character options
     */
    private VBox getIslandSelectionButton(String labelText) {
        Label label = new Label(labelText);
        label.setWrapText(true);
        Button startSelectIsland = new Button("Select Island");
        startSelectIsland.getStyleClass().add("character-buttons");
        startSelectIsland.setOnAction(actionEvent -> inputManager.islandSelection(label));
        return new VBox(label, startSelectIsland);
    }

    /**
     * Returns a form used to choose the number of students that will be swapped in character 7 or 10. The first element
     * of the returned pair is the form (choice box + label), the second is the specific choice box that the user will
     * have to fill
     *
     * @param numStudents the maximum number of students that the choice box will permit to select
     * @return the form used to choose the number of students that will be swapped in character 7 or 10
     */
    private Pair<VBox, ChoiceBox<Integer>> getStudentsNumberSelectionButton(int numStudents) {
        Label label = new Label("Select the number of students you want to move");
        label.setWrapText(true);
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
        for (int i = 1; i <= numStudents; i++) {
            choiceBox.getItems().add(i);
        }
        choiceBox.setMinWidth(characterOptions.getWidth() / 2);
        return new Pair<>(new VBox(label, choiceBox), choiceBox);
    }

    /**
     * Returns a VBox that will contain a label and a button that will permit to chose students from the character
     *
     * @param maxStudents the maximum number of students that can be selected from the character
     * @param choiceBox the choice box from which the number of students that can be selected is taken, the choice box is
     *                  filled by the user before doing the character students selection
     * @return a VBox that will contain a label and a button that will permit to chose students from the character
     */
    private VBox getSelectCharacterStudentsButton(int maxStudents, ChoiceBox<Integer> choiceBox) {
        String labelText = "Click here to select " + ((maxStudents == 1) ? "a student from this character" : "students from" +
                "this character");
        Label label = new Label(labelText);
        label.setWrapText(true);
        Button startSelectStudents = new Button("Select Character Students");
        startSelectStudents.getStyleClass().add("character-buttons");
        VBox vBox = new VBox(label, startSelectStudents);
        startSelectStudents.setOnAction(actionEvent -> {
            if (maxStudents == 1) inputManager.selectionFromCharacter(maxStudents, label);
            else {
                Integer numStudents = choiceBox.getSelectionModel().getSelectedItem();
                if (numStudents == null) {
                    vBox.getChildren().add(new Label("You have to insert a number before"));
                } else {
                    if (vBox.getChildren().size() == 3) vBox.getChildren().remove(2);
                    inputManager.selectionFromCharacter(numStudents, label);
                }
            }
        });
        return vBox;
    }

    /**
     * Returns a VBox that will contain a label and a button that will permit to chose students from the entrance of the
     * user's school.
     *
     * @param maxStudents the maximum number of students that can be selected from the entrance
     * @param choiceBox the choice box from which the number of students that can be selected is taken, the choice box is
     *                  filled by the user before doing the character students selection
     * @return a VBox that will contain a label and a button that will permit to chose students from the entrance of the
     *      user's school.
     */
    private VBox getSelectEntranceStudentsButton(int maxStudents, ChoiceBox<Integer> choiceBox) {
        String labelText = "Click here to select students from your entrance";
        Label label = new Label(labelText);
        label.setWrapText(true);
        Button startSelectStudents = new Button("Select Entrance Students");
        startSelectStudents.getStyleClass().add("character-buttons");
        VBox vBox = new VBox(label, startSelectStudents);
        startSelectStudents.setOnAction(actionEvent -> {
            Integer numStudents = choiceBox.getSelectionModel().getSelectedItem();
            if (numStudents == null) {
                vBox.getChildren().add(new Label("You have to insert a number before"));
            } else {
                if (vBox.getChildren().size() == 3) vBox.getChildren().remove(2);
                inputManager.selectionFromEntrance(numStudents, label);
            }
        });
        return vBox;
    }

    /**
     * Returns a VBox that will contain a label and a button that will permit to chose students from the dining room of the
     * user's school.
     *
     * @param maxStudents the maximum number of students that can be selected from the dining room
     * @param choiceBox the choice box from which the number of students that can be selected is taken, the choice box is
     *                  filled by the user before doing the character students selection
     * @return a VBox that will contain a label and a button that will permit to chose students from the dining room of the
     *      user's school.
     */
    private VBox getSelectDiningRoomStudentsButton(int maxStudents, ChoiceBox<Integer> choiceBox) {
        String labelText = "Click here to select students from your dining room";
        Label label = new Label(labelText);
        label.setWrapText(true);
        Button startSelectStudents = new Button("Select Dining Room Students");
        startSelectStudents.getStyleClass().add("character-buttons");
        VBox vBox = new VBox(label, startSelectStudents);
        startSelectStudents.setOnAction(actionEvent -> {
            Integer numStudents = choiceBox.getSelectionModel().getSelectedItem();
            if (numStudents == null) {
                vBox.getChildren().add(new Label("You have to insert a number before"));
            } else {
                if (vBox.getChildren().size() == 3) vBox.getChildren().remove(2);
                inputManager.selectionFromDiningRoom(numStudents, label);
            }
        });
        return vBox;
    }

    /**
     * Returns a form used to choose a realm for characters that needs this type of choice. These characters are characters
     * 9 and 12.
     *
     * @param labelText the label that describe for what type of action the selection will be used
     * @return the form used to choose the number of students that will be swapped in character 7 or 10
     */
    private Pair<VBox, ChoiceBox<RealmType>> getSelectRealmChoiceBox(String labelText) {
        Label label = new Label(labelText);
        label.setWrapText(true);
        ChoiceBox<RealmType> choiceBox = new ChoiceBox<>();
        for (RealmType realm: RealmType.values()) {
            choiceBox.getItems().add(realm);
        }
        return new Pair<>(new VBox(label, choiceBox), choiceBox);
    }

    /**
     * CharacterInputManager is an inner class of the CharacterController that controls the input of the character
     */
    private class CharacterInputManager {
        enum ActionType {
            SELECT_STUDENTS_FROM_CHARACTER,
            SELECT_STUDENTS_FROM_ENTRANCE,
            SELECT_STUDENTS_FROM_DINING_ROOM,
            SELECT_ISLAND,
            SELECT_REALM,
            SELECT_NUMBER
        }

        /**
         * SelectionEvent is an event that is fired when a character option is completely inserted
         */
        class SelectionEvent extends Event {
            public static final EventType<SelectionEvent> ANY = new EventType<>("finishEvent");

            public SelectionEvent(EventType<? extends Event> eventType) {
                super(eventType);
            }
        }
        private final Map<ActionType, List<String>> actions = new HashMap<>();
        private final List<ActionType> actionsNeeded = new ArrayList<>();
        private final List<ActionType> actionsOrder = new ArrayList<>();

        /**
         * Callback that will be called when the user selects the button for selecting students from the entrance and that
         * will handle the selection of the students. At the end of the selection the character dialog is restored at
         * the center of the scene
         *
         * @param numStudentsToSelect the number of students to select
         * @param details the label on which details about the selection will be inserted
         */
        private void selectionFromEntrance(int numStudentsToSelect, Label details) {
            root.setTranslateX(translation);
            gameMainSceneController.moveAccordionUp();
            AnchorPane clientSchoolEntrance = gameMainSceneController.getSchoolOfClient().getEntrancePane();
            List<Node> students = clientSchoolEntrance.getChildren().stream().filter(c -> c.getOpacity() == 1).toList();
            students.forEach(s -> s.getStyleClass().add("selectable-item"));
            List<Node> selectedStudents = new ArrayList<>();
            EventHandler<MouseEvent> studentSelection = mouseEvent -> {
                StudentImage targetStudent = (StudentImage) mouseEvent.getTarget();
                if (targetStudent.getStyleClass().contains("selected-student")) {
                    targetStudent.getStyleClass().remove("selected-student");
                    selectedStudents.remove(targetStudent);
                } else {
                    targetStudent.getStyleClass().add("selected-student");
                    selectedStudents.add(targetStudent);
                }
                if (selectedStudents.size() == numStudentsToSelect) {
                    List<RealmType> selectedRealms = selectedStudents.stream().map(s -> ((StudentImage) s).getStudentType()).toList();
                    root.setTranslateX(0);
                    actions.put(ActionType.SELECT_STUDENTS_FROM_ENTRANCE, selectedRealms.stream().map(RealmType::getAbbreviation).toList());
                    actionsNeeded.remove(ActionType.SELECT_STUDENTS_FROM_ENTRANCE);
                    details.setText("You have selected these students from your entrance:" +
                            Arrays.toString(selectedRealms.toArray(new RealmType[0])) + ".\nClick here if you want to change.");
                    fireEndSelectionEvent(students);
                }
            };
            registerHandlers(students, studentSelection);
        }

        /**
         * Callback that will be called when the user selects the button for selecting students from the dining room and that
         * will handle the selection of the students. At the end of the selection the character dialog is restored at
         * the center of the scene
         *
         * @param numStudentsToSelect the number of students to select
         * @param details the label on which details about the selection will be inserted
         */
        private void selectionFromDiningRoom(int numStudentsToSelect, Label details) {
            root.setTranslateX(translation);
            gameMainSceneController.moveAccordionUp();
            AnchorPane clientSchoolDiningRoom = gameMainSceneController.getSchoolOfClient().getDiningRoomPane();
            List<Node> students = clientSchoolDiningRoom.getChildren().stream().filter(c -> c.getOpacity() == 1).toList();
            students.forEach(s -> s.getStyleClass().add("selectable-item"));
            List<Node> selectedStudents = new ArrayList<>();
            EventHandler<MouseEvent> studentSelection = mouseEvent -> {
                StudentImage targetStudent = (StudentImage) mouseEvent.getTarget();
                if (targetStudent.getStyleClass().contains("selected-student")) {
                    targetStudent.getStyleClass().remove("selected-student");
                    selectedStudents.remove(targetStudent);
                } else {
                    targetStudent.getStyleClass().add("selected-student");
                    selectedStudents.add(targetStudent);
                }
                if (selectedStudents.size() == numStudentsToSelect) {
                    List<RealmType> selectedRealms = selectedStudents.stream().map(s -> ((StudentImage) s).getStudentType()).toList();
                    root.setTranslateX(0);
                    actions.put(ActionType.SELECT_STUDENTS_FROM_DINING_ROOM, selectedRealms.stream().map(RealmType::getAbbreviation).toList());
                    actionsNeeded.remove(ActionType.SELECT_STUDENTS_FROM_DINING_ROOM);
                    details.setText("You have selected these students from your dining room:" +
                            Arrays.toString(selectedRealms.toArray(new RealmType[0])) + ".\nClick here if you want to change.");
                    fireEndSelectionEvent(students);
                }
            };
            registerHandlers(students, studentSelection);
        }

        /**
         * Callback that will be called when the user selects the button for selecting students from the character and that
         * will handle the selection of the students.
         *
         * @param numStudentsToSelect the number of students to select
         * @param details the label on which details about the selection will be inserted
         */
        private void selectionFromCharacter(int numStudentsToSelect, Label details) {
            List<Node> students = studentsBox.getChildren();
            List<Node> selectedStudents = new ArrayList<>();
            EventHandler<MouseEvent> studentSelection = mouseEvent -> {
                StudentImage targetStudent = (StudentImage) mouseEvent.getTarget();
                if (targetStudent.getStyleClass().contains("selected-student")) {
                    targetStudent.getStyleClass().remove("selected-student");
                    selectedStudents.remove(targetStudent);
                } else {
                    targetStudent.getStyleClass().add("selected-student");
                    selectedStudents.add(targetStudent);
                }
                if (selectedStudents.size() == numStudentsToSelect) {
                    List<RealmType> selectedRealms = selectedStudents.stream().map(s -> ((StudentImage) s).getStudentType()).toList();
                    actions.put(ActionType.SELECT_STUDENTS_FROM_CHARACTER, selectedStudents.stream()
                            .map(s -> ((StudentImage) s).getStudentType().getAbbreviation()).toList());
                    actionsNeeded.remove(ActionType.SELECT_STUDENTS_FROM_CHARACTER);
                    details.setText("You have selected these students from this character:" +
                            Arrays.toString(selectedRealms.toArray(new RealmType[0])) + ".\nClick here if you want to change.");
                    fireEndSelectionEvent(students);
                }
            };
            registerHandlers(students, studentSelection);
        }

        /**
         * Fires SelectionEvent at the end of the selection of students. All handlers that handle the selection will
         * be removed by the specified students.
         *
         * @param students the nodes that represent the students that were previously selected.
         */
        private void fireEndSelectionEvent(List<Node> students) {
            students.forEach(s -> {
                s.getStyleClass().clear();
                SelectionEvent event = new SelectionEvent(SelectionEvent.ANY);
                Event.fireEvent(s, event);
            });
            if (actionsNeeded.isEmpty() || (actionsNeeded.size() == 1 && actionsNeeded.get(0) == ActionType.SELECT_NUMBER)) {
                makePlayCharacterButtonClickable();
            }
        }

        /**
         * Register the specified handler for a students of the specified students is selected. Also, a handler for when
         * all the needed students are selected is added to the students.
         *
         * @param students the students that can be selected for playing the character
         * @param studentSelection the handler of the selection
         */
        private void registerHandlers(List<Node> students, EventHandler<MouseEvent> studentSelection) {
            students.forEach(s -> s.addEventHandler(MouseEvent.MOUSE_CLICKED, studentSelection));
            EventHandler<SelectionEvent> onFinishHandler = selectionEvent -> {
                StudentImage targetStudent = (StudentImage) selectionEvent.getTarget();
                targetStudent.removeEventHandler(MouseEvent.MOUSE_CLICKED, studentSelection);
            };
            students.forEach(s -> s.addEventHandler(SelectionEvent.ANY, onFinishHandler));
        }

        /**
         * Callback that will be called when the user selects the button for selecting an island from the map and that
         * will handle the selection of the island. At the end of the selection the character dialog is restored at
         * the center of the scene.
         *
         * @param label the label on which details about the selection will be inserted
         */
        private void islandSelection(Label label) {
            root.setTranslateX(translation);
            gameMainSceneController.moveAccordionDown();
            List<IslandSubScene> islands = gameMainSceneController.getIslands().getIslands();
            islands.forEach(i -> i.getStyleClass().add("selectable-item"));
            EventHandler<MouseEvent> islandSelection = mouseEvent -> {
                IslandSubScene island = (IslandSubScene) mouseEvent.getTarget();
                String islandId = String.valueOf(island.getIslandId());
                root.setTranslateX(0);
                islands.forEach(i -> i.getStyleClass().remove("selectable-item"));
                actions.put(ActionType.SELECT_ISLAND, List.of(islandId));
                actionsNeeded.remove(ActionType.SELECT_ISLAND);
                if (actionsNeeded.isEmpty()) makePlayCharacterButtonClickable();
                label.setText("You have selected island " + islandId + ".\nClick here if you want to change.");
            };
            islands.forEach(i -> i.addEventHandler(MouseEvent.MOUSE_CLICKED, islandSelection));
        }

        /**
         * Reset the input manager when the character dialog is closed by removing the options that the user need to do
         */
        private void reset() {
            actions.clear();
            actionsNeeded.clear();
            actionsOrder.clear();
        }

        /**
         * Starts the input manager when the dialog is opened. All needed action s that require a selections are added
         * to the list of actions that the input manager will control
         */
        private void start() {
            actionsOrder.addAll(actionsNeeded);
            actionsOrder.remove(ActionType.SELECT_REALM);
            actionsOrder.remove(ActionType.SELECT_NUMBER);
        }

        /**
         * Reset the events after the character dialog is closed
         */
        private void resetEvents() {
            if (actionsOrder.contains(ActionType.SELECT_STUDENTS_FROM_ENTRANCE)) {
                AnchorPane entrancePane = gameMainSceneController.getSchoolOfClient().getEntrancePane();
                entrancePane.getChildren().forEach(student -> {
                    student.getStyleClass().clear();
                    SelectionEvent event = new SelectionEvent(SelectionEvent.ANY);
                    Event.fireEvent(student, event);
                });
            }
            if (actionsOrder.contains(ActionType.SELECT_STUDENTS_FROM_DINING_ROOM)) {
                AnchorPane diningRoomPane = gameMainSceneController.getSchoolOfClient().getDiningRoomPane();
                diningRoomPane.getChildren().forEach(student -> {
                    student.getStyleClass().clear();
                    SelectionEvent event = new SelectionEvent(SelectionEvent.ANY);
                    Event.fireEvent(student, event);
                });
            }
        }
    }
}
