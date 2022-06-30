package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.util.Map;

/**
 * CloudMapPrinter role is to manage all the cloud map components that are printed on the command line interface with the
 * help of PrintClouds instance.
 *
 * @see PrintClouds
 */
public class CloudMapPrinter {
    private FieldView fieldView;
    private String[][] cloudMap;
    //private final PrintClouds cloudPrinter = new PrintClouds();

    /**
     * Set the field view value used to create the clouds for the game
     *
     * @param fieldView the field view value used
     */
    public void setFieldView(FieldView fieldView) {
        this.fieldView = fieldView;
    }

    /**
     * getCloudMap return the map grid that contains all the clouds
     *
     * @return the map grid that contains all the clouds
     */
    public String[][] getCloudMap() {
        return cloudMap;
    }

    /**
     * (Re)Initializes the map grid of all the clouds
     */
    public void initializeCloudMap() {
        Map<Integer, Integer[]> clouds = fieldView.getCloudStudents();
        cloudMap = new String[0][0];
        for (Integer cloud: clouds.keySet()) {
            String[][] cloudPrint = getCloud(cloud);
            cloudMap = MapPrinter.appendMatrixInColumn(cloudPrint, cloudMap);
        }
    }

    /**
     * Change the cloud box that have the specific ID
     *
     * @param cloudId id of the cloud
     */
    public void changeOnlyCloud(int cloudId) {
        String[][] cloudPrint = getCloud(cloudId);
        int cloudPositionX = cloudId * PrintClouds.CLOUD_SIZE_X;
        cloudMap = MapPrinter.substituteSubMatrix(cloudMap, cloudPrint, new Pair<>(cloudPositionX, 0));
    }

    /**
     * getClouds return the cloud box with all its components inside with the help of PrintClouds
     *
     * @param cloudId id of the cloud
     * @return the cloud box with all its components inside
     * @see PrintClouds
     */
    private String[][] getCloud(int cloudId) {
        RealmType[] students = RealmType.getRealmsFromIntegerRepresentation(fieldView.getCloudStudents().get(cloudId));
        return PrintClouds.drawClouds(cloudId, students.length > 0, students);
    }
}
