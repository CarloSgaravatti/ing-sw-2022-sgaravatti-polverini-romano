package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * SchoolMapPrinter role is to manage all the school map components that are printed on the command line interface with the
 * help of a PrintSchool instance.
 * @see PrintSchool
 */
public class SchoolMapPrinter {
    private ModelView modelView;
    private final PrintSchool schoolPrinter = new PrintSchool();
    private String[][] schoolMap;
    private List<String> playersSchoolOrder;

    /**
     * Sets the value of the specified ModelView to this
     *
     * @param modelView the model view of the game
     */
    public void setModelView(ModelView modelView) {
        this.modelView = modelView;
    }

    /**
     * Returns the school map grid that will be printed on screen
     *
     * @return the school map grid
     */
    public String[][] getSchoolMap() {
        return schoolMap;
    }

    /**
     * (Re)Initialize the school map grid in base of the data that is contained on the model view
     */
    public void initializeSchoolMap() {
        String[][] schoolMapTmp = new String[0][0];
        for (String player: playersSchoolOrder) {
            schoolMapTmp = MapPrinter.appendMatrixInLine(getSchoolOf(player), schoolMapTmp);
        }
        schoolMap = schoolMapTmp;
        schoolMap = addPaddingAndMargin();
    }

    /**
     * Sets, at the beginning of the game, the order of appearance of the schools in CLI prints. The first school will
     * always be the client's player school
     *
     * @param playerName the nickname of the client's player
     */
    public void initializePlayersSchoolOrder(String playerName) {
        playersSchoolOrder = new ArrayList<>(modelView.getPlayers().keySet());
        playersSchoolOrder.remove(playerName);
        playersSchoolOrder.add(0, playerName);
    }

    /**
     * Modify the school map grid by substituting only the school of the specified player
     *
     * @param player the player of which the school have changed
     */
    public void changeOnlySchoolOf(String player) {
        int positionY = getPositionOfSchool(player);
        //incremented by 2 because of padding and margin
        schoolMap = MapPrinter.substituteSubMatrix(schoolMap, getSchoolOf(player), new Pair<>(1, positionY + 2));
    }

    /**
     * Returns the school local grid map of the specified player, that contains the school, the school heading and the
     * information about the last played assistant
     *
     * @param player the owner of the school
     * @return the school local grid map of the player
     */
    private String[][] getSchoolOf(String player) {
        PlayerView playerView = modelView.getPlayers().get(player);
        boolean[] playerProfessors = new boolean[RealmType.values().length];
        for (int i = 0; i < playerProfessors.length; i++) {
            playerProfessors[i] = player.equals(modelView.getField().getProfessorOwner(RealmType.values()[i]));
        }
        Pair<Integer[], Integer[]> students = playerView.getSchoolStudents();
        String[][] school = schoolPrinter.getSchool(playerProfessors, students.getFirst(),
                playerView.getNumTowers(), playerView.getPlayerTower(), students.getSecond());
        Pair<Integer, Integer> lastAssistant = modelView.getPlayers().get(player).getLastPlayedAssistant();
        return schoolPrinter.addHeading(player, modelView.getPlayers().get(player).getPlayerCoins(),
                modelView.isExpert(), school, lastAssistant);
    }

    /**
     * Returns the y-position of the specified player's school in the schools grid map
     *
     * @param player the owner of the school
     * @return the y-position of the specified player's school
     */
    private int getPositionOfSchool(String player) {
        return playersSchoolOrder.indexOf(player) * PrintSchool.SCHOOL_DIMENSION_Y;
    }

    /**
     * Adds the border to the school, that contains the heading, and also adds padding and margin to separate the school
     * map to the other parts of the global map
     *
     * @return the school map, decorated with padding, margin and borders
     */
    private String[][] addPaddingAndMargin() {
        String[][] newSchoolMap = new String[schoolMap.length + 2][schoolMap[0].length + 4];
        for (int i = 1; i < newSchoolMap[0].length - 1; i++) {
            newSchoolMap[0][i] = UnicodeConstants.HORIZONTAL.toString();
            newSchoolMap[newSchoolMap.length - 1][i] = UnicodeConstants.HORIZONTAL.toString();
        }
        String[] heading = new String[] {"S","C","H","O","O","L","S"};
        for (int i = 0; i < heading.length; i++) {
            newSchoolMap[0][i + 5] = heading[i];
        }
        for (int i = 1; i < newSchoolMap.length - 1; i++) {
            newSchoolMap[i][0] = UnicodeConstants.VERTICAL.toString();
            newSchoolMap[i][newSchoolMap[0].length - 1] = UnicodeConstants.VERTICAL.toString();
            newSchoolMap[i][1] = " ";
            newSchoolMap[i][newSchoolMap[0].length - 2] = " ";
        }
        newSchoolMap[0][0] = UnicodeConstants.TOP_LEFT.toString();
        newSchoolMap[0][newSchoolMap[0].length - 1] = UnicodeConstants.TOP_RIGHT.toString();
        newSchoolMap[newSchoolMap.length - 1][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        newSchoolMap[newSchoolMap.length - 1][newSchoolMap[0].length - 1] = UnicodeConstants.BOTTOM_RIGHT.toString();
        MapPrinter.substituteSubMatrix(newSchoolMap, schoolMap, new Pair<>(1, 2));
        return newSchoolMap;
    }
}
