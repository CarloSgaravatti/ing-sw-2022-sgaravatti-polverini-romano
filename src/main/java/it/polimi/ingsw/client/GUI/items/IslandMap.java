package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.utils.Pair;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

/**
 * IslandMap is used for represent the map of the islands in the main scene where mother nature, students and towers will move and appear/disappear during the game
 *
 */
public class IslandMap {
    private final AnchorPane container;
    private final ModelView modelView;
    private final List<IslandSubScene> islandsSubScenes = new ArrayList<>();
    private final double islandsWidth;

    /**
     * constructor used for initialize the container of the islands and the modelView
     *
     * @param container AnchorPane that will contain the islands
     * @param modelView modelView of the island map
     */
    public IslandMap(AnchorPane container, ModelView modelView) {
        this.container = container;
        this.modelView = modelView;
        eliminateRedundantIslands(new ArrayList<>(container.getChildren().subList(0, 12)));
        initializeMap();
        islandsSubScenes.get(modelView.getField().getMotherNaturePosition()).setMotherNature(true);
        islandsWidth = islandsSubScenes.get(0).getPrefWidth();
    }


    /**
     * method used for initialize the island map, it inserts all the islands as images using the method IslandSubScene
     *
     */
    public void initializeMap() {
        List<Node> islands = container.getChildren().subList(0, modelView.getField().getIslandSize());
        for (int i = 0; i < islands.size(); i++) {
            Pair<Double, Double> layout = new Pair<>(islands.get(i).getLayoutX(), islands.get(i).getLayoutY());
            IslandSubScene islandSubScene = new IslandSubScene();
            islandSubScene.init(modelView.getField(), i);
            islandSubScene.setLayoutX(layout.getFirst());
            islandSubScene.setLayoutY(layout.getSecond());
            islandsSubScenes.add(islandSubScene);
            islandSubScene.setOnDragEntered(dragEvent -> {
                IslandSubScene target = (IslandSubScene) dragEvent.getTarget();
                if (dragEvent.getDragboard() != null) {
                    DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.RED, 15, 0.2, 0, 0);
                    target.setEffect(dropShadow);
                }
                dragEvent.consume();
            });
            islandSubScene.setOnDragExited(dragEvent -> {
                IslandSubScene target = (IslandSubScene) dragEvent.getTarget();
                target.setEffect(null);
                dragEvent.consume();
            });
        }
        //Need to do this because otherwise there is ConcurrentModificationException
        for (int i = 0; i < islandsSubScenes.size(); i++) {
            container.getChildren().remove(i);
            container.getChildren().add(i, islandsSubScenes.get(i));
        }
    }

    /**
     * method used for delete redundant islands choosing a random island
     *
     * @param islands
     */
    private void eliminateRedundantIslands(List<Node> islands) {
        Random rnd = new Random();
        while (islands.size() > modelView.getField().getIslandSize()) {
            Node islandRemoved = islands.remove(rnd.nextInt(islands.size()));
            container.getChildren().remove(islandRemoved);
        }
    }

    /**
     * method setDragAndDropHandlers links for every island the handler for the drag and drop
     *
     * @param dragOverIsland handler of drag over island event
     * @param dropOnIsland handler of drop on island event
     * @param dragMotherNatureStartHandler handler of drag mother nature event
     */
    public void setDragAndDropHandlers(EventHandler<DragEvent> dragOverIsland, EventHandler<DragEvent> dropOnIsland,
                                       EventHandler<MouseEvent> dragMotherNatureStartHandler) {
        for (IslandSubScene island: islandsSubScenes) {
            island.setOnDragOver(dragOverIsland);
            island.setOnDragDropped(dropOnIsland);
            island.getMotherNature().setOnDragDetected(dragMotherNatureStartHandler);
        }
    }

    /**
     * method used to move mother nature between the islands
     *
     * @param oldPosition old position of mother nature (island index)
     * @param newPosition new position of mother nature (island index)
     */
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
    }

    /**
     * method getIslands gives a list of islands
     *
     * @return returns the list of islands requested
     */
    public List<IslandSubScene> getIslands() {
        return islandsSubScenes;
    }

    /**
     * method getIslandPosition gives the position of the island requested
     *
     * @param islandId island that position is requested
     * @return returns a pair of positions (on X and Y)
     */
    public Pair<Double, Double> getIslandPosition(int islandId) {
        IslandSubScene island = islandsSubScenes.get(islandId);
        return new Pair<>(island.getLayoutX() + island.getWidth() / 2,
                island.getLayoutY() + island.getHeight() / 2);
    }

    /**
     * method getIslandById gets and island searching by the island Id
     *
     * @param islandId Id of the island requested
     * @return returns the island requested
     */
    public Optional<IslandSubScene> getIslandById(int islandId) {
        return islandsSubScenes.stream().filter(i -> i.getIslandId() == islandId).findFirst();
    }

    /**
     * method mergeIslands does the merge of two or more island during the game
     *
     * @param unifiedIds indexes of islands that will be merged
     */
    public void mergeIslands(List<Integer> unifiedIds) {
        Integer centerId = (unifiedIds.size() == 2) ? unifiedIds.get(0) : unifiedIds.get(1);
        int minId = unifiedIds.stream().min(Comparator.comparingInt(i -> i)).orElse(unifiedIds.get(0));
        unifiedIds.remove(centerId);
        Optional<IslandSubScene> centerIsland = getIslandById(centerId);
        if (centerIsland.isEmpty()) return;
        centerIsland.get().setIslandId(minId);
        for (Integer id: unifiedIds) {
            Optional<IslandSubScene> islandToMove = getIslandById(id);
            if (islandToMove.isPresent()) {
                IslandSubScene island = islandToMove.get();
                double xTranslate = centerIsland.get().getLayoutX() - island.getLayoutX();
                double yTranslate = centerIsland.get().getLayoutY() - island.getLayoutY();
                TranslateTransition transition = new TranslateTransition(Duration.millis(2000), island);
                transition.setDelay(Duration.millis(1500));
                transition.setByX(xTranslate);
                transition.setByY(yTranslate);
                transition.play();
                transition.setOnFinished(actionEvent -> {
                    islandsSubScenes.remove(island);
                    container.getChildren().remove(island);
                    centerIsland.get().updateIsland(modelView.getField());
                    centerIsland.get().setMotherNature(true);
                });
            }
        }
        islandsSubScenes.stream()
                .filter(islandSubScene -> islandSubScene.getIslandId() >
                        unifiedIds.stream().max(Comparator.comparingInt(id -> id)).orElse(0))
                .forEach(islandSubScene -> islandSubScene.setIslandId(islandSubScene.getIslandId() - 1));
    }

}
