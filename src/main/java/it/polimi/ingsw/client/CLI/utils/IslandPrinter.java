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

    @Deprecated
    public static String[][] drawIsland(int islandIndex, Integer numTowers,  TowerType type , Integer ...islandStudent){
        String[][] draw = new String[7][15];
        draw[0][4] = ""+  Colors.GREEN + "╔" + Colors.RED;
        draw[0][11] = Colors.GREEN + "╗";
        draw[1][2] = ""+  Colors.GREEN+ "╔";
        draw[3][0] = ""+  Colors.GREEN+ "╔";
        draw[4][4] = "╔";
        draw[5][13] = "╔";
        draw[4][5] = "╗";
        draw[1][12] = "╗";
        draw[2][14] = "╗";
        draw[1][4] = "╝";
        draw[3][2] = "╝";
        draw[5][4] = "╝";
        draw[5][14] = "╝";
        draw[6][13] = "╝"+Colors.RESET;
        draw[5][0] = ""+  Colors.GREEN+ "╚";
        draw[6][5] = ""+  Colors.GREEN+ "╚";
        draw[1][11] = "╚";
        draw[2][12] = "╚";
        int f = 0;


        //TODO: students colors are wrong (Integer[] is based on the RealmType order)
        //  there is also a problem when island index i greater than 9
        for(int i = 0; i < 7; i++ ){
            for(int j = 0 ; j < 15 ; j++){
                if(i==0 && j>=5 && j<=10){
                    draw[i][j] = "═";
                }
                if(i==1 && j==3){
                    draw[i][j] = "═";
                }
                if(i==2){
                    if(j==2){
                        draw[i][j] = ""+  Colors.GREEN+ "║";
                    }
                    if(j==4){
                        draw[i][j] = "" + Colors.RED + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==8){
                        draw[i][j] = "" + Colors.BLUE + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==13){
                        draw[i][j] = "═";
                    }
                }
                if(i==3){
                    if(j==1){
                        draw[i][j] = "═";
                    }
                    if(j==6){
                        draw[i][j] = "" + Colors.YELLOW + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==14){
                        draw[i][j] = "║";
                    }
                }
                if(i==4){
                    if(j==0) {
                        draw[i][j] =""+  Colors.GREEN+ "║";
                    }
                    if(j==10){
                        draw[i][j] = "" + Colors.PURPLE + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==14){
                        draw[i][j] = "║";
                    }
                }
                if(i==5){
                    if(j>=1 && j<=3){
                        draw[i][j] = "═";
                    }
                    if(j==5){
                        draw[i][j] = "║";
                    }
                    if(j==6){
                        draw[i][j] = " ";
                    }
                    if(j==8){
                        draw[i][j] = ""  + Colors.GREEN + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==9){
                        draw[i][j] = " ";
                    }
                }
                if(i==6 && j>=6 && j<=12){
                    draw[i][j] = "═";
                }
            }
        }


        for(int i = 0; i < 7; i++ ) {
            for (int j = 0; j < 15; j++) {
                if(draw[i][j]==null){
                    draw[i][j] = " ";
                }
            }
        }

        if(numTowers>=1){
            if(type == TowerType.WHITE) {
                draw[4][1] = Colors.RESET + "" + numTowers;
                draw[4][2] = UnicodeConstants.WHITE_TOWER.toString() + Colors.GREEN;
            }else if(type == TowerType.BLACK){
                draw[4][1] = Colors.BLACK + "" + numTowers;
                draw[4][2] = UnicodeConstants.BLACK_TOWER.toString() + Colors.GREEN;
            }else{
                draw[4][1] = Colors.WHITE + "" + numTowers;
                draw[4][2] = UnicodeConstants.GREY_TOWER.toString() + Colors.GREEN;
            }
        }

        draw[0][7] = Colors.BLACK +""+ BackgroundColors.YELLOW + "" + islandIndex + BackgroundColors.RESET +""+ Colors.RED;
        return draw;
    }

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

    public String[][] getIsland(int islandIndex, Integer numTowers,  TowerType type , Integer ... islandStudent) {
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
        }
        for (int i = 0; i < RealmType.values().length; i++) {
            RealmType r = RealmType.values()[i];
            island[STUDENT_COLOR_POSITION.get(r).getFirst()][STUDENT_COLOR_POSITION.get(r).getSecond()] =
                    STUDENT_COLOR_POSITION.get(r).getThird().toString() + islandStudent[i] + Colors.RESET + Colors.GREEN;
        }
        return island;
    }
}
