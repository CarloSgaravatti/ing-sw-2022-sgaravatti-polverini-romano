package it.polimi.ingsw.client.GUI.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GenericBackgroundController extends AnchorPane implements Initializable {

    @FXML
    private AnchorPane containerCloud;

    @FXML
    private AnchorPane containerCloud1;

    @FXML
    private AnchorPane containerCloud2;

    @FXML
    private AnchorPane containerCloud3;

    @FXML
    private AnchorPane containerCloud4;

    @FXML
    private AnchorPane containerCloud5;

    @FXML
    private AnchorPane containerCloud6;

    @FXML
    private AnchorPane containerCloud7;

    @FXML
    private AnchorPane containerCloud8;

    @FXML
    private AnchorPane containerCloud9;

    public GenericBackgroundController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/genericBackground.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Node> images = containerCloud.getChildren();
        images.forEach(image-> {containerCloud.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud.setTranslateY(-(containerCloud.getHeight()/2));});

        images.get(1).setOpacity(0);
        images.get(2).setOpacity(0);
        images.get(3).setOpacity(0);
        images.get(4).setOpacity(0);

        List<Node> images1 = containerCloud1.getChildren();
        images1.forEach(image1-> {containerCloud1.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud1.setTranslateY(-(containerCloud.getHeight()/2)-200);});

        images1.get(1).setOpacity(0);
        images1.get(2).setOpacity(0);
        images1.get(3).setOpacity(0);
        images1.get(4).setOpacity(0);

        List<Node> images2 = containerCloud2.getChildren();
        images2.forEach(image2-> {containerCloud2.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud2.setTranslateY(+(containerCloud.getHeight()/2));});

        images2.get(1).setOpacity(0);
        images2.get(2).setOpacity(0);
        images2.get(3).setOpacity(0);
        images2.get(4).setOpacity(0);

        List<Node> images3 = containerCloud3.getChildren();
        images3.forEach(image3-> {containerCloud3.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud3.setTranslateY(-(containerCloud.getHeight()/2));});

        images3.get(1).setOpacity(0);
        images3.get(2).setOpacity(0);
        images3.get(3).setOpacity(0);
        images3.get(4).setOpacity(0);

        List<Node> images4 = containerCloud4.getChildren();
        images4.forEach(image4-> {containerCloud4.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud4.setTranslateY(-(containerCloud.getHeight()/2)-200);});

        images4.get(1).setOpacity(0);
        images4.get(2).setOpacity(0);
        images4.get(3).setOpacity(0);
        images4.get(4).setOpacity(0);

        List<Node> images5 = containerCloud5.getChildren();
        images5.forEach(image5-> {containerCloud5.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud5.setTranslateY(+(containerCloud.getHeight()/2));});

        images5.get(1).setOpacity(0);
        images5.get(2).setOpacity(0);
        images5.get(3).setOpacity(0);
        images5.get(4).setOpacity(0);

        List<Node> images6 = containerCloud6.getChildren();
        images6.forEach(image6-> {containerCloud6.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud6.setTranslateY(+(containerCloud.getHeight()/2) + 200);});

        images6.get(1).setOpacity(0);
        images6.get(2).setOpacity(0);
        images6.get(3).setOpacity(0);
        images6.get(4).setOpacity(0);

        List<Node> images7 = containerCloud7.getChildren();
        images7.forEach(image7-> {containerCloud7.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud7.setTranslateY(+(containerCloud.getHeight()/2) + 400);});

        images7.get(1).setOpacity(0);
        images7.get(2).setOpacity(0);
        images7.get(3).setOpacity(0);
        images7.get(4).setOpacity(0);

        List<Node> images8 = containerCloud8.getChildren();
        images8.forEach(image8-> {containerCloud8.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud8.setTranslateY(+(containerCloud.getHeight()/2) + 500);});

        images8.get(1).setOpacity(0);
        images8.get(2).setOpacity(0);
        images8.get(3).setOpacity(0);
        images8.get(4).setOpacity(0);

        List<Node> images9 = containerCloud9.getChildren();
        images9.forEach(image9-> {containerCloud9.setTranslateX(-containerCloud.getWidth()-500);
            containerCloud9.setTranslateY(+(containerCloud.getHeight()/2) + 500);});

        images9.get(1).setOpacity(0);
        images9.get(2).setOpacity(0);
        images9.get(3).setOpacity(0);
        images9.get(4).setOpacity(0);


        //transition containerCloud
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(containerCloud);
        translate.setDuration(Duration.millis(30000));
        translate.setCycleCount(TranslateTransition.INDEFINITE);
        translate.setByX(2100);
        translate.play();

        //transition containerCloud1
        TranslateTransition translate1 = new TranslateTransition();
        translate1.setNode(containerCloud1);
        translate1.setDelay(Duration.millis(4000));
        translate1.setDuration(Duration.millis(30000));
        translate1.setCycleCount(TranslateTransition.INDEFINITE);
        translate1.setByX(2100);
        translate1.play();

        //transition containerCloud2
        TranslateTransition translate2 = new TranslateTransition();
        translate2.setNode(containerCloud2);
        translate2.setDelay(Duration.millis(3000));
        translate2.setDuration(Duration.millis(30000));
        translate2.setCycleCount(TranslateTransition.INDEFINITE);
        translate2.setByX(2100);
        translate2.play();

        //transition containerCloud3
        TranslateTransition translate3 = new TranslateTransition();
        translate3.setNode(containerCloud3);
        translate3.setDelay(Duration.millis(10000));
        translate3.setDuration(Duration.millis(30000));
        translate3.setCycleCount(TranslateTransition.INDEFINITE);
        translate3.setByX(2100);
        translate3.play();

        //transition containerCloud4
        TranslateTransition translate4 = new TranslateTransition();
        translate4.setNode(containerCloud4);
        translate4.setDelay(Duration.millis(12000));
        translate4.setDuration(Duration.millis(30000));
        translate4.setCycleCount(TranslateTransition.INDEFINITE);
        translate4.setByX(2100);
        translate4.play();

        //transition containerCloud3
        TranslateTransition translate5 = new TranslateTransition();
        translate5.setNode(containerCloud5);
        translate5.setDelay(Duration.millis(25000));
        translate5.setDuration(Duration.millis(30000));
        translate5.setCycleCount(TranslateTransition.INDEFINITE);
        translate5.setByX(2100);
        translate5.play();

        //transition containerCloud6
        TranslateTransition translate6 = new TranslateTransition();
        translate6.setNode(containerCloud6);
        translate6.setDelay(Duration.millis(2000));
        translate6.setDuration(Duration.millis(30000));
        translate6.setCycleCount(TranslateTransition.INDEFINITE);
        translate6.setByX(2100);
        translate6.play();

        //transition containerCloud7
        TranslateTransition translate7 = new TranslateTransition();
        translate7.setNode(containerCloud7);
        translate7.setDelay(Duration.millis(2000));
        translate7.setDuration(Duration.millis(30000));
        translate7.setCycleCount(TranslateTransition.INDEFINITE);
        translate7.setByX(2100);
        translate7.play();

        //transition containerCloud8
        TranslateTransition translate8 = new TranslateTransition();
        translate8.setNode(containerCloud8);
        translate8.setDelay(Duration.millis(2000));
        translate8.setDuration(Duration.millis(30000));
        translate8.setCycleCount(TranslateTransition.INDEFINITE);
        translate8.setByX(2100);
        translate8.play();

        //transition containerCloud9
        TranslateTransition translate9 = new TranslateTransition();
        translate9.setNode(containerCloud9);
        translate9.setDelay(Duration.millis(2000));
        translate9.setDuration(Duration.millis(30000));
        translate9.setCycleCount(TranslateTransition.INDEFINITE);
        translate9.setByX(2100);
        translate9.play();

        FadeTransition fade = new FadeTransition();
        FadeTransition fade1 = new FadeTransition();
        FadeTransition fade2 = new FadeTransition();
        FadeTransition fade3 = new FadeTransition();
        FadeTransition fade4 = new FadeTransition();
        FadeTransition fade5 = new FadeTransition();
        FadeTransition fade6 = new FadeTransition();
        FadeTransition fade7 = new FadeTransition();
        FadeTransition fade8 = new FadeTransition();
        FadeTransition fade9 = new FadeTransition();

        fade.setNode(images.get(4));
        fade.setDelay(Duration.millis(5000));
        fade.setDuration(Duration.millis(5000));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setAutoReverse(true);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.play();

        fade1.setNode(images1.get(3));
        fade1.setDelay(Duration.millis(5000));
        fade1.setDuration(Duration.millis(4000));
        fade1.setFromValue(0);
        fade1.setToValue(1);
        fade1.setAutoReverse(true);
        fade1.setCycleCount(FadeTransition.INDEFINITE);
        fade1.play();

        fade2.setNode(images2.get(2));
        fade2.setDelay(Duration.millis(5000));
        fade2.setDuration(Duration.millis(4000));
        fade2.setFromValue(0);
        fade2.setToValue(1);
        fade2.setAutoReverse(true);
        fade2.setCycleCount(FadeTransition.INDEFINITE);
        fade2.play();

        fade3.setNode(images3.get(3));
        fade3.setDelay(Duration.millis(5000));
        fade3.setDuration(Duration.millis(4000));
        fade3.setFromValue(0);
        fade3.setToValue(1);
        fade3.setAutoReverse(true);
        fade3.setCycleCount(FadeTransition.INDEFINITE);
        fade3.play();

        fade4.setNode(images4.get(3));
        fade4.setDelay(Duration.millis(5000));
        fade4.setDuration(Duration.millis(4000));
        fade4.setFromValue(0);
        fade4.setToValue(1);
        fade4.setAutoReverse(true);
        fade4.setCycleCount(FadeTransition.INDEFINITE);
        fade4.play();

        fade5.setNode(images5.get(1));
        fade5.setDelay(Duration.millis(5000));
        fade5.setDuration(Duration.millis(4000));
        fade5.setFromValue(0);
        fade5.setToValue(1);
        fade5.setAutoReverse(true);
        fade5.setCycleCount(FadeTransition.INDEFINITE);
        fade5.play();

        fade6.setNode(images6.get(3));
        fade6.setDelay(Duration.millis(5000));
        fade6.setDuration(Duration.millis(4000));
        fade6.setFromValue(0);
        fade6.setToValue(1);
        fade6.setAutoReverse(true);
        fade6.setCycleCount(FadeTransition.INDEFINITE);
        fade6.play();

        fade7.setNode(images7.get(4));
        fade7.setDelay(Duration.millis(5000));
        fade7.setDuration(Duration.millis(4000));
        fade7.setFromValue(0);
        fade7.setToValue(1);
        fade7.setAutoReverse(true);
        fade7.setCycleCount(FadeTransition.INDEFINITE);
        fade7.play();

        fade8.setNode(images8.get(3));
        fade8.setDelay(Duration.millis(5000));
        fade8.setDuration(Duration.millis(4000));
        fade8.setFromValue(0);
        fade8.setToValue(1);
        fade8.setAutoReverse(true);
        fade8.setCycleCount(FadeTransition.INDEFINITE);
        fade8.play();

        fade9.setNode(images9.get(4));
        fade9.setDelay(Duration.millis(5000));
        fade9.setDuration(Duration.millis(4000));
        fade9.setFromValue(0);
        fade9.setToValue(1);
        fade9.setAutoReverse(true);
        fade9.setCycleCount(FadeTransition.INDEFINITE);
        fade9.play();

    }
}
