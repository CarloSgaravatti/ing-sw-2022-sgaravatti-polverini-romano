package it.polimi.ingsw.client.GUI.items;

import it.polimi.ingsw.model.enumerations.RealmType;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CloudSubScene extends AnchorPane implements Initializable {
    private final boolean ifFourStudents;
    private final int cloudId;


    public CloudSubScene(boolean isFourStudents, int cloudId) {
        this.ifFourStudents = isFourStudents;
        this.cloudId = cloudId;
        try {
            FXMLLoader loader;
            if(isFourStudents){
                loader = new FXMLLoader(getClass().getResource("/fxml/cloud4.fxml"));
            }
            else loader = new FXMLLoader(getClass().getResource("/fxml/cloud.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method initializeStudents inserts 3 or 4 students in the cloud
     *
     * @param students array of students that will be inserted in the clouds
     */
    public void initializeStudents(RealmType[] students) {
        List<Node> studentsNode = this.getChildren();
        for (int i = 0; i < studentsNode.size(); i++) {
            Circle circle = (Circle) studentsNode.get(i);
            StudentImage studentImage = new StudentImage(circle);
            if(students.length > i) studentImage.setStudent(students[i]);
            this.getChildren().set(i, studentImage);

        }
    }

    /**
     * method ResetStudentImage remove all the students from the cloud after a cloud choice by the player
     */
    public void ResetStudentImage() {
        List <Node> studentsNode = this.getChildren();
        for(int i = 0; i < studentsNode.size(); i++){
            this.getChildren().get(i).setOpacity(0);
        }

    }

    /**
     * method initialize inserts random clouds in the scene
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(ifFourStudents) {
            super.getStyleClass().add("cloud-pane");
        }
        else super.getStyleClass().add("cloud-pane" + (new Random().nextInt(4) + 1));
    }

    public int getCloudId() {
        return cloudId;
    }
}

