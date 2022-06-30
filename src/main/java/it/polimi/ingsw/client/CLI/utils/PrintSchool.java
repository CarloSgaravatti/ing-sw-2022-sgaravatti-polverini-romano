package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import it.polimi.ingsw.utils.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * PrintSchool is used for creating the skeleton of the school with all dynamics information inside
 */
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

    /**
     * Construct a new PrintSchool that will load the school skeleton that will be filled
     * when printing a specific school
     */
    public PrintSchool() {
        schoolSkeleton = loadSchoolSkeleton();
    }

    /**
     * loadSchoolSkeleton create the school boxes (just the structure) and return the value that will be
     * used by School by SchoolMapPrinter
     *
     * @return return the value that will be used by School by SchoolMapPrinter
     */
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

    /**
     * Returns the completed school box with all dynamics part.
     * The method filled the school skeleton with the dynamics part of the island
     *
     * @param isProfessorPresent boolean array that tell wich professor is present in the school
     * @param entranceStudents integer array that tell how many student for each type are present in the entrance
     * @param numTowers number of towers present in school
     * @param type type of towers present in school
     * @param diningRoomStudents integer array that tell how many student for each type,
     *                           are present in the dining room
     * @return the completed school box with all dynamics part
     */
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
        for (int i = 0; i < 8; i++) {
            school[line][row] = (i < numTowers) ? tower : " ";
            line += (row == 32) ? 1 : 0;
            row += (row != 32) ? 1 : -1;
        }
        return schoolSkeleton;
    }

    /**
     * Add heading information to the school box
     *
     * @param nickname owner of the school name
     * @param coins number of coins of the owner of te school
     * @param addCoins boolean value for telling if the player have to get a coins
     * @param school school box skeleton
     * @param lastAssistant Pair of integer that represent index and mother nature movement of last assistant played
     * @return school box with new heading
     */
    public String[][] addHeading(String nickname, int coins, boolean addCoins, String[][] school, Pair<Integer, Integer> lastAssistant) {
        String[][] schoolWithHeading = new String[school.length + 2][school[0].length];
        Arrays.fill(schoolWithHeading[0], " ");
        Arrays.fill(schoolWithHeading[schoolWithHeading.length - 1], " ");
        for (int i = 1; i < schoolWithHeading.length - 1; i++) {
            for (int j = 0; j < school[i - 1].length; j++) {
                schoolWithHeading[i][j] = school[i - 1][j];
            }
        }
        if (addCoins) {
            String[] coinsPart = ("coins = " + coins).split("");
            for (int i = coinsPart.length - 1; i >= 0; i--) {
                schoolWithHeading[0][SCHOOL_DIMENSION_Y - (coinsPart.length - 1 - i) - 2] = coinsPart[i];
            }
        }
        String nicknameHead = ((nickname.length() > 13) ? nickname.substring(0, 10) + "..." : nickname) + "'s school";
        for (int i = 0; i < nicknameHead.length(); i++) {
            schoolWithHeading[0][i + 1] = String.valueOf(nicknameHead.charAt(i));
        }
        String lastAssistantPrint = "Last assistant played: ";
        if (lastAssistant != null)  lastAssistantPrint += lastAssistant.toString();
        for (int i = 0; i < lastAssistantPrint.length(); i++) {
            schoolWithHeading[schoolWithHeading.length - 1][i + 1] = String.valueOf(lastAssistantPrint.charAt(i));
        }
        return schoolWithHeading;
    }
}
