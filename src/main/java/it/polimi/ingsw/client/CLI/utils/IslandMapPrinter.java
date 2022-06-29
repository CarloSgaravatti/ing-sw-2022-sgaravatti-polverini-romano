package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;

import java.util.Arrays;

import static it.polimi.ingsw.client.CLI.utils.MapPrinter.*;

public class IslandMapPrinter {
    private FieldView fieldView;
    private final IslandPrinter islandPrinter = new IslandPrinter();
    private String[][] islandMap;
    boolean isWithNoEntryTiles;

    public void setFieldView(FieldView fieldView) {
        this.fieldView = fieldView;
        isWithNoEntryTiles = fieldView.getExpertField() != null;
    }

    public void initializeIslandMap() {
        int numIslands = fieldView.getIslandSize();
        int previousLengthY = (islandMap != null) ? islandMap[0].length : 0;
        String[][] firstLine = new String[0][0];
        String[][] secondLine = new String[0][0];
        int position = fieldView.getMotherNaturePosition();
        for (int i = 0; i < (numIslands + 1) / 2; i++) {
            Triplet<Integer[], Integer, TowerType> islandValues = fieldView.getIsland(i);
            int numNoEntryTiles = getNoEntryTilesValue(i);
            String[][] island = islandPrinter.getIsland(i, position == i, islandValues.getSecond(),
                    islandValues.getThird(), islandValues.getFirst(), numNoEntryTiles);
            firstLine = appendMatrixInLine(island, firstLine);
            //If the islands are odd, first line will have more islands than second line (so second line will have
            //an empty space instead of the island, this will be on the right side of the second line)
            if (i < numIslands / 2) {
                int island2Idx = numIslands - i - 1;
                numNoEntryTiles = getNoEntryTilesValue(island2Idx);
                Triplet<Integer[], Integer, TowerType> island2Values = fieldView.getIsland(island2Idx);
                String[][] island2 = islandPrinter.getIsland(island2Idx, position == island2Idx, island2Values.getSecond(),
                        island2Values.getThird(), island2Values.getFirst(), numNoEntryTiles);
                secondLine = appendMatrixInLine(island2, secondLine);
            }
        }
        firstLine = addAdditionalSpaces(previousLengthY, firstLine);
        secondLine = addAdditionalSpaces((previousLengthY == 0) ? firstLine[0].length : previousLengthY, secondLine);
        islandMap = firstLine;
        islandMap = appendMatrixInColumn(secondLine, islandMap);
    }

    private String[][] addAdditionalSpaces(int previousLengthY, String[][] matrix) {
        if (matrix[0].length < previousLengthY) {
            String[][] spacesMatrix = new String[matrix.length][previousLengthY - matrix[0].length];
            for (String[] strings: spacesMatrix) {
                Arrays.fill(strings, " ");
            }
            matrix = appendMatrixInLine(spacesMatrix, matrix);
        }
        return matrix;
    }

    public String[][] getIslandMap() {
        return islandMap;
    }

    public void changeOnlyIsland(int islandId) {
        int position = fieldView.getMotherNaturePosition();
        Pair<Integer, Integer> islandPosition = getIslandPositionInMap(islandId);
        Triplet<Integer[], Integer, TowerType> island = fieldView.getIsland(islandId);
        int numNoEntryTiles = getNoEntryTilesValue(islandId);
        String[][] islandDrawn = islandPrinter.getIsland(islandId, position == islandId, island.getSecond(),
                island.getThird(), island.getFirst(), numNoEntryTiles);
        islandMap = substituteSubMatrix(islandMap, islandDrawn, islandPosition);
    }

    private int getNoEntryTilesValue(int islandId) {
        return (isWithNoEntryTiles) ? ((fieldView.getExpertField().getNoEntryTilesOnIsland(islandId) == null) ? 0 :
                fieldView.getExpertField().getNoEntryTilesOnIsland(islandId)) : 0;
    }

    private Pair<Integer, Integer> getIslandPositionInMap(int islandId) {
        int numIslands = fieldView.getIslandSize();
        int positionX;
        int positionY;
        boolean isInFirstLine = islandId < (numIslands + 1) / 2;
        if (isInFirstLine) {
            positionX = 0;
            positionY = IslandPrinter.ISLAND_SIZE_Y * islandId;
        } else {
            positionX = IslandPrinter.ISLAND_SIZE_X;
            positionY = IslandPrinter.ISLAND_SIZE_Y * (numIslands - 1 - islandId);
        }
        return new Pair<>(positionX, positionY);
    }
}
