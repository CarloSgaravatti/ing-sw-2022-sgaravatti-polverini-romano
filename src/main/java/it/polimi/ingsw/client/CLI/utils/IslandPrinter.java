package it.polimi.ingsw.client.CLI.utils;


import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triplet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IslandPrinter {
    private final String[][] islandSkeleton;
    private static final Pair<Integer, Integer> ISLAND_INDEX_POSITION = new Pair<>(0, 7);
    private static final Pair<Integer, Integer> TOWER_POSITION = new Pair<>(4, 2);
    private static final Map<TowerType, Pair<UnicodeConstants, Colors>> towersUnicode =
            Map.of(TowerType.BLACK, new Pair<>(UnicodeConstants.BLACK_TOWER, Colors.BLACK),
                    TowerType.WHITE, new Pair<>(UnicodeConstants.WHITE_TOWER, Colors.WHITE),
                    TowerType.GREY, new Pair<>(UnicodeConstants.GREY_TOWER, Colors.CYAN));
    private static final Map<RealmType, Triplet<Integer, Integer, Colors>> STUDENT_COLOR_POSITION =
            Map.of(RealmType.YELLOW_GNOMES, new Triplet<>(2, 4, Colors.YELLOW),
                    RealmType.BLUE_UNICORNS, new Triplet<>(2, 8, Colors.BLUE),
                    RealmType.GREEN_FROGS, new Triplet<>(3, 6, Colors.GREEN),
                    RealmType.RED_DRAGONS, new Triplet<>(4, 10, Colors.RED),
                    RealmType.PINK_FAIRES, new Triplet<>(5, 8, Colors.PURPLE));
    private static final List<Pair<Integer, Integer>> VERTICAL_POSITIONS =
            List.of(new Pair<>(2, 2), new Pair<>(3, 14), new Pair<>(4, 0),
                    new Pair<>(4, 14), new Pair<>(5, 5));
    private static final List<Pair<Integer, Integer>> BOTTOM_RIGHT_POSITIONS =
            List.of(new Pair<>(1, 4), new Pair<>(3, 2), new Pair<>(5, 4),
                    new Pair<>(5, 14), new Pair<>(6, 13));
    private static final List<Pair<Integer, Integer>> BOTTOM_LEFT_POSITIONS =
            List.of(new Pair<>(5, 0), new Pair<>(6, 5), new Pair<>(1, 11), new Pair<>(2, 12));
    public static final int ISLAND_SIZE_X = 7;
    public static final int ISLAND_SIZE_Y = 15;

    public IslandPrinter() {
        islandSkeleton = getIslandSkeleton();
    }

    //TODO: some notes that might improve code readability and cli performance (not only for PrintIslands)
    //  - this class will not have static methods
    //  - class will have a constructor that loads an island skeleton in a final String[][] (without dynamic parts)
    //  - method drawIsland (not static) will change the skeleton only in the dynamic parts

    public String[][] getIslandSkeleton() {
        String[][] islandSkeleton = new String[ISLAND_SIZE_X][ISLAND_SIZE_Y];
        for (String[] row: islandSkeleton) {
            Arrays.fill(row, " ");
        }
        //Need to set always colors because map will be composed not only by islands (other don't have green borders)
        islandSkeleton[0][4] = Colors.GREEN.toString() + UnicodeConstants.TOP_LEFT + Colors.RESET;
        islandSkeleton[0][11] = Colors.GREEN.toString() + UnicodeConstants.TOP_RIGHT + Colors.RESET;
        islandSkeleton[1][2] = Colors.GREEN.toString() + UnicodeConstants.TOP_LEFT + Colors.RESET;
        islandSkeleton[3][0] = Colors.GREEN.toString() + UnicodeConstants.TOP_LEFT + Colors.RESET;
        islandSkeleton[4][4] = Colors.GREEN + UnicodeConstants.TOP_LEFT.toString() + Colors.RESET;
        islandSkeleton[5][13] = Colors.GREEN + UnicodeConstants.TOP_LEFT.toString() + Colors.RESET;
        islandSkeleton[4][5] = Colors.GREEN + UnicodeConstants.TOP_RIGHT.toString() + Colors.RESET;
        islandSkeleton[1][12] = Colors.GREEN + UnicodeConstants.TOP_RIGHT.toString() + Colors.RESET;
        islandSkeleton[2][14] = Colors.GREEN + UnicodeConstants.TOP_RIGHT.toString() + Colors.RESET;

        for (int i = 5; i <= 10; i++) {
            islandSkeleton[0][i] = Colors.RED + UnicodeConstants.HORIZONTAL.toString() + Colors.RESET;
        }
        islandSkeleton[1][3] = Colors.GREEN + UnicodeConstants.HORIZONTAL.toString() + Colors.RESET;
        islandSkeleton[2][13] = Colors.GREEN + UnicodeConstants.HORIZONTAL.toString() + Colors.RESET;
        islandSkeleton[3][1] = Colors.GREEN + UnicodeConstants.HORIZONTAL.toString() + Colors.RESET;
        for (Pair<Integer, Integer> position: VERTICAL_POSITIONS) {
            islandSkeleton[position.getFirst()][position.getSecond()] =
                     Colors.GREEN + UnicodeConstants.VERTICAL.toString() + Colors.RESET;
        }
        for (Pair<Integer, Integer> position: BOTTOM_LEFT_POSITIONS) {
            islandSkeleton[position.getFirst()][position.getSecond()] =
                    Colors.GREEN + UnicodeConstants.BOTTOM_LEFT.toString() + Colors.RESET;
        }
        for (Pair<Integer, Integer> position: BOTTOM_RIGHT_POSITIONS) {
            islandSkeleton[position.getFirst()][position.getSecond()] =
                    Colors.GREEN + UnicodeConstants.BOTTOM_RIGHT.toString() + Colors.RESET;
        }
        for (int i = 1; i <= 3; i++) {
            islandSkeleton[5][i] = Colors.GREEN.toString() + UnicodeConstants.HORIZONTAL + Colors.RESET;
        }
        for (int i = 6; i <= 12; i++) {
            islandSkeleton[6][i] = Colors.GREEN + UnicodeConstants.HORIZONTAL.toString() + Colors.RESET;
        }
        return islandSkeleton;
    }

    public String[][] getIsland(int islandIndex, boolean isPresent, Integer numTowers,  TowerType type , Integer ... islandStudent) {
        String[][] island = islandSkeleton; //doesn't matter if array are mutable, only the dynamic part are replaced
        //(even if the island skeleton is modified this isn't a problem)
        int firstIslandIdxFigure = (islandIndex <= 9) ? islandIndex : islandIndex / 10;
        island[ISLAND_INDEX_POSITION.getFirst()][ISLAND_INDEX_POSITION.getSecond()] =
                Colors.BLACK.toString() + BackgroundColors.YELLOW + firstIslandIdxFigure + BackgroundColors.RESET + Colors.RESET;
        island[ISLAND_INDEX_POSITION.getFirst()][ISLAND_INDEX_POSITION.getSecond() + 1] = (islandIndex > 9) ?
                Colors.BLACK.toString() + BackgroundColors.YELLOW + islandIndex % 10 + BackgroundColors.RESET + Colors.RED :
                Colors.RED + UnicodeConstants.HORIZONTAL.toString() + Colors.RESET;
        if (type != null) {
            island[TOWER_POSITION.getFirst()][TOWER_POSITION.getSecond() - 1] =
                    towersUnicode.get(type).getSecond().toString() + numTowers + Colors.RESET;
            island[TOWER_POSITION.getFirst()][TOWER_POSITION.getSecond()] =
                    towersUnicode.get(type).getFirst().toString() + Colors.GREEN;
        } else {
            island[TOWER_POSITION.getFirst()][TOWER_POSITION.getSecond() - 1] = " ";
            island[TOWER_POSITION.getFirst()][TOWER_POSITION.getSecond()] = " ";
        }
        for (int i = 0; i < RealmType.values().length; i++) {
            RealmType r = RealmType.values()[i];
            island[STUDENT_COLOR_POSITION.get(r).getFirst()][STUDENT_COLOR_POSITION.get(r).getSecond()] =
                    STUDENT_COLOR_POSITION.get(r).getThird().toString() + islandStudent[i] + Colors.RESET;
        }
        if(isPresent){
            island[4][13] = "M";
        }
        else{
            island[4][13] = " ";
        }
        //TODO: add entrytales
        return island;
    }
}
