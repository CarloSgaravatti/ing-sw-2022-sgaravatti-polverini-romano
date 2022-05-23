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
    }

    public void initializePlayersSchoolOrder(String playerName) {
        playersSchoolOrder = new ArrayList<>(modelView.getPlayers().keySet());
        playersSchoolOrder.remove(playerName);
        playersSchoolOrder.add(0, playerName);
    }

    public void changeOnlySchoolOf(String player) {
        int positionY = getPositionOfSchool(player);
        MapPrinter.substituteSubMatrix(schoolMap, getSchoolOf(player), new Pair<>(0, positionY));
    }

    private String[][] getSchoolOf(String player) {
        PlayerView playerView = modelView.getPlayers().get(player);
        boolean[] playerProfessors = new boolean[RealmType.values().length];
        for (int i = 0; i < playerProfessors.length; i++) {
            playerProfessors[i] = player.equals(modelView.getField().getProfessorOwner(RealmType.values()[i]));
        }
        Pair<Integer[], Integer[]> students = playerView.getSchoolStudents();
        return schoolPrinter.getSchool(playerProfessors, students.getFirst(),
                playerView.getNumTowers(), playerView.getPlayerTower(), students.getSecond());
    }

    private int getPositionOfSchool(String player) {
        return playersSchoolOrder.indexOf(player) * PrintSchool.SCHOOL_DIMENSION_Y;
    }
}
