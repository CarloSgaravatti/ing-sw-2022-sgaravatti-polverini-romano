package it.polimi.ingsw.utils;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.RealmType;
import it.polimi.ingsw.model.Student;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest extends TestCase {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    void getCharacterJsonObjectTest(int id) {
        JsonObject obj = JsonUtils.getCharacterJsonObject(id);
        Assertions.assertEquals(obj.get("characterId").getAsInt(), id);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 15})
    void getNotExistingCharacterTest(int id) {
        try {
            JsonObject obj = JsonUtils.getCharacterJsonObject(id);
        } catch (NoSuchElementException e){
            Assertions.assertTrue(true);
        }
    }

    /*
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    void getCharacterMethodTest(int id) {
        Game game = new Game(null, null);
        for (int i = 0; i < 10; i++) game.getBag().insertStudent(new Student(RealmType.YELLOW_GNOMES));
        CharacterCreator creator = CharacterCreator.getInstance();
        creator.setGame(game);
        JsonObject obj = JsonUtils.getCharacterJsonObject(id);
        String methodName = obj.get("effectMethod").getAsString();
        if (!methodName.equals("NO")) {
            try {
                Assertions.assertEquals(JsonUtils.getCharacterMethod(obj).getName(), methodName);
            } catch (ClassNotFoundException e) {
                Assertions.fail();
            } catch (NoSuchMethodException e1) {
                System.out.println("A");
                Assertions.fail();
            }
        }
    }*/
}