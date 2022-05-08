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


public class JsonUtils {
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

    public static GameConstants constantsByNumPlayer(int numPlayer){
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

}
