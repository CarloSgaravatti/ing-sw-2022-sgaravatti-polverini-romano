package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;

import java.util.Arrays;

public class MapPrinter {
    private String[][] lastMap;
    private ModelView modelView;
    private final IslandPrinter islandPrinter = new IslandPrinter();
    private Pair<Integer, Integer> islandsSectionTopLeftPosition;
    private Pair<Integer, Integer> islandsSectionBottomRightPosition;
    private Pair<Integer, Integer> cloudsSectionTopLeftPosition;
    private Pair<Integer, Integer> cloudsSectionBottomRightPosition;
    private Pair<Integer, Integer> schoolsSectionTopLeftPosition;
    private Pair<Integer, Integer> schoolsSectionBottomRightPosition;

    public MapPrinter(int dimX, int dimY) {
        lastMap = new String[dimX][dimY];
        for (String[] strings : lastMap) {
            Arrays.fill(strings, " ");
        }
        //TODO: set dimensions of map parts
    }

    public void printMap() {
        for (int i = 0; i < lastMap.length; i++) {
            for (int j = 0; j < lastMap[i].length; j++) {
                System.out.print(lastMap[i][j]);
            }
            System.out.println();
        }
    }

    public void replaceMapPart(Pair<Integer, Integer> startingCoordinates, Pair<Integer, Integer> finalCoordinates,
                               String[][] newMapPart) {
        int matrixDimX = finalCoordinates.getFirst() - startingCoordinates.getFirst();
        int matrixDimY = finalCoordinates.getSecond() - startingCoordinates.getSecond();
        for (int i = 0; i  < matrixDimX; i++) {
            for (int j = 0; j < matrixDimY; j++) {
                lastMap[i + startingCoordinates.getFirst()][j + startingCoordinates.getSecond()] = newMapPart[i][j];
            }
        }
    }

    public void initializeMap(ModelView modelView) {
        this.modelView = modelView;
        int numIslands = modelView.getField().getIslandSize();
        String[][] firstLine = new String[0][0];
        String[][] secondLine = new String[0][0];
        for (int i = 0; i < (numIslands + 1) / 2; i++) {
            Triplet<Integer[], Integer, TowerType> islandValues = modelView.getField().getIsland(i);
            String[][] island = islandPrinter.getIsland(i, islandValues.getSecond(), islandValues.getThird(), islandValues.getFirst());
            firstLine = appendMatrixInLine(island, firstLine);

            int island2Idx = numIslands - i - 1;
            Triplet<Integer[], Integer, TowerType> island2Values = modelView.getField().getIsland(island2Idx);
            String[][] island2 = islandPrinter.getIsland(island2Idx, island2Values.getSecond(), island2Values.getThird(), island2Values.getFirst());
            secondLine = appendMatrixInLine(island2, secondLine);
        }
        lastMap = firstLine;
        lastMap = appendMatrixInColumn(secondLine, lastMap);
    }


    private String[][] appendMatrixInLine(String[][] toAppend, String[][] matrix) {
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

    //The two matrix must have the same y dimension
    private String[][] appendMatrixInColumn(String[][] toAppend, String[][] matrix) {
        int matrixDimX = matrix.length;
        int toAppendDimX = toAppend.length;
        int matrixDimY = matrix[0].length;
        int newMatrixDimX = matrixDimX + toAppendDimX;
        String[][] newMatrix = new String[newMatrixDimX][matrixDimY];

        for (int i = 0; i < matrixDimX; i++) {
            for (int j = 0; j < matrixDimY; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }
        for (int i = 0; i < matrixDimX; i++) {
            for (int j = 0; j < matrixDimY; j++) {
                newMatrix[i + matrixDimX][j] = toAppend[i][j];
            }
        }
        return newMatrix;
    }
}
