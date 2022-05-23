package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.util.Map;

public class CloudMapPrinter {
    private FieldView fieldView;
    private String[][] cloudMap;
    //private final PrintClouds cloudPrinter = new PrintClouds();

    public void setFieldView(FieldView fieldView) {
        this.fieldView = fieldView;
    }

    public String[][] getCloudMap() {
        return cloudMap;
    }

    public void initializeCloudMap() {
        Map<Integer, Integer[]> clouds = fieldView.getCloudStudents();
        cloudMap = new String[0][0];
        for (Integer cloud: clouds.keySet()) {
            String[][] cloudPrint = getCloud(cloud);
            cloudMap = MapPrinter.appendMatrixInColumn(cloudPrint, cloudMap);
        }
    }

    public void changeOnlyCloud(int cloudId) {
        String[][] cloudPrint = getCloud(cloudId);
        int cloudPositionY = cloudId * PrintClouds.CLOUD_SIZE_Y;
        cloudMap = MapPrinter.substituteSubMatrix(cloudMap, cloudPrint, new Pair<>(0, cloudPositionY));
    }

    private String[][] getCloud(int cloudId) {
        //TODO: need to change print cloud signature because client doesn't have Student class (but only RealmType)
        RealmType[] students = RealmType.getRealmsFromIntegerRepresentation(fieldView.getCloudStudents().get(cloudId));
        return PrintClouds.drawClouds(cloudId, students.length > 0, students);
    }
}
