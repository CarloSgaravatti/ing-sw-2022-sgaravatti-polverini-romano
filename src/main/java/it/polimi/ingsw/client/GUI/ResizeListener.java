package it.polimi.ingsw.client.GUI;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.List;

public class ResizeListener {
    private final Stage stage;
    private Scene scene;
    private double actualScaleX = 1.0;
    private double actualScaleY = 1.0;
    private double lastRelativeScaleX;
    private double lastRelativeScaleY;
    private ChangeListener<? super Number> stageWidthListener;
    private ChangeListener<? super Number> stageHeightListener;
    private final double sceneRatio;
    private double initialWidth;
    private double initialHeight;

    public ResizeListener(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        sceneRatio = scene.widthProperty().get() / scene.heightProperty().get();
        /*stage.minWidthProperty().bind(scene.heightProperty().multiply(sceneRatio));
        stage.minHeightProperty().bind(scene.widthProperty().divide(sceneRatio));*/
        initialWidth = stage.getWidth();
        initialHeight = stage.getHeight();
    }

    public void registerHandlers() {
        stageHeightListener = (observable, oldValue, newValue) -> {
            /*stage.widthProperty().removeListener(stageWidthListener);
            double lastWidth = stage.getWidth();
            stage.setWidth(newValue.doubleValue() * sceneRatio);*/
            //double relativeScale = newValue.doubleValue() / oldValue.doubleValue();
            //actualScaleX *= relativeScale;
            //lastRelativeScaleX = relativeScale;
            //actualScaleY *= (stage.getWidth() / lastWidth);
            //System.out.println(relativeScale + " " + actualScaleX);
            //stage.widthProperty().addListener(stageWidthListener);
            Parent parent = scene.getRoot();
            //resizeScene(parent, parent.getChildrenUnmodifiable());
            resizeNewYValues(parent, parent.getChildrenUnmodifiable(), newValue.doubleValue());
        };
        stageWidthListener = (observable, oldValue, newValue) -> {
            /*stage.heightProperty().removeListener(stageHeightListener);
            double lastHeight = stage.getHeight();
            stage.setHeight(newValue.doubleValue() / sceneRatio);*/
            //double relativeScale = newValue.doubleValue() / oldValue.doubleValue();
            //actualScaleY *= relativeScale;
            //lastRelativeScaleY = relativeScale;
            /*actualScaleX *= stage.getHeight() / lastHeight;
            stage.heightProperty().addListener(stageHeightListener);*/
            Parent parent = scene.getRoot();
            //resizeScene(parent, parent.getChildrenUnmodifiable());
            resizeNewXValues(parent, parent.getChildrenUnmodifiable(), newValue.doubleValue());
        };
        stage.widthProperty().addListener(stageWidthListener);
        stage.heightProperty().addListener(stageHeightListener);
    }
    
    public void setScene(Scene scene) {
        this.scene = scene;
        initialWidth = stage.getWidth();
        initialHeight = stage.getHeight();
    }

    private void resizeScene(Parent parent, List<Node> children) {
        parent.setScaleY(actualScaleX);
        parent.setScaleX(actualScaleY);
        children.forEach(node -> {
            if (node instanceof ImageView imageView) {
                double newHeight = imageView.getFitHeight() * lastRelativeScaleY;
                imageView.setFitHeight(newHeight);
                if (!imageView.preserveRatioProperty().get()) {
                    double newWidth = imageView.getFitWidth() * lastRelativeScaleX;
                    imageView.setFitWidth(newWidth);
                }
            } else if (node instanceof Parent parentNode) {
                resizeScene(parentNode, parentNode.getChildrenUnmodifiable());
            }
        });
        /*Scale scale = new Scale(lastRelativeScaleX, lastRelativeScaleY, parent.getLayoutX(), parent.getLayoutY());
        parent.getTransforms().add(scale);*/
        /*parent.setScaleX(actualScaleX);
        parent.setScaleY(actualScaleY);*/
    }

    private void resizeNewYValues(Parent parent, List<Node> children, double newHeight) {
        boolean isLayoutModifiable = ! (parent instanceof HBox || parent instanceof VBox || parent instanceof BorderPane);
        parent.setScaleX(newHeight / initialHeight);
        children.forEach(node -> {
            if (isLayoutModifiable) {
                double initialLayoutPercent = node.getLayoutY() / initialHeight;
                double finalPosition = initialLayoutPercent * newHeight;
                node.setTranslateY(finalPosition - node.getLayoutY());
            }
            if (node instanceof ImageView imageView) {
                imageView.setFitHeight(imageView.getFitHeight() * newHeight / initialHeight);
                if (!imageView.preserveRatioProperty().get()) {
                    imageView.setFitWidth(imageView.getFitWidth() * newHeight / initialHeight);
                }
            } else if (node instanceof Parent parentNode){
                resizeNewYValues(parentNode, parentNode.getChildrenUnmodifiable(), newHeight);
            }
        });
    }

    private void resizeNewXValues(Parent parent, List<Node> children, double newWidth) {
        boolean isLayoutModifiable = ! (parent instanceof HBox || parent instanceof VBox || parent instanceof BorderPane);
        parent.setScaleY(newWidth / initialWidth);
        children.forEach(node -> {
            if (isLayoutModifiable) {
                double initialLayoutPercent = node.getLayoutX() / initialWidth;
                double finalPosition = initialLayoutPercent * newWidth;
                node.setTranslateX(finalPosition - node.getLayoutX());
            }
            if (node instanceof ImageView imageView) {
                imageView.setFitWidth(imageView.getFitWidth() * newWidth / initialWidth);
                if (!imageView.preserveRatioProperty().get()) {
                    imageView.setFitHeight(imageView.getFitHeight() * newWidth / initialWidth);
                }
            } else if (node instanceof Parent parentNode){
                resizeNewXValues(parentNode, parentNode.getChildrenUnmodifiable(), newWidth);
            }
        });
    }
}
