package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.GUI.constants.Constants;
import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * class IslandSubScene is used for represent the single islands with the addition of students, towers and mother nature on them during the game
 *
 */
public class IslandSubScene extends AnchorPane implements Initializable {
    @FXML private HBox redStudent;
    @FXML private HBox greenStudent;
    @FXML private HBox blueStudent;
    @FXML private HBox yellowStudent;
    @FXML private AnchorPane motherNature;
    @FXML private ImageView tower;
    @FXML private HBox pinkStudent;
    @FXML private HBox noEntryTile;
    private final Map<RealmType, HBox> students = new HashMap<>();
    private int islandId;

    /**
     * constructor of the IslandsSubScene that initializes the image of the single island
     *
     */
    public IslandSubScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/island.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method init initialize the island adding it in the fieldView
     *
     * @param fieldView fieldView of the islands
     * @param islandId Id of the island added in the filedView
     */
    public void init(FieldView fieldView, int islandId) {
        /*Triplet<Integer[], Integer, TowerType> island = fieldView.getIsland(islandId);
        Integer[] studentsInIsland = island.getFirst();
        for (RealmType realm: RealmType.values()) {
            for (int i = 0; i < studentsInIsland[realm.ordinal()]; i++) addStudent(realm);
        }
        if (island.getThird() != null) this.addTower(island.getThird());*/
        this.islandId = islandId;
        updateIsland(fieldView);
    }

    /**
     * method addStudent adds a student on an island
     *
     * @param student type of the student that will be added on the island
     */
    public void addStudent(RealmType student) {
        Label label = (Label) students.get(student).getChildren().get(0);
        int labelText = Integer.parseInt(label.getText());
        label.setText(String.valueOf(labelText + 1));
        if (labelText == 0) {
            students.get(student).setVisible(true);
        }
    }

    /**
     * method addTower adds a tower on an island
     *
     * @param tower type of the tower that will be added on the island
     */
    public void addTower(TowerType tower) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.towerImages.get(tower))));
        this.tower.setImage(image);
        this.tower.getParent().setVisible(true);
    }

    /**
     * method setMotherNature sets the presence of mother nature on the island
     *
     * @param present boolean that says if mother nature is present on the island or not
     */
    public void setMotherNature(boolean present) {
        this.motherNature.setVisible(present);
    }

    /**
     * method insertNoEntryTile inserts a No Entry Tile on the island
     *
     */
    public void insertNoEntryTile() {
        Label label = (Label) noEntryTile.getChildren().get(0);
        int labelText = Integer.parseInt(label.getText());
        label.setText(String.valueOf(labelText + 1));
        if (labelText == 0) {
            noEntryTile.setVisible(true);
        }
    }

    /**
     * method removeNoEntryTile removes a No Entry Tile on the island
     *
     */
    public void removeNoEntryTile() {
        Label label = (Label) noEntryTile.getChildren().get(0);
        int labelText = Integer.parseInt(label.getText());
        label.setText(String.valueOf(labelText - 1));
        if (labelText == 1) {
            noEntryTile.setVisible(false);
        }
    }

    /**
     * method getNumNoEntryTile gets the number of No Entry Tiles on the island
     *
     * @return returns the number of No Entry Tiles on the island
     */
    public int getNumNoEntryTile() {
        Label label = (Label) noEntryTile.getChildren().get(0);
        return Integer.parseInt(label.getText());
    }

    /**
     * method getMotherNature gets the AnchorPane of mother nature
     *
     * @return returns the AnchorPane of mother nature
     */
    public AnchorPane getMotherNature() {
        return motherNature;
    }

    /**
     * method getMotherNatureLayout gets the layout of mother nature by a pair of position (on X and on Y)
     *
     * @return returns the mother nature's layout
     */
    public Pair<Double, Double> getMotherNatureLayout() {
        return new Pair<>(motherNature.getLayoutX(), motherNature.getLayoutY());
    }

    /**
     * method getIslandId gets the id of the island
     *
     * @return returns the id of the island
     */
    public int getIslandId() {
        return islandId;
    }

    /**
     * method setIslandId sets an id for the island
     *
     * @param islandId id that will be linked to an island
     */
    public void setIslandId(int islandId) {
        this.islandId = islandId;
    }

    /**
     * method initialize initializes the IslandSubScene
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        students.put(RealmType.YELLOW_GNOMES, yellowStudent);
        students.put(RealmType.BLUE_UNICORNS, blueStudent);
        students.put(RealmType.GREEN_FROGS, greenStudent);
        students.put(RealmType.RED_DRAGONS, redStudent);
        students.put(RealmType.PINK_FAIRES, pinkStudent);
        students.values().forEach(hBox -> hBox.setVisible(false));
        this.tower.getParent().setVisible(false);
        this.motherNature.setVisible(false);
        this.noEntryTile.setVisible(false);
        super.getStyleClass().add("island-pane" + (new Random().nextInt(3) + 1));
    }

    /**
     * method updateIsland updates an island in the filedView
     *
     * @param fieldView filedView where the island will be updated
     */
    public void updateIsland(FieldView fieldView) {
        Triplet<Integer[], Integer, TowerType> island = fieldView.getIsland(islandId);
        Integer[] students = island.getFirst();
        for (RealmType r: RealmType.values()) {
            Label label = (Label) this.students.get(r).getChildren().get(0);
            int studentOfType = students[r.ordinal()];
            label.setText(String.valueOf(studentOfType));
            this.students.get(r).setVisible(studentOfType > 0);
        }
        if (island.getThird() != null) {
            addTower(island.getThird());
            Label towerLabel = (Label) ((HBox) tower.getParent()).getChildren().get(0);
            towerLabel.setText(String.valueOf(island.getSecond()));
            tower.getParent().setVisible(true);
        } else tower.getParent().setVisible(false);
        if (fieldView.getExpertField() != null) {
            int noEntryTiles = fieldView.getExpertField().getNoEntryTilesOnIsland(islandId);
            Label noEntryTileLabel = (Label) noEntryTile.getChildren().get(0);
            noEntryTileLabel.setText(String.valueOf(noEntryTiles));
            noEntryTile.setVisible(noEntryTiles != 0);
        }
    }
}
