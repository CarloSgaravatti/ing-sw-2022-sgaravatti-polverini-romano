package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.util.List;
import java.util.Map;

public class CloudMapPrinter {
    private FieldView fieldView;
    private String[][] cloudMap;
    private final PrintClouds cloudPrinter = new PrintClouds();

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
            RealmType[] students = RealmType.getRealmsFromIntegerRepresentation(clouds.get(cloud));
            //TODO: need to change print cloud signature because client doesn't have Student class (but only RealmType)
            //String[][] cloudPrint = PrintClouds.drawClouds(cloud, students.length > 0, students);
            //cloudMap = MapPrinter.appendMatrixInColumn(cloudPrint, cloudMap);
        }
    }

    public void changeOnlyCloud(int cloudId) {

    }
}
