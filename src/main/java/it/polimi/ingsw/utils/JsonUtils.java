package it.polimi.ingsw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.gameConstants.GameConstants;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * JsonUtils provides all static method to get information from configuration files that is useful during the game.
 */
public class JsonUtils {
    /**
     * Returns all the actions that a game with the specified rules permit. The actions are read from a json file
     *
     * @param difficulty the rules of the game, true if the game is expert, otherwise false
     * @return a list of actions that game with the specified rules permit
     * @throws NoSuchElementException if the file does not contain the requested information
     * @throws NullPointerException if the file does exist
     */
    public static List<String> getRulesByDifficulty(boolean difficulty) throws NoSuchElementException, NullPointerException {
        InputStream stream = JsonUtils.class.getResourceAsStream("/jsonConfigFiles/RulesConfig.json");
        if (stream == null) throw new NullPointerException();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        JsonElement obj = JsonParser.parseReader(jsonReader);
        JsonArray jsonArray = obj.getAsJsonArray();
        String difficultyName = (difficulty)? "hard" : "simple" ;
        List<String> actions = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray){ //FOR cicle for Difficulty
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("Difficulty").getAsString().equals(difficultyName)){
                JsonArray actionList = jsonObject.get("Rules").getAsJsonArray();
                for(JsonElement jsonElement1 : actionList){ //FOR cicle for ActionType
                    JsonObject jsonObject1 = jsonElement1.getAsJsonObject();
                    actions.add(jsonObject1.get("ActionType").getAsString());
                }
                return actions;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns all constants of the game by the number of the player in game, the constants are read from a json file
     *
     * @param numPlayer number of players in the game
     * @return all constants of the game
     */
    public static GameConstants constantsByNumPlayer(int numPlayer) {
        InputStream stream = JsonUtils.class.getResourceAsStream("/jsonConfigFiles/ConstantsByPlayers.json");
        if (stream == null) throw new NullPointerException();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        JsonElement obj = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = obj.getAsJsonObject();
        GameConstants gameConstants = new GameConstants();
        gameConstants.setNumTotalStudents( jsonObject.get("TotalNumOfStudents").getAsInt());
        gameConstants.setNumCharacters( jsonObject.get("NumOfCharacters").getAsInt());
        gameConstants.setNumIslands(jsonObject.get("NumIslands").getAsInt());
        gameConstants.setNumCoins(jsonObject.get("NumOfCoins").getAsInt());
        gameConstants.setNumCharacterPerGame(jsonObject.get("NumOfCharacterPerGame").getAsInt());
        gameConstants.setNumAssistantsPerWizard(jsonObject.get("NumOfAssistantsPerWizard").getAsInt());
        gameConstants.setMaxStudentPerDiningRoom(jsonObject.get("MaxStudentPerDiningRoom").getAsInt());
        JsonArray jsonArray = jsonObject.get("DifferencesByNumPlayer").getAsJsonArray();
        for(JsonElement jsonElement : jsonArray){
            JsonObject differentConstantsByNumPlayer = jsonElement.getAsJsonObject();
            if(differentConstantsByNumPlayer.get("NumPlayer").getAsInt() == numPlayer){
                gameConstants.setNumTowers(differentConstantsByNumPlayer.get("NumTower").getAsInt());
                gameConstants.setNumStudentsInEntrance(differentConstantsByNumPlayer.get("NumStudentsInEntrance").getAsInt());
                gameConstants.setNumStudentsPerCloud(differentConstantsByNumPlayer.get("NumStudentsPerCloud").getAsInt());
            }
        }
        return gameConstants;
    }

    /**
     * Returns a Triplet of strings containing the des description of the character, instruction for calling the
     * character and an example of call of the character
     *
     * @param characterId index of the character
     * @return a Triplet of strings containing the des description of the character, instruction for calling the
     *      * character and an example of call of the character
     * @throws NoSuchElementException if the character chose doesn't exist in file
     */
    public static Triplet<String, String, String> getCharacterDescription(int characterId) throws NoSuchElementException {
        InputStream stream = JsonUtils.class.getResourceAsStream("/jsonConfigFiles/CharactersDescription.json");
        if (stream == null) throw new NullPointerException();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        JsonElement obj = JsonParser.parseReader(jsonReader);
        JsonArray jsonArray = obj.getAsJsonArray();
        for (JsonElement jsonElement : jsonArray){
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.get("characterId").getAsInt() == characterId) {
                Triplet<String, String, String> characterInfo = new Triplet<>();
                characterInfo.setFirst(jsonObject.get("description").getAsString());
                characterInfo.setSecond(jsonObject.get("callInstructions").getAsString());
                characterInfo.setThird(jsonObject.get("example").getAsString());
                return characterInfo;
            }
        }
        throw new NoSuchElementException();
    }
}
