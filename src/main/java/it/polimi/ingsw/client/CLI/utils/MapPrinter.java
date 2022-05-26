package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.utils.Pair;

import java.util.Arrays;

public class MapPrinter {
    private String[][] islandsMap;
    private ModelView modelView;
    private String nickname;
    private final IslandMapPrinter islandMapPrinter = new IslandMapPrinter();
    private final SchoolMapPrinter schoolMapPrinter = new SchoolMapPrinter();
    private final CloudMapPrinter cloudMapPrinter = new CloudMapPrinter();
    private final CharacterMapPrinter characterMapPrinter = new CharacterMapPrinter();
    private Pair<Integer, Integer> islandsSectionTopLeftPosition;
    private Pair<Integer, Integer> islandsSectionBottomRightPosition;
    private Pair<Integer, Integer> cloudsSectionTopLeftPosition;
    private Pair<Integer, Integer> cloudsSectionBottomRightPosition;
    private Pair<Integer, Integer> schoolsSectionTopLeftPosition;
    private Pair<Integer, Integer> schoolsSectionBottomRightPosition;


    //TODO: add all to last map (for the moment i do separate maps)
    private String[][] schoolMap;
    private String[][] cloudsMap;
    private String[][] characterMap;

    public MapPrinter(int dimX, int dimY) {
        islandsMap = new String[dimX][dimY];
        for (String[] strings : islandsMap) {
            Arrays.fill(strings, " ");
        }
        //TODO: set dimensions of map parts
    }

    public void printMap() {
        int characterMapLength = (modelView.isExpert()) ? characterMap.length : 0;
        int maxDimensionX = Math.max(Math.max(islandsMap.length, cloudsMap.length), characterMapLength);
        //FIXME: try to eliminate if else
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
            /*printMatrixRow((i < islandsMap.length) ? islandsMap[i] : null, islandsMap[0].length, maxDimensionX, i);
            printMatrixRow((i < cloudsMap.length) ? cloudsMap[i] : null, cloudsMap[0].length, maxDimensionX, i);
            printMatrixRow((i < characterMap.length) ? characterMap[i] : null, characterMap[0].length, maxDimensionX, i);*/
            System.out.println();
        }

        //temporary
        for (int i = 0; i < schoolMap.length; i++) {
            for (int j = 0; j < schoolMap[i].length; j++) {
                System.out.print(schoolMap[i][j]);
            }
            System.out.println();
        }

        //temporary
        System.out.println("These are your assistants: " +
                Arrays.toString(modelView.getClientPlayerAssistants().keySet().toArray(new Integer[0])));
    }

    public static void printMatrixRow(String[] matrixRow, int rowLength, int matrixRowDim, int numRows) {
        if (matrixRowDim < numRows) {
            for (int i = 0; i < rowLength; i++) {
                System.out.print(matrixRow[i]);
            }
        } else {
            for (int i = 0; i < rowLength; i++) {
                System.out.print(" ");
            }
        }
    }

    @Deprecated
    public void replaceMapPart(Pair<Integer, Integer> startingCoordinates, Pair<Integer, Integer> finalCoordinates,
                               String[][] newMapPart) {
        int matrixDimX = finalCoordinates.getFirst() - startingCoordinates.getFirst();
        int matrixDimY = finalCoordinates.getSecond() - startingCoordinates.getSecond();
        for (int i = 0; i  < matrixDimX; i++) {
            for (int j = 0; j < matrixDimY; j++) {
                islandsMap[i + startingCoordinates.getFirst()][j + startingCoordinates.getSecond()] = newMapPart[i][j];
            }
        }
    }

    public void initializeMap(ModelView modelView, String nickname) {
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

    public void replaceIsland(int islandId) {
        islandMapPrinter.changeOnlyIsland(islandId);
        islandsMap = islandMapPrinter.getIslandMap();
    }

    public void recomputeIslandMap() {
        islandMapPrinter.initializeIslandMap();
        islandsMap = islandMapPrinter.getIslandMap();
    }

    public void replaceSchool(String player) {
        schoolMapPrinter.changeOnlySchoolOf(player);
        schoolMap = schoolMapPrinter.getSchoolMap();
    }

    public void recomputeSchoolMap() {
        schoolMapPrinter.initializeSchoolMap();
        schoolMap = schoolMapPrinter.getSchoolMap();
    }

    public void replaceCloud(int cloudId) {
        cloudMapPrinter.changeOnlyCloud(cloudId);
        cloudsMap = cloudMapPrinter.getCloudMap();
    }

    public void recomputeCloudMap() {
        cloudMapPrinter.initializeCloudMap();
        cloudsMap = cloudMapPrinter.getCloudMap();
    }

    public void replaceCharacter(int characterId) {
        characterMapPrinter.changeOnlyCharacter(characterId);
        characterMap = characterMapPrinter.getCharacterMap();
    }

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

    //The two matrix must have the same y dimension (or matrix can be 0 y-dimensioned)
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

    //toInsert must be smaller in dimensions than oldMatrix
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
