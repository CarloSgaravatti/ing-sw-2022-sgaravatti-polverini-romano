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

public class IslandMap {
    private final AnchorPane container;
    private final ModelView modelView;
    private final List<IslandSubScene> islandsSubScenes = new ArrayList<>();
    private final double islandsWidth;

    public IslandMap(AnchorPane container, ModelView modelView) {
        this.container = container;
        this.modelView = modelView;
        eliminateRedundantIslands(new ArrayList<>(container.getChildren().subList(0, 12)));
        initializeMap();
        islandsSubScenes.get(modelView.getField().getMotherNaturePosition()).setMotherNature(true);
        islandsWidth = islandsSubScenes.get(0).getPrefWidth();
    }

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

    private void eliminateRedundantIslands(List<Node> islands) {
        Random rnd = new Random();
        while (islands.size() > modelView.getField().getIslandSize()) {
            Node islandRemoved = islands.remove(rnd.nextInt(islands.size()));
            container.getChildren().remove(islandRemoved);
        }
    }

    public void setDragAndDropHandlers(EventHandler<DragEvent> dragOverIsland, EventHandler<DragEvent> dropOnIsland,
                                       EventHandler<MouseEvent> dragMotherNatureStartHandler) {
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
    }

    public List<IslandSubScene> getIslands() {
        return islandsSubScenes;
    }

    public Pair<Double, Double> getIslandPosition(int islandId) {
        IslandSubScene island = islandsSubScenes.get(islandId);
        return new Pair<>(island.getLayoutX() + island.getWidth() / 2,
                island.getLayoutY() + island.getHeight() / 2);
    }

    @Deprecated
    public List<IslandSubScene> getIslandsById(int islandId) {
        return  islandsSubScenes.stream().filter(i -> i.getIslandId() == islandId).toList();
    }

    public Optional<IslandSubScene> getIslandById(int islandId) {
        return islandsSubScenes.stream().filter(i -> i.getIslandId() == islandId).findFirst();
    }

    public void mergeIslands2(List<Integer> unifiedIds) {
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

    @Deprecated
    public void mergeIslands(List<Integer> unifiedIds) {
        Integer centerId = (unifiedIds.size() == 2) ? unifiedIds.get(0) : unifiedIds.get(1);
        int minId = unifiedIds.stream().min(Comparator.comparingInt(i -> i)).orElse(unifiedIds.get(0));
        unifiedIds.remove(centerId);
        List<IslandSubScene> centerIsland = getIslandsById(centerId);
        for (Integer i: unifiedIds) {
            List<IslandSubScene> islandToMove = getIslandsById(i);
            Optional<IslandSubScene> closerIslandToFirst = getClosestIsland(islandToMove, centerIsland.get(0));
            Optional<IslandSubScene> closerIslandToLast = getClosestIsland(islandToMove, centerIsland.get(centerIsland.size() - 1));
            if (closerIslandToFirst.isPresent() && closerIslandToLast.isPresent()) {
                double xTranslate;
                double yTranslate;
                double offset = islandsWidth / 1.6; //TODO: control this and eventually change it
                if (distance(closerIslandToFirst.get(), centerIsland.get(0)) <=
                        distance(closerIslandToLast.get(), centerIsland.get(centerIsland.size() - 1))) {
                    xTranslate = (centerIsland.get(0).getLayoutX() - closerIslandToFirst.get().getLayoutX()) +
                            ((centerIsland.get(0).getLayoutX() > closerIslandToFirst.get().getLayoutX()) ? -offset : offset);
                    yTranslate = (centerIsland.get(0).getLayoutY() - closerIslandToFirst.get().getLayoutY()) +
                            ((centerIsland.get(0).getLayoutY() > closerIslandToFirst.get().getLayoutY()) ? -offset : offset);
                } else {
                    xTranslate = (centerIsland.get(centerIsland.size() - 1).getLayoutX() - closerIslandToLast.get().getLayoutX()) +
                            ((centerIsland.get(centerIsland.size() - 1).getLayoutX() > closerIslandToLast.get().getLayoutX()) ? -offset : offset);
                    yTranslate = (centerIsland.get(centerIsland.size() - 1).getLayoutY() - closerIslandToLast.get().getLayoutY()) +
                            ((centerIsland.get(centerIsland.size() - 1).getLayoutY() > closerIslandToLast.get().getLayoutY()) ? -offset : offset);
                }
                islandToMove.forEach(island -> {
                    TranslateTransition transition = new TranslateTransition(Duration.millis(2000), island);
                    transition.setByX(xTranslate);
                    transition.setByY(yTranslate);
                    transition.play();
                    island.setIslandId(minId);
                    transition.setOnFinished(actionEvent -> {
                        island.getStyleClass().remove("root-island-in-group");
                        //island.setRootIsland(false);
                        island.setTranslateX(0);
                        island.setLayoutX(island.getLayoutX() + transition.getByX());
                        island.setTranslateY(0);
                        island.setLayoutY(island.getLayoutY() + transition.getByY());
                    });
                });
            }
        }
        if (centerIsland.size() == 1) centerIsland.get(0).getStyleClass().add("root-island-in-group");
        if (centerId != minId) centerIsland.forEach(islandSubScene -> islandSubScene.setIslandId(minId));
        //decrement ids to maintain compatibility with CLI (and also model)
        islandsSubScenes.stream()
                .filter(i -> i.getIslandId() > centerIsland.get(0).getIslandId()
                        && i.getIslandId() > unifiedIds.stream().max(Comparator.comparingInt(id -> id)).orElse(0))
                .forEach(i -> i.setIslandId(i.getIslandId() - unifiedIds.size() + 1));
        islandsSubScenes.forEach(island -> System.out.println(island.getIslandId()));
    }

    private Optional<IslandSubScene> getClosestIsland(List<IslandSubScene> islands, IslandSubScene centerIsland) {
        return islands.stream().min((i1, i2) -> {
            double i1Distance = distance(i1, centerIsland);
            double i2Distance = distance(i2, centerIsland);
            return Double.compare(i1Distance, i2Distance);
        });
    }

    private double distance(IslandSubScene i1, IslandSubScene i2) {
        return Math.pow(i1.getLayoutX() - i2.getLayoutX(), 2) +
                Math.pow(i1.getLayoutY() - i2.getLayoutY(), 2);
    }
}
