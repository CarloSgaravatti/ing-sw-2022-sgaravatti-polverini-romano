package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;

import static it.polimi.ingsw.client.CLI.utils.MapPrinter.*;

public class IslandMapPrinter {
    private FieldView fieldView;
    private final IslandPrinter islandPrinter = new IslandPrinter();
    private String[][] islandMap;

    public void setFieldView(FieldView fieldView) {
        this.fieldView = fieldView;
    }

    public void initializeIslandMap() {
        int numIslands = fieldView.getIslandSize();
        String[][] firstLine = new String[0][0];
        String[][] secondLine = new String[0][0];
        for (int i = 0; i < (numIslands + 1) / 2; i++) {
            Triplet<Integer[], Integer, TowerType> islandValues = fieldView.getIsland(i);
            String[][] island = islandPrinter.getIsland(i, islandValues.getSecond(), islandValues.getThird(), islandValues.getFirst());
            firstLine = appendMatrixInLine(island, firstLine);

            int island2Idx = numIslands - i - 1;
            Triplet<Integer[], Integer, TowerType> island2Values = fieldView.getIsland(island2Idx);
            String[][] island2 = islandPrinter.getIsland(island2Idx, island2Values.getSecond(), island2Values.getThird(), island2Values.getFirst());
            secondLine = appendMatrixInLine(island2, secondLine);
        }
        islandMap = firstLine;
        islandMap = appendMatrixInColumn(secondLine, islandMap);
    }

    public String[][] getIslandMap() {
        return islandMap;
    }

    public void changeOnlyIsland(int islandId) {
        Pair<Integer, Integer> islandPosition = getIslandPositionInMap(islandId);
        Triplet<Integer[], Integer, TowerType> island = fieldView.getIsland(islandId);
        String[][] islandDrawn = islandPrinter.getIsland(islandId, island.getSecond(), island.getThird(), island.getFirst());
        substituteSubMatrix(islandMap, islandDrawn, islandPosition);
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
