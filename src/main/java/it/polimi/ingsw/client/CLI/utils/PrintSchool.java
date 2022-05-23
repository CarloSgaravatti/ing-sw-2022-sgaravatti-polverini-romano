package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PrintSchool {
    String[][] schoolSkeleton;
    public static final int SCHOOL_DIMENSION_X = 7;
    public static final int SCHOOL_DIMENSION_Y = 35;
    private static final Map<TowerType, Pair<UnicodeConstants, Colors>> towersUnicode =
            Map.of(TowerType.BLACK, new Pair<>(UnicodeConstants.BLACK_TOWER, Colors.BLACK),
                    TowerType.WHITE, new Pair<>(UnicodeConstants.WHITE_TOWER, Colors.WHITE),
                    TowerType.GREY, new Pair<>(UnicodeConstants.GREY_TOWER, Colors.CYAN));
    private static final Map<RealmType, UnicodeConstants> studentsUnicode =
            Map.of(RealmType.YELLOW_GNOMES, UnicodeConstants.YELLOW_DOT,
                    RealmType.BLUE_UNICORNS, UnicodeConstants.BLUE_DOT,
                    RealmType.GREEN_FROGS, UnicodeConstants.GREEN_DOT,
                    RealmType.RED_DRAGONS, UnicodeConstants.RED_DOT,
                    RealmType.PINK_FAIRES, UnicodeConstants.PURPLE_DOT);
    private static final List<Integer> VERTICAL_POSTIONS_Y = List.of(0, 4, 24, 29, 34);

    public PrintSchool() {
        schoolSkeleton = loadSchoolSkeleton();
    }

    public static String[][] drawSchool(boolean[] isProfessorPresent, int[] entranceStudents,int numToweras ,TowerType type, int... diningRoomStudents) {
        String[][] school = new String[7][35];
        school[0][0] = "╔";
        school[6][0] = "╚";
        school[0][34] = "╗";
        school[6][34] = "╝";

        for (int i = 1; i < 34; i++) {
            school[0][i] = "═";
        }

        for (int i = 1; i < 34; i++) {
            school[6][i] = "═";
        }

        int f = 0;
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 35; j++) {
                if(j==0 || j==4 || j==24 || j==29 || j==34){
                    school[i][j] = "║";
                }
                if(i==1 && j== 3){
                    school[i][j] = Colors.RED + "●"+Colors.RESET;
                }
                if(i==2 && j== 3){
                    school[i][j] = Colors.YELLOW+"●"+Colors.RESET;
                }
                if(i==3 && j== 3){
                    school[i][j] = Colors.GREEN+"●"+Colors.RESET;
                }
                if(i==4 && j== 3){
                    school[i][j] = Colors.BLUE+"●"+Colors.RESET;
                }
                if(i==5 && j== 3){
                    school[i][j] = Colors.PURPLE+"●"+Colors.RESET;
                }
                if(i==1 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";
                }
                if(i==2 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";

                }
                if(i==3 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";

                }
                if(i==4 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";

                }
                if(i==5 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    school[i][j+1] = "x";

                }
            }
        }

        int cont = 0;
        f = 0;
        for(int i = 1; i < 6 ; i++){
            for(int j = 0 ; j < 30; j++){
                if(i == 1 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.RED + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 2 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.YELLOW + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 3 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.GREEN + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 4 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.BLUE + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 5 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.PURPLE + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
            }
            cont=0;
            f++;
        }

        f = 0;
        for(int i = 1; i < 6; i++){
            if(i == 1) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.RED + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 2) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.YELLOW + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 3) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.GREEN + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 4) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.BLUE + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 5) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.PURPLE + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
        }

        int l=1;
        int s=31;
        cont=numToweras;
        while (cont != 0){
            if(type == TowerType.BLACK){
                school[l][s] = UnicodeConstants.BLACK_TOWER.toString();
            }
            if(type == TowerType.WHITE){
                school[l][s] = UnicodeConstants.WHITE_TOWER.toString();
            }
            if(type == TowerType.GREY){
                school[l][s] = UnicodeConstants.GREY_TOWER.toString();
            }
            if(s==32){
                s=31;
                l++;
            }
            else{
                s++;
            }
            cont--;
        }

        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 35; j++){
                if(school[i][j]==null){
                    school[i][j] = " ";
                }
            }
        }
        return school;
    }

    public String[][] loadSchoolSkeleton() {
        String[][] skeleton = new String[SCHOOL_DIMENSION_X][SCHOOL_DIMENSION_Y];
        for (String[] row: skeleton) {
            Arrays.fill(row, " ");
        }
        skeleton[0][0] = UnicodeConstants.TOP_LEFT.toString();
        skeleton[6][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        skeleton[0][34] = UnicodeConstants.TOP_RIGHT.toString();
        skeleton[6][34] = UnicodeConstants.BOTTOM_RIGHT.toString();
        for (int i = 1; i < 34; i++) {
            skeleton[0][i] = UnicodeConstants.HORIZONTAL.toString();
            skeleton[SCHOOL_DIMENSION_X - 1][i] = UnicodeConstants.HORIZONTAL.toString();
        }
        for (int i = 1; i < 6; i++) {
            for (Integer j: VERTICAL_POSTIONS_Y) {
                skeleton[i][j] = UnicodeConstants.VERTICAL.toString();
            }
        }
        return skeleton;
    }

    public String[][] getSchool(boolean[] isProfessorPresent, Integer[] entranceStudents,int numTowers ,TowerType type, Integer[] diningRoomStudents) {
        String[][] school = schoolSkeleton;
        int cont = 0;
        for(int i = 1; i < 6; i++){
            RealmType currentRealm = RealmType.values()[i - 1];
            String studentUnicode = studentsUnicode.get(currentRealm).toString();
            school[i][1] = Integer.toString(entranceStudents[i - 1]);
            school[i][2] = "x";
            school[i][3] = studentsUnicode.get(currentRealm).toString();
            school[i][26] = (isProfessorPresent[i - 1]) ? studentUnicode : UnicodeConstants.NO_COLOR_DOT.toString();
            for(int j = 5 ; j < 24; j += 2){
                school[i][j] = (cont < diningRoomStudents[i - 1]) ? studentUnicode : UnicodeConstants.NO_COLOR_DOT.toString();
                cont += (cont < diningRoomStudents[i - 1]) ? 1 : 0;
            }
            cont = 0;
        }
        int line = 1;
        int row = 31;
        String tower = towersUnicode.get(type).getSecond().toString() + towersUnicode.get(type).getFirst() + Colors.RESET;
        for (int i = 0; i < numTowers; i++) {
            school[line][row] = tower;
            line += (row == 32) ? 1 : 0;
            row += (row != 32) ? 1 : -1;
        }
        return schoolSkeleton;
    }
}
