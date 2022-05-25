package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.utils.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CharacterPrinter {
    private ExpertFieldView expertField;
    private final String[][] characterSkeleton;
    public static final int CHARACTER_DIM_X = 5;
    public static final int CHARACTER_DIM_Y = 10;
    private static final Pair<Integer, Integer> ID_POSITION = new Pair<>(0, 4);
    private static final String[] LINE_1 = new String[] {"p","r","i","c","e"," ", "="};
    private static final Pair<Integer, Integer> PRICE_POSITION = new Pair<>(1, LINE_1.length + 1);
    //TODO: image for characters without students or no entry tiles
    private static final List<Integer> STUDENTS_LINES = List.of(2, 3);
    private static final Pair<Integer, Integer> NO_ENTRY_TILE_POSITION = new Pair<>(3, 4);
    private static final Map<RealmType, UnicodeConstants> studentsUnicode =
            Map.of(RealmType.YELLOW_GNOMES, UnicodeConstants.YELLOW_DOT,
                    RealmType.BLUE_UNICORNS, UnicodeConstants.BLUE_DOT,
                    RealmType.GREEN_FROGS, UnicodeConstants.GREEN_DOT,
                    RealmType.RED_DRAGONS, UnicodeConstants.RED_DOT,
                    RealmType.PINK_FAIRES, UnicodeConstants.PURPLE_DOT);

    public CharacterPrinter() {
        characterSkeleton = loadCharacterSkeleton();
    }

    public void setExpertField(ExpertFieldView expertField) {
        this.expertField = expertField;
    }

    public String[][] loadCharacterSkeleton() {
        String[][] character = new String[CHARACTER_DIM_X][CHARACTER_DIM_Y];
        for(String[] strings: character) {
            Arrays.fill(strings, " ");
        }
        character[0][0] = UnicodeConstants.TOP_LEFT.toString();
        character[0][CHARACTER_DIM_Y - 1] = UnicodeConstants.TOP_RIGHT.toString();
        character[CHARACTER_DIM_X - 1][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        character[CHARACTER_DIM_X - 1][CHARACTER_DIM_Y - 1] = UnicodeConstants.BOTTOM_RIGHT.toString();
        for (int i = 1; i < CHARACTER_DIM_Y - 1; i++) {
            character[1][i] = (i + 1 < LINE_1.length) ? LINE_1[i - 1] : " ";
        }
        for (int i = 1; i < CHARACTER_DIM_Y - 1; i++) {
            character[0][i] = UnicodeConstants.HORIZONTAL.toString();
            character[CHARACTER_DIM_X - 1][i] = UnicodeConstants.HORIZONTAL.toString();
        }
        for (int i = 1; i < CHARACTER_DIM_X - 1; i++) {
            character[i][0] = UnicodeConstants.VERTICAL.toString();
            character[i][CHARACTER_DIM_Y - 1] = UnicodeConstants.VERTICAL.toString();
        }
        return character;
    }

    public String[][] getCharacter(int characterId) {
        int firstCharacterIdFigure = (characterId > 9) ? characterId / 10 : characterId;
        characterSkeleton[ID_POSITION.getFirst()][ID_POSITION.getSecond()] =
                BackgroundColors.RED.toString() + Colors.BLACK + firstCharacterIdFigure + Colors.RESET;
        characterSkeleton[ID_POSITION.getFirst()][ID_POSITION.getSecond() + 1] = (characterId > 9) ?
                BackgroundColors.RED.toString() + Colors.BLACK + characterId % 10 + Colors.RESET :
                UnicodeConstants.HORIZONTAL.toString();
        characterSkeleton[PRICE_POSITION.getFirst()][PRICE_POSITION.getSecond()] =
                BackgroundColors.RED.toString() + Colors.BLACK + expertField.getCharacterPrice(characterId) + Colors.RESET;
        for (Integer i: STUDENTS_LINES) {
            for (int j = 1; j < CHARACTER_DIM_Y - 1; j++) {
                characterSkeleton[i][j] = " ";
            }
        }
        switch (characterId) {
            case 1, 7, 11 -> getCharacterWithStudent(characterId);
            case 5 -> getCharacterWithNoEntryTile();
            default -> getCharacterWithoutNothing();
        }
        return characterSkeleton;
    }

    private void getCharacterWithStudent(int characterId) {
        characterSkeleton[NO_ENTRY_TILE_POSITION.getFirst()][NO_ENTRY_TILE_POSITION.getSecond()] = " ";
        characterSkeleton[NO_ENTRY_TILE_POSITION.getFirst()][NO_ENTRY_TILE_POSITION.getSecond() + 1] = " ";
        RealmType[] characterStudents = RealmType.getRealmsFromIntegerRepresentation(expertField.characterStudents(characterId));
        int charactersPerLine = (characterStudents.length) / 2;
        int spaceFromStudents = (CHARACTER_DIM_Y - 3) / charactersPerLine;
        int studentIdx = 0;
        for (Integer i: STUDENTS_LINES) {
            for (int j = 2, k = 0; k < charactersPerLine; j += spaceFromStudents, k++) {
                characterSkeleton[i][j] = studentsUnicode.get(characterStudents[studentIdx]).toString();
                studentIdx++;
            }
        }
    }

    private void getCharacterWithNoEntryTile() {
        characterSkeleton[NO_ENTRY_TILE_POSITION.getFirst()][NO_ENTRY_TILE_POSITION.getSecond()] =
                Colors.RED.toString() + expertField.getNumNoEntryTilesOnCharacter().getSecond() + Colors.RESET;
        characterSkeleton[NO_ENTRY_TILE_POSITION.getFirst()][NO_ENTRY_TILE_POSITION.getSecond() + 1] =
                Colors.RED + "x" + Colors.RESET;
    }

    private void getCharacterWithoutNothing() {
        //For the moment I fill with spaces, but characterId can be used to place a special unicode character for that character
        characterSkeleton[NO_ENTRY_TILE_POSITION.getFirst()][NO_ENTRY_TILE_POSITION.getSecond()] = " ";
        characterSkeleton[NO_ENTRY_TILE_POSITION.getFirst()][NO_ENTRY_TILE_POSITION.getSecond() + 1] = " ";
    }
}
