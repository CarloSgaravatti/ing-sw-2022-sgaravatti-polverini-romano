package it.polimi.ingsw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.CharacterCreator;

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
                    JsonObject jsonObject1 = jsonElement.getAsJsonObject();
                    actions.add(jsonObject1.get("ActionType").getAsString());
                }
                return actions;
            }
        }
        throw new NoSuchElementException();
    }
}
