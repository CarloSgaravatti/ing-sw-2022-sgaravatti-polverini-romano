package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.utils.Pair;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class IslandMap {
    private final AnchorPane container;
    private final ModelView modelView;
    private final List<IslandSubScene> islandsSubScenes = new ArrayList<>();
    private EventHandler<DragEvent> dragOverHandler;
    private EventHandler<DragEvent> dropHandler;
    private EventHandler<MouseEvent> dragMotherNatureStart;

    public IslandMap(AnchorPane container, ModelView modelView) {
        this.container = container;
        this.modelView = modelView;
        initializeMap();
        islandsSubScenes.get(modelView.getField().getMotherNaturePosition()).setMotherNature(true);
    }

    public void initializeMap() {
        List<Node> islands = container.getChildren().subList(0, 12);
        for (int i = 0; i < 12; i++) {
            Pair<Double, Double> layout = new Pair<>(islands.get(i).getLayoutX(), islands.get(i).getLayoutY());
            IslandSubScene islandSubScene = new IslandSubScene();
            islandSubScene.init(modelView.getField().getIsland(i).getFirst(), i);
            islandSubScene.setLayoutX(layout.getFirst());
            islandSubScene.setLayoutY(layout.getSecond());
            //container.getChildren().add(i, islandSubScene);
            islandsSubScenes.add(islandSubScene);
        }
        //Need to do this because otherwise there is ConcurrentModificationException
        for (int i = 0; i < islandsSubScenes.size(); i++) {
            container.getChildren().remove(i);
            container.getChildren().add(i, islandsSubScenes.get(i));
        }
    }

    public void setDragAndDropHandlers(EventHandler<DragEvent> dragOverIsland, EventHandler<DragEvent> dropOnIsland,
                                       EventHandler<MouseEvent> dragMotherNatureStartHandler) {
        this.dragOverHandler = dragOverIsland;
        this.dropHandler = dropOnIsland;
        this.dragMotherNatureStart = dragMotherNatureStartHandler;
        //this.motherNature.setOnDragDetected(dragMotherNatureStartHandler);
        /*for (IslandImage island: islands) {
            island.getIslandPane().setOnDragOver(dragOverIsland);
            island.getIslandPane().setOnDragDropped(dropOnIsland);
        }*/
        for (IslandSubScene island: islandsSubScenes) {
            island.setOnDragOver(dragOverIsland);
            island.setOnDragDropped(dropOnIsland);
            island.getMotherNature().setOnDragDetected(dragMotherNatureStartHandler);
        }
    }

    public void moveMotherNature(int oldPosition, int newPosition) {
        AnchorPane motherNature = new AnchorPane();
        motherNature.getStyleClass().add("mother-nature");
        motherNature.setPrefWidth(islandsSubScenes.get(0).getMotherNature().getWidth());
        motherNature.setPrefHeight(islandsSubScenes.get(0).getMotherNature().getHeight());
        Pair<Double, Double> motherNatureIslandLayout = islandsSubScenes.get(0).getMotherNatureLayout();
        motherNature.setLayoutX(motherNatureIslandLayout.getFirst() + islandsSubScenes.get(oldPosition).getLayoutX());
        motherNature.setLayoutY(motherNatureIslandLayout.getSecond() + islandsSubScenes.get(oldPosition).getLayoutY());
        container.getChildren().add(motherNature);
        TranslateTransition transition2 = new TranslateTransition();
        transition2.setByX(islandsSubScenes.get(newPosition).getLayoutX() - islandsSubScenes.get(oldPosition).getLayoutX());
        transition2.setByY(islandsSubScenes.get(newPosition).getLayoutY() - islandsSubScenes.get(oldPosition).getLayoutY());
        transition2.setNode(motherNature);
        transition2.setDuration(Duration.millis(1500));
        islandsSubScenes.get(oldPosition).setMotherNature(false);
        transition2.play();
        transition2.setOnFinished(actionEvent -> {
            islandsSubScenes.get(newPosition).setMotherNature(true);
            container.getChildren().remove(motherNature);
        });
        /*AnchorPane oldIsland = islands.get(oldPosition).getIslandPane();
        Rectangle fakeMotherNature = new Rectangle(oldIsland.getWidth() / 5, oldIsland.getHeight() / 5);
        fakeMotherNature.setFill(new ImagePattern(new Image(Objects
                .requireNonNull(getClass().getResourceAsStream("/images/mother_nature.png")))));
        motherNature.setOpacity(0);
        islands.get(oldPosition).setMotherNature(false, motherNature);
        islands.get(oldPosition).setMotherNature(true, fakeMotherNature);
        //islandsSubScenes.get()
        fakeMotherNature.setLayoutX(oldIsland.getWidth() / 2);
        fakeMotherNature.setLayoutY(oldIsland.getHeight() / 2);
        islands.get(newPosition).setMotherNature(true, this.motherNature);
        Pair<Double, Double> lastPos = new Pair<>(oldIsland.getLayoutX(), oldIsland.getLayoutY());
        AnchorPane newIsland = islands.get(newPosition).getIslandPane();
        Pair<Double, Double> newPos = new Pair<>(newIsland.getLayoutX(), newIsland.getLayoutY());
        TranslateTransition transition = new TranslateTransition();
        transition.setByX(newPos.getFirst() - lastPos.getFirst());
        transition.setByY(newPos.getSecond() - lastPos.getSecond());
        transition.setNode(fakeMotherNature);
        transition.setDuration(Duration.millis(1500));
        transition.play();
        transition.setOnFinished(actionEvent -> {
            islands.get(oldPosition).setMotherNature(false, fakeMotherNature);
            motherNature.setOpacity(1);
        });*/
    }

    public List<IslandSubScene> getIslands() {
        return islandsSubScenes;
    }

    public Pair<Double, Double> getIslandPosition(int islandId) {
        IslandSubScene island = islandsSubScenes.get(islandId);
        return new Pair<>(island.getLayoutX() + island.getWidth() / 2,
                island.getLayoutY() + island.getHeight() / 2);
    }
}
