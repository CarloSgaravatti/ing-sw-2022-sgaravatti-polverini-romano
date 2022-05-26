package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class CharacterMapPrinter {
    private final CharacterPrinter characterPrinter = new CharacterPrinter();
    private String[][] characterMap;
    private ExpertFieldView expertField;
    private final Map<Integer, Integer> charactersPrintsPositions = new HashMap<>();

    public void setExpertField(ExpertFieldView expertField) {
        characterPrinter.setExpertField(expertField);
        this.expertField = expertField;
    }

    public void initializeCharacterMap() {
        characterMap = new String[0][0];
        charactersPrintsPositions.clear();
        for (Integer character: expertField.getCharacters().keySet()) {
            charactersPrintsPositions.put(character, characterMap.length);
            String[][] characterPrint = characterPrinter.getCharacter(character);
            characterMap = MapPrinter.appendMatrixInColumn(characterPrint, characterMap);
        }
    }

    public void changeOnlyCharacter(int characterId) {
        String[][] characterPrint = characterPrinter.getCharacter(characterId);
        characterMap = MapPrinter.substituteSubMatrix(characterMap, characterPrint,
                new Pair<>(charactersPrintsPositions.get(characterId), 0));
    }

    public String[][] getCharacterMap() {
        return characterMap;
    }
}
