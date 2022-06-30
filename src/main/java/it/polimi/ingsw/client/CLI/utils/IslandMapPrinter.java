package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;

import java.util.Arrays;

import static it.polimi.ingsw.client.CLI.utils.MapPrinter.*;

/**
 * IslandMapPrinter role is to manage all the island map components that are printed on the command line interface with the
 * help of a IslandPrinter instance.
 * @see IslandPrinter
 */
public class IslandMapPrinter {
    private FieldView fieldView;
    private final IslandPrinter islandPrinter = new IslandPrinter();
    private String[][] islandMap;
    boolean isWithNoEntryTiles;

    /**
     * Sets the value of the specified FieldView to this
     *
     * @param fieldView the field view of the game
     */
    public void setFieldView(FieldView fieldView) {
        this.fieldView = fieldView;
        isWithNoEntryTiles = fieldView.getExpertField() != null;
    }

    /**
     * (Re)Initialize the island map grid in base of the data that is contained on the field view
     */
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

    /**
     * addAdditionalSpaces substitute a unified island with spaces.
     * Add spaces to an island line when an island is deleted after unification with another one
     *
     * @param previousLengthY previous dimension of the line of islands
     * @param matrix the line of islands that have to get the additional spaces
     * @return matrix with additional spaces
     */
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

    /**
     * Returns the island map grid that will be printed on screen
     *
     * @return the island map grid
     */
    public String[][] getIslandMap() {
        return islandMap;
    }

    /**
     * Modify the island map grid by substituting only the island of the specified id
     *
     * @param islandId the id of the island
     */
    public void changeOnlyIsland(int islandId) {
        int position = fieldView.getMotherNaturePosition();
        Pair<Integer, Integer> islandPosition = getIslandPositionInMap(islandId);
        Triplet<Integer[], Integer, TowerType> island = fieldView.getIsland(islandId);
        int numNoEntryTiles = getNoEntryTilesValue(islandId);
        String[][] islandDrawn = islandPrinter.getIsland(islandId, position == islandId, island.getSecond(),
                island.getThird(), island.getFirst(), numNoEntryTiles);
        islandMap = substituteSubMatrix(islandMap, islandDrawn, islandPosition);
    }

    /**
     * Upgrade no_entry_tile value of the island of the specified id
     *
     * @param islandId the id of the island
     * @return island with no_entry_tile value upgraded
     */
    private int getNoEntryTilesValue(int islandId) {
        return (isWithNoEntryTiles) ? ((fieldView.getExpertField().getNoEntryTilesOnIsland(islandId) == null) ? 0 :
                fieldView.getExpertField().getNoEntryTilesOnIsland(islandId)) : 0;
    }

    /**
     * getIslandPositionInMap get the position of the island with the specified id between all islands of the map
     *
     * @param islandId the id of the island
     * @return the position of the island with the specified id
     */
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
