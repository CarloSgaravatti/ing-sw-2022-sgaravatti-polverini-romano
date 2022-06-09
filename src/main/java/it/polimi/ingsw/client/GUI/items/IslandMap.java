package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.utils.Pair;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IslandMap {
    private final AnchorPane container;
    private final ModelView modelView;
    private final List<IslandImage> islands = new ArrayList<>();
    private final Rectangle motherNature;
    private EventHandler<DragEvent> dragOverHandler;
    private EventHandler<DragEvent> dropHandler;
    private EventHandler<MouseEvent> dragMotherNatureStart;

    public IslandMap(AnchorPane container, ModelView modelView) {
        this.container = container;
        this.modelView = modelView;
        /*for (int i = 0; i < modelView.getField().getIslandSize(); i++) {
            islands.add(new IslandImage(i, modelView.getField(), 50));
        }*/
        initializeMap();
        AnchorPane islandPane = islands.get(0).getIslandPane();
        motherNature = new Rectangle(islandPane.getWidth() / 5, islandPane.getHeight() / 5);
        motherNature.setLayoutX(islandPane.getWidth() / 2);
        motherNature.setLayoutY(islandPane.getHeight() / 2);
        motherNature.setFill(new ImagePattern(new Image(Objects
                .requireNonNull(getClass().getResourceAsStream("/images/mother_nature.png")))));
        islands.get(modelView.getField().getMotherNaturePosition()).setMotherNature(true, motherNature);
    }

    public void initializeMap() {
        List<Node> islands = container.getChildren().subList(0, 12);
        for (int i = 0; i < islands.size(); i++) {
            IslandImage island = new IslandImage(i, modelView.getField(), (AnchorPane)islands.get(i));
            /*Node node = islands.get(i);
            island.setLayoutX(node.getLayoutX());
            island.setLayoutY(node.getLayoutY());
            container.getChildren().set(i, island);*/
            this.islands.add(island);
        }
    }

    public void setDragAndDropHandlers(EventHandler<DragEvent> dragOverIsland, EventHandler<DragEvent> dropOnIsland,
                                       EventHandler<MouseEvent> dragMotherNatureStartHandler) {
        this.dragOverHandler = dragOverIsland;
        this.dropHandler = dropOnIsland;
        this.dragMotherNatureStart = dragMotherNatureStartHandler;
        this.motherNature.setOnDragDetected(dragMotherNatureStartHandler);
        for (IslandImage island: islands) {
            island.getIslandPane().setOnDragOver(dragOverIsland);
            island.getIslandPane().setOnDragDropped(dropOnIsland);
            //island.setDragStartHandler(dragMotherNatureStartHandler);
        }
    }

    public void moveMotherNature(int oldPosition, int newPosition) {
        AnchorPane oldIsland = islands.get(oldPosition).getIslandPane();
        Rectangle fakeMotherNature = new Rectangle(oldIsland.getWidth() / 5, oldIsland.getHeight() / 5);
        fakeMotherNature.setFill(new ImagePattern(new Image(Objects
                .requireNonNull(getClass().getResourceAsStream("/images/mother_nature.png")))));
        motherNature.setOpacity(0);
        islands.get(oldPosition).setMotherNature(false, motherNature);
        islands.get(oldPosition).setMotherNature(true, fakeMotherNature);
        islands.get(newPosition).setMotherNature(true, motherNature);
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
            /*islands.get(oldPosition).setMotherNature(false, motherNature);
            motherNature.setOpacity(0);
            islands.get(newPosition).setMotherNature(true, motherNature);
            motherNature.setTranslateX(lastPos.getFirst() - newPos.getFirst());
            motherNature.setTranslateY(lastPos.getSecond() - newPos.getSecond());*/
            islands.get(oldPosition).setMotherNature(false, fakeMotherNature);
            motherNature.setOpacity(1);
        });
    }

    /*public void computeMap() {
        double angleStep = 2 * Math.PI / islands.size();
        for (int i = 0; i < islands.size(); i++) {
            Pair<Double, Double> position = getEllipsePositionByAngle(angleStep * i, i < (islands.size() + 1)/ 2);
            AnchorPane.setLeftAnchor(islands.get(i), position.getFirst());
            AnchorPane.setTopAnchor(islands.get(i), position.getSecond());
            container.getChildren().add(islands.get(i));
        }
    }

    //angle must be between 0 and 2pi
    public Pair<Double, Double> getEllipsePositionByAngle(double angle, boolean invert) {
        double ellipseA = container.getWidth() / 2;
        double ellipseB = container.getHeight() / 2; //ellipse is x/a^2 + y/b^2 = 1
        double angularCoefficient = Math.tan(angle); //y = x * tan(angle)
        double xPosition = (ellipseA * ellipseB) /
                Math.pow(Math.pow(ellipseB, 2) + Math.pow(ellipseA * angularCoefficient, 2), 0.5);
        //xPosition = (angle > (3 * Math.PI) / 2 || angle < Math.PI / 2) ? xPosition : -xPosition;
        if (invert) xPosition = -xPosition;
        double yPosition = -xPosition * angularCoefficient;
        //y is inverted from the usual position because javafx has an opposite orientation for the y coordinate
        //ellipse is centered in the center of the container
        System.out.println("angle = " + angle + " position = (" + xPosition + "," + (-yPosition) + ")");
        return new Pair<>(xPosition + ellipseA, yPosition + ellipseB );
    }*/
}
