package it.polimi.ingsw.server.resumeGame;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.effects.*;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.io.IOException;

public class InfluenceStrategyAdapter extends TypeAdapter<InfluenceStrategy> {
    @Override
    public void write(JsonWriter jsonWriter, InfluenceStrategy influenceStrategy) throws IOException {
        if (influenceStrategy == null) {
            jsonWriter.nullValue();
        } else if (influenceStrategy instanceof NormalInfluenceStrategy) {
            jsonWriter.value("1");
        } else if (influenceStrategy instanceof NoTowerInfluenceStrategy) {
            jsonWriter.value("2");
        } else if (influenceStrategy instanceof GainInfluenceStrategy) {
            jsonWriter.value("3");
        } else if (influenceStrategy instanceof NoStudentInfluenceStrategy) {
            RealmType realmType = ((NoStudentInfluenceStrategy) influenceStrategy).getStudentType();
            jsonWriter.value(realmType.getAbbreviation());
        }
    }

    @Override
    public InfluenceStrategy read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String value = jsonReader.nextString();
        return switch (value) {
            case "1" -> new NormalInfluenceStrategy();
            case "2" -> new NoTowerInfluenceStrategy(new NormalInfluenceStrategy());
            case "3" -> new GainInfluenceStrategy(new NormalInfluenceStrategy());
            default -> {
                try {
                    RealmType realmType = RealmType.getRealmByAbbreviation(value);
                    yield new NoStudentInfluenceStrategy(new NormalInfluenceStrategy(), realmType);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalStateException("Unexpected value: " + value);
                }
            }
        };
    }
}