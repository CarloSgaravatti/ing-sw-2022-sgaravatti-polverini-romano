package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Triplet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CloudSubScene extends AnchorPane implements Initializable {
    private boolean ifFourStudents;


    public CloudSubScene(boolean isFourStudents) {
        this.ifFourStudents = isFourStudents;
        try {
            FXMLLoader loader;
            if(isFourStudents){
                loader = new FXMLLoader(getClass().getResource("/fxml/cloud.fxml"));
            }
            else loader = new FXMLLoader(getClass().getResource("/fxml/cloud.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initializeStudents(RealmType[] students) {
        List<Node> studentsNode = this.getChildren();
        for (int i = 0; i < studentsNode.size(); i++) {
            Circle circle = (Circle) studentsNode.get(i);
            this.getChildren().set(i, new StudentImage(circle));
            }
    }

    public void ResetStudentImage() {
        List <Node> studentsNode = this.getChildren();
        for(int i = 0; i < studentsNode.size(); i++){
            this.getChildren().get(i).setOpacity(0);
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(ifFourStudents) {
            super.getStyleClass().add("cloud-pane");
        }
        else super.getStyleClass().add("cloud-pane" + (new Random().nextInt(4) + 1));
    }


}

