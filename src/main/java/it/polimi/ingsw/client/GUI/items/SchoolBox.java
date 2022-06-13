package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class SchoolBox {
    private final AnchorPane school;
    private final ImageView lastAssistantPlayed;
    private final PlayerView playerView;
    private final double dimStudentsRadius;
    private final AnchorPane entrance;
    private final AnchorPane diningRoom;
    private final AnchorPane container;
    private EventHandler<MouseEvent> dragStartStudentHandler;
    private static final List<RealmType> diningRoomOrder = List.of(RealmType.GREEN_FROGS, RealmType.RED_DRAGONS,
            RealmType.YELLOW_GNOMES, RealmType.PINK_FAIRES, RealmType.BLUE_UNICORNS);

    public SchoolBox(String player, AnchorPane container, PlayerView playerView) {
        this.container = container;
        school = (AnchorPane) container.getChildren().get(0);
        lastAssistantPlayed = (ImageView) container.getChildren().get(1);
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.wizardImages.get(playerView.getPlayerWizard()))));
        lastAssistantPlayed.setImage(image);
        this.playerView = playerView;
        dimStudentsRadius = school.getHeight() * 75 / 1454;
        entrance = (AnchorPane) school.getChildren().get(1);
        diningRoom = (AnchorPane) school.getChildren().get(2);
        initializeSchool();
    }

    public void setAssistantImage(Image image) {
        lastAssistantPlayed.setImage(image);
        lastAssistantPlayed.setPreserveRatio(true);
    }

    public void initializeSchool() {
        initializeStudents(entrance);
        initializeStudents(diningRoom);
        RealmType[] entranceStudents = RealmType.getRealmsFromIntegerRepresentation(playerView.getSchoolStudents().getFirst());
        for (int i = 0; i < entranceStudents.length; i++) {
            ((StudentImage) entrance.getChildren().get(i)).setStudent(entranceStudents[i]);
        }
    }

    private void initializeStudents(AnchorPane container) {
        List<Node> students = container.getChildren().stream().toList();
        for (int i = 0; i < students.size(); i++) {
            Circle circle = (Circle) students.get(i);
            container.getChildren().set(i, new StudentImage(circle));
        }
    }

    public synchronized void insertStudentEntrance(RealmType student) {
        List<Node> students = entrance.getChildren().stream().toList();
        Integer emptyIndex = findFirstEmpty(students);
        if (emptyIndex == null) return;
        StudentImage studentImage = (StudentImage) students.get(emptyIndex);
        studentImage.setStudent(student);
        studentImage.setOnDragDetected(dragStartStudentHandler);
    }

    private Integer findFirstEmpty(List<Node> students) {
        Optional<Node> studentEmpty = students.stream().filter(s -> s.getOpacity() == 0).findFirst();
        if (studentEmpty.isEmpty()) return null;
        return students.indexOf(studentEmpty.get());
    }

    public synchronized void removeFromEntrance(RealmType studentType, boolean isClientSchool) {
        Optional<Node> student = entrance.getChildren().stream()
                .filter(s -> ((StudentImage) s).getStudentType() == studentType)
                .findFirst();
        student.ifPresent(s -> {
            ((StudentImage) s).reset();
            if (isClientSchool) s.removeEventHandler(MouseEvent.DRAG_DETECTED, dragStartStudentHandler);
        });
    }

    public void setDragStartStudentHandler(EventHandler<MouseEvent> handler) {
        this.dragStartStudentHandler = handler;
        entrance.getChildren().stream()
                .filter(s -> s.getOpacity() == 1)
                .forEach(s -> s.setOnDragDetected(handler));
    }

    public void registerDiningRoomDragAndDrop(EventHandler<DragEvent> dragOverHandler, EventHandler<DragEvent> dropHandler) {
        diningRoom.setOnDragOver(dragOverHandler);
        diningRoom.setOnDragDropped(dropHandler);
    }

    public synchronized void insertInDiningRoom(RealmType student) {
        int studentIdx = diningRoomOrder.indexOf(student);
        List<Node> studentsLine = diningRoom.getChildren().subList(studentIdx * 10, (studentIdx + 1) * 10);
        int i = 0;
        //maybe can be done with streams, but I'm not sure that they preserve order
        while (studentsLine.get(i).getOpacity() == 1) i++;
        ((StudentImage) studentsLine.get(i)).setStudent(student);
    }

    public void moveStudentFromEntranceToDiningRoom(RealmType studentType) {
        Optional<Node> studentNode = entrance.getChildren().stream()
                .filter(s -> ((StudentImage) s).getStudentType() == studentType)
                .findFirst();
        studentNode.ifPresent(student -> {
            StudentImage studentImage = (StudentImage) student;
            StudentImage fakeStudent = new StudentImage(studentImage);
            fakeStudent.setStudent(studentType);
            entrance.getChildren().remove(studentImage);
            entrance.getChildren().add(fakeStudent);
            Pair<Double, Double> firstFreeDiningRoom = findFirstFreePosition(studentType);
            double xTranslation = diningRoom.getLayoutX() + firstFreeDiningRoom.getFirst() - studentImage.getLayoutX();
            double yTranslation = diningRoom.getLayoutY() + firstFreeDiningRoom.getSecond() - studentImage.getLayoutY();
            TranslateTransition transition = new TranslateTransition(Duration.millis(2000), fakeStudent);
            transition.setByX(xTranslation);
            transition.setByY(yTranslation);
            transition.setDelay(Duration.millis(500)); //let the button event finish
            transition.play();
            transition.setOnFinished(actionEvent -> {
                entrance.getChildren().remove(fakeStudent);
                insertInDiningRoom(studentType);
            });
            
        });
    }
    
    private Pair<Double, Double> findFirstFreePosition(RealmType student) {
        int studentIdx = diningRoomOrder.indexOf(student);
        List<Node> studentsLine = diningRoom.getChildren().subList(studentIdx * 10, (studentIdx + 1) * 10);
        int i = 0;
        while (studentsLine.get(i).getOpacity() == 1) i++;
        return new Pair<>(studentsLine.get(i).getLayoutX(), studentsLine.get(i).getLayoutY());
    }

    public AnchorPane getEntrancePane() {
        return entrance;
    }

    public AnchorPane getDiningRoomPane() {
        return diningRoom;
    }

    public Pair<Double, Double> getEntranceStudentLayout(RealmType student) throws NoSuchElementException {
        Optional<Node> studentPresent = entrance.getChildren().stream()
                .filter(s -> s.getOpacity() == 1 &&  ((StudentImage) s).getStudentType() == student).findFirst();
        if (studentPresent.isEmpty()) throw new NoSuchElementException();
        double xPosition = container.getLayoutX() + school.getLayoutX() + entrance.getLayoutX() + studentPresent.get().getLayoutX();
        double yPosition = container.getLayoutY() + school.getLayoutY() + entrance.getLayoutY() + studentPresent.get().getLayoutY();
        return new Pair<>(xPosition, yPosition);
    }

    public double getDimStudentsRadius() {
        return dimStudentsRadius;
    }
}