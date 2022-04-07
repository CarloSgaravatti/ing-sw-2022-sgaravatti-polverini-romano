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

@Deprecated
public class JsonUtils {
    public static JsonObject getCharacterJsonObject(int characterId) throws NoSuchElementException, NullPointerException {
        InputStream stream = JsonUtils.class.getResourceAsStream("/jsonConfigFiles/CharacterCardsEffectInfo.json");
        if (stream == null) throw new NullPointerException();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        JsonElement obj = JsonParser.parseReader(jsonReader);
        JsonArray jsonArray = obj.getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("characterId").getAsInt() == characterId) {
                return jsonObject;
            }
        }
        throw new NoSuchElementException();
    }

    //TODO: handle characters without effect methods
    /*public static Method getCharacterMethod(JsonObject characterJsonObject) throws ClassNotFoundException, NoSuchMethodException {
        CharacterCreator characterCreator = CharacterCreator.getInstance();
        Class<?> characterClass = characterCreator.getCharacter(characterJsonObject.get("characterId").getAsInt()).getClass();
        List<Class<?>> parametersClass = new ArrayList<>();
        JsonArray parametersJsonArray = characterJsonObject.get("parameters").getAsJsonArray();
        for (JsonElement j: parametersJsonArray) {
            parametersClass.add(Class.forName(j.getAsJsonObject().get("paramType").getAsString()));
        }
        String methodName = characterJsonObject.get("effectMethod").getAsString();
        return characterClass.getMethod(methodName, parametersClass.toArray(new Class<?>[0]));
    }*/
}
