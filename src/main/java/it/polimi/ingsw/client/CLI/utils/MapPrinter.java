package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MapPrinter role is to put together all difference maps
 */
public class MapPrinter {
    private String[][] islandsMap;
    private ModelView modelView;
    private String nickname;
    private final IslandMapPrinter islandMapPrinter = new IslandMapPrinter();
    private final SchoolMapPrinter schoolMapPrinter = new SchoolMapPrinter();
    private final CloudMapPrinter cloudMapPrinter = new CloudMapPrinter();
    private final CharacterMapPrinter characterMapPrinter = new CharacterMapPrinter();
    private String[][] schoolMap;
    private String[][] cloudsMap;
    private String[][] characterMap;

    /**
     * Initialize the space of the map containing all different maps with spaces
     *
     * @param dimX x axe dimension of the final map
     * @param dimY y axe dimension of the final map
     */
    public MapPrinter(int dimX, int dimY) {
        islandsMap = new String[dimX][dimY];
        for (String[] strings : islandsMap) {
            Arrays.fill(strings, " ");
        }
    }

    /**
     * printMap print the complete map of all the game
     */
    public synchronized void printMap() {
        int characterMapLength = (modelView.isExpert()) ? characterMap.length : 0;
        int maxDimensionX = Math.max(Math.max(islandsMap.length, cloudsMap.length), characterMapLength);
        for (int i = 0; i < maxDimensionX; i++) {
            if (i < islandsMap.length) {
                for (int j = 0; j < islandsMap[i].length; j++) {
                    System.out.print(islandsMap[i][j]);
                }
            } else {
                for (int j = 0; j < islandsMap[0].length; j++) {
                    System.out.print(" ");
                }
            }
            if (i < cloudsMap.length) {
                for (int j = 0; j < cloudsMap[i].length; j++) {
                    System.out.print(cloudsMap[i][j]);
                }
            } else {
                for (int j = 0; j < cloudsMap[0].length; j++) {
                    System.out.print(" ");
                }
            }
            if (modelView.isExpert() && i < characterMap.length) {
                for (int j = 0; j < characterMap[i].length; j++) {
                    System.out.print(characterMap[i][j]);
                }
            } else if (modelView.isExpert()) {
                for (int j = 0; j < characterMap[0].length; j++) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        for (int i = 0; i < schoolMap.length; i++) {
            for (int j = 0; j < schoolMap[i].length; j++) {
                System.out.print(schoolMap[i][j]);
            }
            System.out.println();
        }
        if (modelView.getClientPlayerAssistants().isEmpty()) return;
        StringBuilder assistants = new StringBuilder("These are your assistants: [");
        List<Integer> assistantValues = new ArrayList<>(modelView.getClientPlayerAssistants().keySet());
        for (int i = 0; i < assistantValues.size() - 1; i++) {
            assistants.append("(").append(assistantValues.get(i)).append(",")
                    .append(modelView.getClientPlayerAssistants().get(assistantValues.get(i))).append("),");
        }
        assistants.append("(").append(assistantValues.get(assistantValues.size() - 1)).append(",")
                .append(modelView.getClientPlayerAssistants().get(assistantValues.get(assistantValues.size() - 1))).append(")]");
        System.out.println(assistants);
    }

    /**
     * Initialize the complete map by the specific value of ModelView
     *
     * @param modelView Model view used by initializeMap
     * @param nickname nickname of the player of this map
     */
    public synchronized void initializeMap(ModelView modelView, String nickname) {
        this.modelView = modelView;
        this.nickname = nickname;
        islandMapPrinter.setFieldView(modelView.getField());
        recomputeIslandMap();
        schoolMapPrinter.setModelView(modelView);
        schoolMapPrinter.initializePlayersSchoolOrder(nickname);
        recomputeSchoolMap();
        cloudMapPrinter.setFieldView(modelView.getField());
        recomputeCloudMap();
        if (modelView.isExpert()) {
            characterMapPrinter.setExpertField(modelView.getField().getExpertField());
            characterMapPrinter.initializeCharacterMap();
            characterMap = characterMapPrinter.getCharacterMap();
        }
    }

    /**
     * Replace island in the map with another one by a specific id
     *
     * @param islandId index of the island to change
     */
    public synchronized void replaceIsland(int islandId) {
        islandMapPrinter.changeOnlyIsland(islandId);
        islandsMap = islandMapPrinter.getIslandMap();
    }

    /**
     * Recompute all the island map
     */
    public synchronized void recomputeIslandMap() {
        islandMapPrinter.initializeIslandMap();
        islandsMap = islandMapPrinter.getIslandMap();
    }

    /**
     * Replace school in the map with another one by a specific id
     *
     * @param player owner of the school
     */
    public synchronized void replaceSchool(String player) {
        schoolMapPrinter.changeOnlySchoolOf(player);
        schoolMap = schoolMapPrinter.getSchoolMap();
    }

    /**
     * Recompute all the school map
     */
    public synchronized void recomputeSchoolMap() {
        schoolMapPrinter.initializeSchoolMap();
        schoolMap = schoolMapPrinter.getSchoolMap();
    }

    /**
     * Replace a cloud in the map with another one by a specific id
     *
     * @param cloudId index of the cloud to replace
     */
    public synchronized void replaceCloud(int cloudId) {
        cloudMapPrinter.changeOnlyCloud(cloudId);
        cloudsMap = cloudMapPrinter.getCloudMap();
    }

    /**
     * Recompute all the cloud map
     */
    public synchronized void recomputeCloudMap() {
        cloudMapPrinter.initializeCloudMap();
        cloudsMap = cloudMapPrinter.getCloudMap();
    }

    /**
     * Replace a character in character map with another one by a specific id
     *
     * @param characterId index of the character to replace
     */
    public synchronized void replaceCharacter(int characterId) {
        characterMapPrinter.changeOnlyCharacter(characterId);
        characterMap = characterMapPrinter.getCharacterMap();
    }

    /**
     * appendMatrixInLine merge together 2 matrix in line
     *
     * @param toAppend matrix to append to the main matrix
     * @param matrix main matrix
     * @return new main matrix with another matrix merged in it in line
     */
    public static String[][] appendMatrixInLine(String[][] toAppend, String[][] matrix) {
        int matrixDimX = toAppend.length;
        int matrixDimY = (matrix.length == 0) ? 0 : matrix[0].length;
        int newMatrixDimY = matrixDimY + toAppend[0].length;
        String[][] newMatrix = new String[matrixDimX][newMatrixDimY];

        for (int i = 0; i < matrixDimX; i++) {
            for (int j = 0; j < matrixDimY; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }
        for (int i = 0; i < matrixDimX; i++) {
            for (int j = 0; j < toAppend[0].length; j++) {
                newMatrix[i][j + matrixDimY] = toAppend[i][j];
            }
        }
        return newMatrix;
    }

    /**
     * appendMatrixInColumn merge together 2 matrix in column if the two matrix have the same
     * Y-dimension (or matrix can be 0 y-dimensioned)
     *
     * @param toAppend matrix to append to the main matrix
     * @param matrix main matrix
     * @return new main matrix with another matrix merged in it in column
     */
    public static String[][] appendMatrixInColumn(String[][] toAppend, String[][] matrix) {
        int matrixDimX = matrix.length;
        int toAppendDimX = toAppend.length;
        int matrixDimY = (matrixDimX == 0) ? toAppend[0].length : matrix[0].length;
        String[][] newMatrix = new String[matrixDimX + toAppendDimX][matrixDimY];

        for (int i = 0; i < matrixDimX; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }
        for (int i = 0; i < toAppendDimX; i++) {
            for (int j = 0; j < toAppend[i].length; j++) {
                newMatrix[i + matrixDimX][j] = toAppend[i][j];
            }
        }
        return newMatrix;
    }

    /**
     * Substitute a sub matrix int the specified position
     *
     * @param oldMatrix main matrix
     * @param toInsert new sub matrix
     * @param startingPos starting position of the new sub matrix
     * @return new main matrix with substituted sub matrix in it
     */
    public static String[][] substituteSubMatrix(String[][] oldMatrix, String[][] toInsert, Pair<Integer, Integer> startingPos) {
        String[][] newMatrix = oldMatrix;
        for (int i = 0; i < toInsert.length; i++) {
            for (int j = 0; j < toInsert[i].length; j++) {
                newMatrix[i + startingPos.getFirst()][j + startingPos.getSecond()] = toInsert[i][j];
            }
        }
        return newMatrix;
    }
}
