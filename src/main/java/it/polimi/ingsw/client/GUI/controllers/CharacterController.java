package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.client.GUI.items.IslandImage;
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

    public void show(int characterId, AnchorPane mainSceneRoot) {
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
        }
        String description = JsonUtils.getCharacterDescription(characterId).getFirst();
        characterDescription.setMaxWidth(characterDescription.getWidth());
        characterDescription.setWrapText(true);
        characterDescription.setText(description);
        characterTitle.setText("Character " + characterId);
        /*TranslateTransition transition = new TranslateTransition(Duration.millis(1000), root);
        transition.setByX(-translation);
        transition.play();*/
        root.setTranslateX(0);
        setOptions();
        root.setOpacity(1);
        characterPlayable = false;
        playButton.setOpacity(0.5);
    }

    public void init(AnchorPane mainSceneRoot, GameMainSceneController gameMainSceneController) {
        this.gameMainSceneController = gameMainSceneController;
        root.setOpacity(0);
        root.setLayoutX((mainSceneRoot.getWidth() - root.getWidth()) / 2);
        root.setLayoutY((mainSceneRoot.getHeight() - root.getHeight()) / 2);
        System.out.println(root.getLayoutX() + " " + root.getLayoutY() + " " + root.getWidth() + " " + root.getHeight());
        System.out.println(root.getLayoutX() + " " + root.getLayoutY() + " " + root.getPrefWidth() + " " + root.getPrefHeight());
        translation = mainSceneRoot.getWidth();
        root.setTranslateX(translation);
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    @FXML
    void closeCharacterView(ActionEvent event) {
        root.setOpacity(0);
        root.setTranslateX(translation);
        inputManager.reset();
    }

    @FXML
    void playCharacter(ActionEvent event) {
        if (!characterPlayable) return;
        //TODO: control parameters for choice boxes
        StringBuilder action = new StringBuilder();
        action.append(characterId);
        if (inputManager.actionsNeeded.contains(CharacterInputManager.ActionType.SELECT_REALM)) {
            RealmType selectedRealm = realmSelection.getSelectionModel().getSelectedItem();
            if (selectedRealm == null) {
                onError(null);
                return;
            }
            action.append(" ").append(selectedRealm.getAbbreviation());
            inputManager.actionsNeeded.remove(CharacterInputManager.ActionType.SELECT_REALM);
        }
        if (inputManager.actionsNeeded.contains(CharacterInputManager.ActionType.SELECT_NUMBER)) {
            Integer selectedNumber = numStudentsSelection.getSelectionModel().getSelectedItem();
            if (selectedNumber == null) {
                onError(null);
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
            onError(null);
        }
    }

    @Override
    public void onError(ErrorMessageType error) {
        errorLabel.setText("You haven't selected all the options");
    }

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

    private void makePlayCharacterButtonClickable() {
        characterPlayable = true;
        playButton.setOpacity(1);
    }

    private VBox getIslandSelectionButton(String labelText) {
        Label label = new Label(labelText);
        label.setWrapText(true);
        /*TextField textField = new TextField();
        textField.setMinWidth(characterOptions.getWidth() / 2);*/
        Button startSelectIsland = new Button("Select Island");
        startSelectIsland.getStyleClass().add("character-buttons");
        startSelectIsland.setOnAction(actionEvent -> inputManager.islandSelection(label));
        return new VBox(label, startSelectIsland);
    }

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

    private Pair<VBox, ChoiceBox<RealmType>> getSelectRealmChoiceBox(String labelText) {
        Label label = new Label(labelText);
        label.setWrapText(true);
        ChoiceBox<RealmType> choiceBox = new ChoiceBox<>();
        for (RealmType realm: RealmType.values()) {
            choiceBox.getItems().add(realm);
        }
        //Button startSelectStudents = new Button("Select Realm");
        return new Pair<>(new VBox(label, choiceBox), choiceBox);
    }

    private class CharacterInputManager {
        enum ActionType {
            SELECT_STUDENTS_FROM_CHARACTER,
            SELECT_STUDENTS_FROM_ENTRANCE,
            SELECT_STUDENTS_FROM_DINING_ROOM,
            SELECT_ISLAND,
            SELECT_REALM,
            SELECT_NUMBER
        }

        class SelectionEvent extends Event {
            public static final EventType<SelectionEvent> ANY = new EventType<>("finishEvent");

            public SelectionEvent(EventType<? extends Event> eventType) {
                super(eventType);
            }
        }
        private final Map<ActionType, List<String>> actions = new HashMap<>();
        private final List<ActionType> actionsNeeded = new ArrayList<>();
        private final List<ActionType> actionsOrder = new ArrayList<>();

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

        private void registerHandlers(List<Node> students, EventHandler<MouseEvent> studentSelection) {
            students.forEach(s -> s.addEventHandler(MouseEvent.MOUSE_CLICKED, studentSelection));
            EventHandler<SelectionEvent> onFinishHandler = selectionEvent -> {
                StudentImage targetStudent = (StudentImage) selectionEvent.getTarget();
                targetStudent.removeEventHandler(MouseEvent.MOUSE_CLICKED, studentSelection);
            };
            students.forEach(s -> s.addEventHandler(SelectionEvent.ANY, onFinishHandler));
        }

        private void islandSelection(Label label) {
            root.setTranslateX(translation);
            gameMainSceneController.moveAccordionDown();
            List<IslandImage> islands = gameMainSceneController.getIslands().getIslandImages();
            islands.forEach(i -> i.getIslandPane().getStyleClass().add("selectable-item"));
            EventHandler<MouseEvent> islandSelection = mouseEvent -> {
                AnchorPane anchorPane = (AnchorPane) mouseEvent.getTarget();
                String islandId = anchorPane.getId().substring("Island".length());
                root.setTranslateX(0);
                islands.forEach(i -> i.getIslandPane().getStyleClass().remove("selectable-item"));
                actions.put(ActionType.SELECT_ISLAND, List.of(islandId));
                actionsNeeded.remove(ActionType.SELECT_ISLAND);
                label.setText("You have selected island " + islandId + ".\nClick here if you want to change.");
            };
            islands.forEach(i -> i.getIslandPane().addEventHandler(MouseEvent.MOUSE_CLICKED, islandSelection));
        }

        private void reset() {
            actions.clear();
            actionsNeeded.clear();
            actionsOrder.clear();
        }

        private void start() {
            actionsOrder.addAll(actionsNeeded);
            actionsOrder.remove(ActionType.SELECT_REALM);
            actionsOrder.remove(ActionType.SELECT_NUMBER);
        }
    }
}
