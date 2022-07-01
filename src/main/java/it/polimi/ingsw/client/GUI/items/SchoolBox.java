package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * class used to initialize, modify and update the school box during the game with the drag and drop of students,addition of towers and professors
 *
 */
public class SchoolBox {
    private final AnchorPane school;
    private final ImageView lastAssistantPlayed;
    private final PlayerView playerView;
    private final double dimStudentsRadius;
    private final AnchorPane entrance;
    private final AnchorPane diningRoom;
    private final AnchorPane container;
    private final AnchorPane professorTable;
    private final AnchorPane towers;
    private EventHandler<MouseEvent> dragStartStudentHandler;
    private static final List<RealmType> diningRoomOrder = List.of(RealmType.GREEN_FROGS, RealmType.RED_DRAGONS,
            RealmType.YELLOW_GNOMES, RealmType.PINK_FAIRES, RealmType.BLUE_UNICORNS);

    /**
     * constructor of the representation of the school in the main scene by the AnchorPane, it depends on if the game
     * is expert or not.
     *
     * @param player name of the school's player
     * @param container the AnchorPane that will contain the school
     * @param modelView the model view of the client
     * @param isExpertGame boolean that says if the game is for expert or not
     */
    public SchoolBox(String player, AnchorPane container, ModelView modelView, boolean isExpertGame) {
        this.container = container;
        this.playerView = modelView.getPlayers().get(player);
        school = (AnchorPane) container.getChildren().get(0);
        lastAssistantPlayed = (ImageView) container.getChildren().get(1);
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.wizardImages.get(playerView.getPlayerWizard()))));
        lastAssistantPlayed.setImage(image);
        dimStudentsRadius = school.getHeight() * 75 / 1454;
        entrance = (AnchorPane) school.getChildren().get(1);
        diningRoom = (AnchorPane) school.getChildren().get(2);
        professorTable = (AnchorPane) school.getChildren().get(3);
        towers = (AnchorPane) school.getChildren().get(4);
        initializeSchool(modelView, player);
        VBox vBox = (VBox) container.getChildren().get(2);
        if (isExpertGame) {
            Image imageCoin = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/schools/coin.png")));
            ((Circle)vBox.getChildren().get(0)).setFill(new ImagePattern(imageCoin));
        } else container.getChildren().remove(vBox);
        towers.getChildren().forEach(i -> ((ImageView) i).setImage(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream(Constants.towerImages.get(playerView.getPlayerTower()))))));
    }

    /**
     * method setAssistantImage takes last assistant played and inserts the image in the scene
     *
     * @param image image of the assistant
     */
    public void setAssistantImage(Image image) {
        lastAssistantPlayed.setImage(image);
        lastAssistantPlayed.setPreserveRatio(true);
    }

    /**
     * method initializeSchool inserts students in the school when the game starts/restarts
     *
     * @param modelView the modelView of the client
     * @param nickname  the nickname of the player
     */
    public void initializeSchool(ModelView modelView, String nickname) {
        initializeStudents(entrance);
        initializeStudents(diningRoom);
        initializeProfessors();
        RealmType[] entranceStudents = RealmType.getRealmsFromIntegerRepresentation(playerView.getSchoolStudents().getFirst());
        for (int i = 0; i < entranceStudents.length; i++) {
            ((StudentImage) entrance.getChildren().get(i)).setStudent(entranceStudents[i]);
        }
        RealmType[] diningRoomStudents = RealmType.getRealmsFromIntegerRepresentation(playerView.getSchoolStudents().getSecond());
        for (RealmType r: diningRoomStudents) {
            insertInDiningRoom(r);
        }
        while (towers.getChildren().size() > playerView.getNumTowers()) {
            towers.getChildren().remove(towers.getChildren().size() - 1);
        }
        for (RealmType r: RealmType.values()) {
            if (nickname.equals(modelView.getField().getProfessorOwner(r))) insertProfessor(r);
        }
        updateCoins();
    }

    /**
     * method initializeStudents inserts empty place for missing students
     *
     * @param container container of the students
     */
    private void initializeStudents(AnchorPane container) {
        List<Node> students = container.getChildren();
        for (int i = 0; i < students.size(); i++) {
            Circle circle = (Circle) students.get(i);
            container.getChildren().set(i, new StudentImage(circle));
        }
    }

    /**
     * method initializeProfessors inserts empty place for the professors in the school
     */
    private void initializeProfessors() {
        List<Node> professors = professorTable.getChildren();
        for (int i = 0; i < professors.size(); i++) {
            Circle circle = (Circle) professors.get(i);
            ProfessorImage professor = new ProfessorImage(circle.getRadius(), circle.getLayoutX(), circle.getLayoutY());
            professor.setRealmType(diningRoomOrder.get(i));
            professor.setOpacity(0);
            professorTable.getChildren().set(i, professor);
        }
    }

    /**
     * method insertProfessor inserts image for professors in the school
     *
     * @param professorType type of the professor added
     */
    public void insertProfessor(RealmType professorType) {
        ProfessorImage professorImage = (ProfessorImage) professorTable.getChildren().get(diningRoomOrder.indexOf(professorType));
        professorImage.setOpacity(1);
    }

    /**
     * method removeProfessor removes image of the professor in the school
     *
     * @param professorType type of the professor removed
     */
    public void removeProfessor(RealmType professorType) {
        ProfessorImage professorImage = (ProfessorImage) professorTable.getChildren().get(diningRoomOrder.indexOf(professorType));
        professorImage.setOpacity(0);
    }

    /**
     * method insertStudentEntrance inserts students in the school entrance
     *
     * @param student type of the student added
     */
    public synchronized void insertStudentEntrance(RealmType student) {
        List<Node> students = entrance.getChildren().stream().toList();
        Integer emptyIndex = findFirstEmpty(students);
        if (emptyIndex == null) return;
        StudentImage studentImage = (StudentImage) students.get(emptyIndex);
        studentImage.setStudent(student);
        studentImage.setOnDragDetected(dragStartStudentHandler);
    }

    /**
     * method findFirstEmpty finds first empty place of the passed list of students
     *
     * @param students list of students
     * @return returns index of the first empty place in the list of student
     */
    private Integer findFirstEmpty(List<Node> students) {
        Optional<Node> studentEmpty = students.stream().filter(s -> s.getOpacity() == 0).findFirst();
        if (studentEmpty.isEmpty()) return null;
        return students.indexOf(studentEmpty.get());
    }

    /**
     * method removeFromEntrance removes students from the school entrance
     *
     * @param studentType type of the student removed
     * @param isClientSchool boolean that says if the school is a client's school or not
     */
    public synchronized void removeFromEntrance(RealmType studentType, boolean isClientSchool) {
        Optional<Node> student = entrance.getChildren().stream()
                .filter(s -> ((StudentImage) s).getStudentType() == studentType)
                .findFirst();
        student.ifPresent(s -> {
            ((StudentImage) s).reset();
            if (isClientSchool) s.removeEventHandler(MouseEvent.DRAG_DETECTED, dragStartStudentHandler);
        });
    }

    /**
     * method setDragStartStudentHandler links for every student the entrance for the drag and drop
     *
     * @param handler handler of drag start event for drag and drop
     */
    public void setDragStartStudentHandler(EventHandler<MouseEvent> handler) {
        this.dragStartStudentHandler = handler;
        entrance.getChildren().stream()
                .filter(s -> s.getOpacity() == 1)
                .forEach(s -> s.setOnDragDetected(handler));
    }

    /**
     * method registerDiningRoomDragAndDrop logs events of drag and drop in the dining room
     *
     * @param dragOverHandler handler for the drag over event
     * @param dropHandler handler for the drag and drop event
     */
    public void registerDiningRoomDragAndDrop(EventHandler<DragEvent> dragOverHandler, EventHandler<DragEvent> dropHandler) {
        diningRoom.setOnDragOver(dragOverHandler);
        diningRoom.setOnDragDropped(dropHandler);
    }

    /**
     * method insertInDiningRoom inserts students in the school's dining room
     *
     * @param student student tha will be added to the school's dining room
     */
    public synchronized void insertInDiningRoom(RealmType student) {
        int studentIdx = diningRoomOrder.indexOf(student);
        List<Node> studentsLine = diningRoom.getChildren().subList(studentIdx * 10, (studentIdx + 1) * 10);
        int i = 0;
        while (studentsLine.get(i).getOpacity() == 1) i++;
        ((StudentImage) studentsLine.get(i)).setStudent(student);
    }

    /**
     * method removeFromDiningRoom removes students from the school's dining room'
     *
     * @param student student that will be removed from the school's dining room
     */
    public void removeFromDiningRoom(RealmType student) {
        int studentIdx = diningRoomOrder.indexOf(student);
        List<Node> studentsLine = diningRoom.getChildren().subList(studentIdx * 10, (studentIdx + 1) * 10);
        int i = 0;
        while (studentsLine.get(i).getOpacity() == 1) i++;
        studentsLine.get(i - 1).setOpacity(0);
    }

    /**
     * method moveStudentFromEntranceToDiningRoom moves student from the school's entrance to the school's dining room
     *
     * @param studentType student that will be moved
     */
    public void moveStudentFromEntranceToDiningRoom(RealmType studentType) {
        Optional<Node> studentNode = entrance.getChildren().stream()
                .filter(s -> ((StudentImage) s).getStudentType() == studentType)
                .findFirst();
        studentNode.ifPresent(student -> {
            StudentImage studentImage = (StudentImage) student;
            StudentImage fakeStudent = new StudentImage(studentImage);
            fakeStudent.setStudent(studentType);
            studentImage.reset();
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

    /**
     * method findFirstFreePosition finds the first empty place in a list of student in the dining room
     *
     * @param student type of the students in the list checked
     * @return returns 
     */
    private Pair<Double, Double> findFirstFreePosition(RealmType student) {
        int studentIdx = diningRoomOrder.indexOf(student);
        List<Node> studentsLine = diningRoom.getChildren().subList(studentIdx * 10, (studentIdx + 1) * 10);
        int i = 0;
        while (studentsLine.get(i).getOpacity() == 1) i++;
        return new Pair<>(studentsLine.get(i).getLayoutX(), studentsLine.get(i).getLayoutY());
    }

    /**
     * method getEntrancePane gets the school's entrance AnchorPane
     *
     * @return returns the school's entrance AnchorPane
     */
    public AnchorPane getEntrancePane() {
        return entrance;
    }

    /**
     * method getEntrancePane gets the school's dining room AnchorPane
     *
     * @return returns the school's dining room AnchorPane
     */
    public AnchorPane getDiningRoomPane() {
        return diningRoom;
    }

    /**
     * method getEntranceStudentLayout gets student's layout (x,y)
     *
     * @param student the student
     * @return the layout of the specified student
     * @throws NoSuchElementException if there isn't such a student
     */
    public Pair<Double, Double> getEntranceStudentLayout(RealmType student) throws NoSuchElementException {
        Optional<Node> studentPresent = entrance.getChildren().stream()
                .filter(s -> s.getOpacity() == 1 &&  ((StudentImage) s).getStudentType() == student).findFirst();
        if (studentPresent.isEmpty()) throw new NoSuchElementException();
        double xPosition = container.getLayoutX() + school.getLayoutX() + entrance.getLayoutX() + studentPresent.get().getLayoutX();
        double yPosition = container.getLayoutY() + school.getLayoutY() + entrance.getLayoutY() + studentPresent.get().getLayoutY();
        return new Pair<>(xPosition, yPosition);
    }

    /**
     * method getDimStudentsRadius gets dimension of the student's circle radius
     *
     * @return returns dimension of the student's circle radius
     */
    public double getDimStudentsRadius() {
        return dimStudentsRadius;
    }

    /**
     * method removeTower removes a tower from the school
     *
     */
    public void removeTower() {
        List<Node> towersVisible = towers.getChildren().stream().filter(Node::isVisible).toList();
        towersVisible.get(towersVisible.size() - 1).setVisible(false);
    }

    /**
     * method insertTower inserts a tower in the school
     *
     */
    public void insertTower() {
        towers.getChildren().stream().filter(n -> !n.isVisible()).findFirst().ifPresent(node -> node.setVisible(false));
    }

    /**
     * method updateCoins updates a player's coins' number
     *
     */
    public void updateCoins() {
        Label coinsLabel = (Label) ((VBox) container.getChildren().get(2)).getChildren().get(1);
        int numCoins = playerView.getPlayerCoins();
        coinsLabel.setText(Integer.toString(numCoins));
    }

    /**
     * method updateEntrance updates the school entrance with new students
     *
     */
    public void updateEntrance() {
        entrance.getChildren().forEach(s -> ((StudentImage) s).reset());
        RealmType[] entranceStudents = RealmType.getRealmsFromIntegerRepresentation(playerView.getSchoolStudents().getFirst());
        for (int i = 0; i < entranceStudents.length; i++) {
            ((StudentImage) entrance.getChildren().get(i)).setStudent(entranceStudents[i]);
        }
    }
}
