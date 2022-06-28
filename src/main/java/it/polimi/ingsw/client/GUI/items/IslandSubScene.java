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
    private boolean rootIsland;

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

    public void init(FieldView fieldView, int islandId) {
        Triplet<Integer[], Integer, TowerType> island = fieldView.getIsland(islandId);
        Integer[] studentsInIsland = island.getFirst();
        for (RealmType realm: RealmType.values()) {
            for (int i = 0; i < studentsInIsland[realm.ordinal()]; i++) addStudent(realm);
        }
        if (island.getThird() != null) this.addTower(island.getThird());
        this.islandId = islandId;
        this.rootIsland = true;
    }

    public void addStudent(RealmType student) {
        Label label = (Label) students.get(student).getChildren().get(0);
        int labelText = Integer.parseInt(label.getText());
        label.setText(String.valueOf(labelText + 1));
        if (labelText == 0) {
            students.get(student).setVisible(true);
        }
    }

    public void addTower(TowerType tower) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(Constants.towerImages.get(tower))));
        this.tower.setImage(image);
        this.tower.setVisible(true);
    }

    public void setMotherNature(boolean present) {
        this.motherNature.setVisible(present);
    }

    public void insertNoEntryTile() {
        Label label = (Label) noEntryTile.getChildren().get(0);
        int labelText = Integer.parseInt(label.getText());
        label.setText(String.valueOf(labelText + 1));
        if (labelText == 0) {
            noEntryTile.setVisible(true);
        }
    }

    public void removeNoEntryTile() {
        Label label = (Label) noEntryTile.getChildren().get(0);
        int labelText = Integer.parseInt(label.getText());
        label.setText(String.valueOf(labelText - 1));
        if (labelText == 1) {
            noEntryTile.setVisible(false);
        }
    }

    public int getNumNoEntryTile() {
        Label label = (Label) noEntryTile.getChildren().get(0);
        return Integer.parseInt(label.getText());
    }


    public AnchorPane getMotherNature() {
        return motherNature;
    }

    public Pair<Double, Double> getMotherNatureLayout() {
        return new Pair<>(motherNature.getLayoutX(), motherNature.getLayoutY());
    }

    public int getIslandId() {
        return islandId;
    }

    public void setIslandId(int islandId) {
        this.islandId = islandId;
    }

    public boolean isRootIsland() {
        return rootIsland;
    }

    public void setRootIsland(boolean rootIsland) {
        this.rootIsland = rootIsland;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        students.put(RealmType.YELLOW_GNOMES, yellowStudent);
        students.put(RealmType.BLUE_UNICORNS, blueStudent);
        students.put(RealmType.GREEN_FROGS, greenStudent);
        students.put(RealmType.RED_DRAGONS, redStudent);
        students.put(RealmType.PINK_FAIRES, pinkStudent);
        students.values().forEach(hBox -> hBox.setVisible(false));
        this.tower.setVisible(false);
        this.motherNature.setVisible(false);
        this.noEntryTile.setVisible(false);
        super.getStyleClass().add("island-pane" + (new Random().nextInt(3) + 1));
    }
}
