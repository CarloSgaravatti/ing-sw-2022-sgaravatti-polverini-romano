package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.utils.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LobbyPrintManager {
    private final Lobby lobby = new Lobby();
    private final Map<Integer, Triplet<Integer, Boolean, String[]>> lobbyInfo;
    private int currentStartingIdx = 0;
    private final List<Integer> lobbyIds;

    public LobbyPrintManager(Map<Integer, Triplet<Integer, Boolean, String[]>> lobbyInfo) {
        this.lobbyInfo = lobbyInfo;
        lobbyIds = new ArrayList<>(lobbyInfo.keySet());
    }

    public boolean onNextFiveCommand() {
        if (lobbyInfo.size() - currentStartingIdx > 5) {
            currentStartingIdx += 5;
            return true;
        } else {
            System.out.println(Colors.RED + "There isn't another page" + Colors.RESET);
            return false;
        }
    }

    public boolean onPreviousFiveCommand() {
        if (currentStartingIdx >= 5) {
            currentStartingIdx -= 5;
            return true;
        } else {
            System.out.println(Colors.RED + "There isn't a previous page" + Colors.RESET);
            return false;
        }
    }

    public void printLobby() {
        switch (lobbyInfo.size() - currentStartingIdx) {
            case 0 -> printMatrix(lobby.getUpperCommandBox1());
            case 1 -> {
                Triplet<Integer, Boolean, String[]> gameInfo = lobbyInfo.get(lobbyIds.get(currentStartingIdx));
                String[][] setup0 = insertSetupInfo((currentStartingIdx == 0) ? lobby.getSetup0() : lobby.getSetup6(),
                        lobbyIds.get(currentStartingIdx), gameInfo.getFirst(), gameInfo.getSecond(), gameInfo.getThird());
                String[][] lobbyPrint = MapPrinter.appendMatrixInColumn(setup0, lobby.getUpperCommandBox());
                if (currentStartingIdx != 0) {
                    lobbyPrint = MapPrinter.appendMatrixInColumn(lobby.getLowerCommandBoxLeft(), lobbyPrint);
                }
                printMatrix(lobbyPrint);
            }
            case 2, 3, 4, 5 -> {
                Triplet<Integer, Boolean, String[]> gameInfo = lobbyInfo.get(lobbyIds.get(currentStartingIdx));
                String[][] setup1 = insertSetupInfo(lobby.getSetup1(), lobbyIds.get(currentStartingIdx), gameInfo.getFirst(),
                        gameInfo.getSecond(), gameInfo.getThird());
                String[][] lobbyPrint = MapPrinter.appendMatrixInColumn(setup1, lobby.getUpperCommandBox());
                for (int i = currentStartingIdx + 1; i < lobbyInfo.size() - 1; i++) {
                    Triplet<Integer, Boolean, String[]> game = lobbyInfo.get(lobbyIds.get(i));
                    String[][] setup2 = insertSetupInfo(lobby.getSetup2(), lobbyIds.get(i), game.getFirst(),
                            game.getSecond(), game.getThird());
                    lobbyPrint = MapPrinter.appendMatrixInColumn(setup2, lobbyPrint);
                }
                Triplet<Integer, Boolean, String[]> lastGame = lobbyInfo.get(lobbyIds.get(lobbyInfo.size() - 1));
                String[][] finalSetup = insertSetupInfo((currentStartingIdx != 0) ? lobby.getSetup8() : lobby.getSetup4(),
                        lobbyIds.get(lobbyInfo.size() - 1), lastGame.getFirst(), lastGame.getSecond(), lastGame.getThird());
                lobbyPrint = MapPrinter.appendMatrixInColumn(finalSetup, lobbyPrint);
                if (currentStartingIdx != 0) {
                    lobbyPrint = MapPrinter.appendMatrixInColumn(lobby.getLowerCommandBoxLeft(), lobbyPrint);
                }
                printMatrix(lobbyPrint);
            }
            default -> {
                Triplet<Integer, Boolean, String[]> gameInfo = lobbyInfo.get(lobbyIds.get(currentStartingIdx));
                String[][] setup1 = insertSetupInfo(lobby.getSetup1(), lobbyIds.get(currentStartingIdx), gameInfo.getFirst(),
                        gameInfo.getSecond(), gameInfo.getThird());
                String[][] lobbyPrint = MapPrinter.appendMatrixInColumn(setup1, lobby.getUpperCommandBox());
                for (int i = currentStartingIdx + 1; i < currentStartingIdx + 4; i++) {
                    Triplet<Integer, Boolean, String[]> game = lobbyInfo.get(lobbyIds.get(i));
                    String[][] setup2 = insertSetupInfo(lobby.getSetup2(), lobbyIds.get(i), game.getFirst(),
                            game.getSecond(), game.getThird());
                    lobbyPrint = MapPrinter.appendMatrixInColumn(setup2, lobbyPrint);
                }
                Triplet<Integer, Boolean, String[]> lastGame = lobbyInfo.get(lobbyIds.get(currentStartingIdx + 4));
                String[][] finalSetup = insertSetupInfo((currentStartingIdx != 0) ? lobby.getSetup3() : lobby.getSetup5(),
                        lobbyIds.get(currentStartingIdx + 4), lastGame.getFirst(), lastGame.getSecond(), lastGame.getThird());
                lobbyPrint = MapPrinter.appendMatrixInColumn(finalSetup, lobbyPrint);
                lobbyPrint = MapPrinter.appendMatrixInColumn((currentStartingIdx == 0) ? lobby.getLowerCommandBoxRight() :
                        lobby.getLowerCommandBox(), lobbyPrint);
                printMatrix(lobbyPrint);
            }
        }
    }

    private void printMatrix(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
    }

    private String[][] insertSetupInfo(String[][] setup, int id, int numPlayers, boolean rules, String[] names) {
        setup[2][3] = String.valueOf(id);
        String rulesString = (rules) ? "expert" : "simple";
        for (int i = 13; i < rulesString.length() + 13; i++) {
            setup[2][i] = String.valueOf(rulesString.charAt(i - 13));
        }
        setup[2][27] = String.valueOf(names.length);
        setup[2][28] = "/";
        setup[2][29] = String.valueOf(numPlayers);
        for (int i = 0; i < names.length; i++) {
            String nameVisible = (names[i].length() <= 10) ? names[i] : names[i].substring(0, 10) + "...";
            for (int j = 0; j < nameVisible.length(); j++) {
                setup[i + 1][j + 34] = String.valueOf(nameVisible.charAt(j));
            }
        }
        return setup;
    }
}
