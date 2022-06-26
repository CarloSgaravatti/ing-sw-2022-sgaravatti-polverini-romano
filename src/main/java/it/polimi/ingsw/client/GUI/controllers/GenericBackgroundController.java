package it.polimi.ingsw.client.GUI.controllers;

import it.polimi.ingsw.controller.InitController;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GenericBackgroundController implements Initializable {

    @FXML
    private AnchorPane containerCloud;

    @FXML
    private AnchorPane containerCloud1;

    @FXML
    private AnchorPane containerCloud2;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Node> images = containerCloud.getChildren();
        images.forEach(image-> {containerCloud.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud.setTranslateY(-(containerCloud.getHeight()/2));});

        images.get(1).setOpacity(0);
        images.get(2).setOpacity(0);
        images.get(3).setOpacity(0);
        images.get(4).setOpacity(0);


        //transition
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(containerCloud);
        translate.setDuration(Duration.millis(30000));
        translate.setCycleCount(TranslateTransition.INDEFINITE);
        translate.setByX(2500);
        translate.play();

        FadeTransition fade = new FadeTransition();
        FadeTransition fade1 = new FadeTransition();
        FadeTransition fade2 = new FadeTransition();
        FadeTransition fade3 = new FadeTransition();
        FadeTransition fade4 = new FadeTransition();
        FadeTransition fade5 = new FadeTransition();
        FadeTransition fade6 = new FadeTransition();
        FadeTransition fade7 = new FadeTransition();
        fade.setNode(images.get(4));
        fade.setDelay(Duration.millis(5000));
        fade.setDuration(Duration.millis(5000));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setAutoReverse(true);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.play();

    }
}
