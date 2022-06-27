package it.polimi.ingsw.server.resumeGame;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.IslandGroup;
import it.polimi.ingsw.model.SingleIsland;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.enumerations.RealmType;
import it.polimi.ingsw.model.enumerations.TowerType;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IslandTypeAdapter extends TypeAdapter<Island> {
    @Override
    public void write(JsonWriter jsonWriter, Island island) throws IOException {
        TowerType islandTower = island.getTowerType();
        jsonWriter.beginObject();
        jsonWriter.name("tower").value((islandTower == null) ? "" : islandTower.toString());
        jsonWriter.name("numTowers").value(Integer.toString(island.getNumTowers()));
        jsonWriter.name("noEntryTiles").value(Integer.toString(island.getNoEntryTilePresents()));
        jsonWriter.name("students").value(getStudentsString(island));
        jsonWriter.name("motherNaturePresent").value(island.isMotherNaturePresent());
        jsonWriter.endObject();
    }

    private String getStudentsString(Island island) {
        if (island.getNumTowers() == 1) {
            return StringUtils.join(island.getStudents().stream()
                    .map(student -> student.getStudentType().getAbbreviation()).toList().toArray(new String[0]), ";");
        } else {
            List<Island> islands = ((IslandGroup) island).getIslands();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < islands.size() - 1; i++) {
                result.append(getStudentsString(islands.get(i))).append("!");
            }
            result.append(getStudentsString(islands.get(islands.size() - 1)));
            return result.toString();
        }
    }

    @Override
    public Island read(JsonReader jsonReader) throws IOException {
        TowerType tower = null;
        int numTowers = 0;
        int numNoEntryTiles = 0;
        boolean motherNaturePresent = false;
        String students = "";
        jsonReader.beginObject();
        while (jsonReader.hasNext() && jsonReader.peek() != JsonToken.END_OBJECT) {
            switch (jsonReader.nextName()) {
                case "tower" -> {
                    try {
                        tower = TowerType.valueOf(jsonReader.nextString());
                    } catch (IllegalArgumentException e) {
                        tower = null;
                    }
                }
                case "numTowers" -> numTowers = Integer.parseInt(jsonReader.nextString());
                case "noEntryTiles" -> numNoEntryTiles = Integer.parseInt(jsonReader.nextString());
                case "students" -> students = jsonReader.nextString();
                case "motherNaturePresent" -> motherNaturePresent = jsonReader.nextBoolean();
            }
        }
        jsonReader.endObject();
        String[] studentsTokens = students.split("!");
        if (studentsTokens.length != numTowers) throw new IllegalArgumentException();
        SingleIsland[] islands = new SingleIsland[numTowers];
        for (int i = 0; i < numTowers; i++) {
            SingleIsland singleIsland = new SingleIsland();
            singleIsland.setTowerType(tower);
            for (int j = 0; j < numNoEntryTiles; j++) singleIsland.insertNoEntryTile(null);
            singleIsland.setMotherNaturePresent(motherNaturePresent);
            singleIsland.setStudents(getStudents(studentsTokens[i]));
        }
        if (numTowers == 1) return islands[0];
        else return new IslandGroup(islands);
    }


    private List<Student> getStudents(String studentsString) {
        if (studentsString.isBlank() || studentsString.isEmpty()) return new ArrayList<>();
        List<Student> students = new ArrayList<>();
        Arrays.stream(studentsString.split(";")).forEach(studentType ->
                students.add(new Student(RealmType.getRealmByAbbreviation(studentType))));
        return students;
    }
}
