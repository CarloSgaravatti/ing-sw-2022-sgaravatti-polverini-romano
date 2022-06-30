package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ExpertFieldView;
import it.polimi.ingsw.utils.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * CharacterMapPrinter role is to manage all the character map components that are printed on the command line interface with the
 * help of CharacterPrinter instance.
 *
 * @see CharacterPrinter
 */
public class CharacterMapPrinter {
    private final CharacterPrinter characterPrinter = new CharacterPrinter();
    private String[][] characterMap;
    private ExpertFieldView expertField;
    private final Map<Integer, Integer> charactersPrintsPositions = new HashMap<>();

    /**
     * Sets the value of the specified ExpertField to this
     *
     * @param expertField the expert field of the game (only in case of expert rules)
     */

    public void setExpertField(ExpertFieldView expertField) {
        characterPrinter.setExpertField(expertField);
        this.expertField = expertField;
    }

    /**
     * (Re)Initialize the character map in base of the data that is contained in the expert field
     */

    public void initializeCharacterMap() {
        characterMap = new String[0][0];
        charactersPrintsPositions.clear();
        for (Integer character: expertField.getCharacters().keySet()) {
            charactersPrintsPositions.put(character, characterMap.length);
            String[][] characterPrint = characterPrinter.getCharacter(character);
            characterMap = MapPrinter.appendMatrixInColumn(characterPrint, characterMap);
        }
    }

    /**
     * Change the character box that have the specific ID
     *
     * @param characterId is the id different for every character
     */
    public void changeOnlyCharacter(int characterId) {
        String[][] characterPrint = characterPrinter.getCharacter(characterId);
        characterMap = MapPrinter.substituteSubMatrix(characterMap, characterPrint,
                new Pair<>(charactersPrintsPositions.get(characterId), 0));
    }

    /**
     * Returns the character map that will be printed on screen
     *
     * @return the character map
     */
    public String[][] getCharacterMap() {
        return characterMap;
    }
}
