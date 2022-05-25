package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.FieldView;
import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.client.modelView.PlayerView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class SchoolMapPrinter {
    private ModelView modelView;
    private final PrintSchool schoolPrinter = new PrintSchool();
    private String[][] schoolMap;
    private List<String> playersSchoolOrder;

    public void setModelView(ModelView modelView) {
        this.modelView = modelView;
    }

    public String[][] getSchoolMap() {
        return schoolMap;
    }

    public void initializeSchoolMap() {
        String[][] schoolMapTmp = new String[0][0];
        for (String player: playersSchoolOrder) {
            schoolMapTmp = MapPrinter.appendMatrixInLine(getSchoolOf(player), schoolMapTmp);
        }
        schoolMap = schoolMapTmp;
        schoolMap = addPaddingAndMargin();
    }

    public void initializePlayersSchoolOrder(String playerName) {
        playersSchoolOrder = new ArrayList<>(modelView.getPlayers().keySet());
        playersSchoolOrder.remove(playerName);
        playersSchoolOrder.add(0, playerName);
    }

    public void changeOnlySchoolOf(String player) {
        int positionY = getPositionOfSchool(player);
        //incremented by 2 because of padding and margin
        schoolMap = MapPrinter.substituteSubMatrix(schoolMap, getSchoolOf(player), new Pair<>(1, positionY + 2));
    }

    private String[][] getSchoolOf(String player) {
        PlayerView playerView = modelView.getPlayers().get(player);
        boolean[] playerProfessors = new boolean[RealmType.values().length];
        for (int i = 0; i < playerProfessors.length; i++) {
            playerProfessors[i] = player.equals(modelView.getField().getProfessorOwner(RealmType.values()[i]));
        }
        Pair<Integer[], Integer[]> students = playerView.getSchoolStudents();
        String[][] school = schoolPrinter.getSchool(playerProfessors, students.getFirst(),
                playerView.getNumTowers(), playerView.getPlayerTower(), students.getSecond());
        return schoolPrinter.addHeading(player, modelView.getPlayers().get(player).getPlayerCoins(), modelView.isExpert(), school);
    }

    private int getPositionOfSchool(String player) {
        return playersSchoolOrder.indexOf(player) * PrintSchool.SCHOOL_DIMENSION_Y;
    }

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
