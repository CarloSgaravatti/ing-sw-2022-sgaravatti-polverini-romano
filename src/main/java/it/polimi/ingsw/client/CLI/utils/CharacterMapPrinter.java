package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class CharacterMapPrinter {
    private CharacterPrinter characterPrinter = new CharacterPrinter();
    private String[][] characterMap;
    private ExpertFieldView expertField;
    private Map<Integer, Integer> charactersPrintsPositions = new HashMap<>();

    public void setExpertField(ExpertFieldView expertField) {
        characterPrinter.setExpertField(expertField);
        this.expertField = expertField;
    }

    public void initializeCharacterMap() {
        characterMap = new String[0][0];
        for (Integer character: expertField.getCharacters().keySet()) {
            charactersPrintsPositions.put(character, characterMap.length);
            String[][] characterPrint = characterPrinter.getCharacter(character);
            characterMap = MapPrinter.appendMatrixInColumn(characterPrint, characterMap);
        }
    }

    public void changeOnlyCharacter(int characterId) {
        String[][] characterPrint = characterPrinter.getCharacter(characterId);
        characterMap = MapPrinter.substituteSubMatrix(characterMap, characterPrint,
                new Pair<>(0, charactersPrintsPositions.get(characterId)));
    }

    public String[][] getCharacterMap() {
        return characterMap;
    }
}
