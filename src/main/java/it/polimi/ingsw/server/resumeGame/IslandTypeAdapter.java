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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IslandTypeAdapter is the TypeAdapter implementation that is used to write Island instances to a json and to
 * read them from a json.
 * @see Island
 * @see SingleIsland
 * @see IslandGroup
 */
public class IslandTypeAdapter extends TypeAdapter<Island> {
    /**
     * Writes the specified island in a json that has the specified json writer
     *
     * @param jsonWriter the writer of the json file
     * @param island the island to write
     * @throws IOException if it wasn't possible to write the object in the file due to a json writer error
     * @see TypeAdapter#write(JsonWriter, Object)
     */
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

    /**
     * Returns a string representation of the students of the specified island. The string contains students abbreviations
     * separated by a ';'. If the island is an IslandGroup, students from different SingleIslands are separated by '!'.
     *
     * @param island the island
     * @return a string representation of the students of the specified island
     */
    private String getStudentsString(Island island) {
        if (island.getNumTowers() == 1) {
            StringBuilder stringBuilder = new StringBuilder();
            Student[] students = island.getStudents().toArray(new Student[0]);
            for (int i = 0; i < students.length - 1; i++) {
                stringBuilder.append(students[i].getStudentType().getAbbreviation()).append(";");
            }
            if (students.length != 0) stringBuilder.append(students[students.length - 1].getStudentType().getAbbreviation());
            return stringBuilder.toString();
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

    /**
     * Reads an island from a json that has the specified json reader. The island can be both a SingleIsland or an
     * IslandGroup
     *
     * @param jsonReader the reader of the json file
     * @return an island, which values correspond to the values read by the specified json reader
     * @throws IOException if it wasn't possible to write the object in the file due to a json reader error
     * @see TypeAdapter#read(JsonReader)
     */
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
        if (studentsTokens.length != numTowers) {
            System.out.println("error");
            throw new IllegalArgumentException();
        }
        SingleIsland[] islands = new SingleIsland[numTowers];
        for (int i = 0; i < numTowers; i++) {
            SingleIsland singleIsland = new SingleIsland();
            singleIsland.setTowerType(tower);
            for (int j = 0; j < numNoEntryTiles; j++) singleIsland.insertNoEntryTile(null);
            singleIsland.setStudents(getStudents(studentsTokens[i]));
            islands[i] = singleIsland;
        }
        if (numTowers == 1) {
            islands[0].setMotherNaturePresent(motherNaturePresent);
            return islands[0];
        }
        else return new IslandGroup(motherNaturePresent, islands);
    }

    /**
     * Returns a list of students obtained from the specified string. The string is encoded in the same format that as
     * students from SingleIsland are encoded by the <CODE>getStudentsString</CODE> method.
     *
     * @param studentsString the string representation of the students
     * @return a list of students obtained from the specified string
     * @see IslandTypeAdapter#getStudentsString(Island)
     */
    private List<Student> getStudents(String studentsString) {
        if (studentsString.isBlank() || studentsString.isEmpty()) return new ArrayList<>();
        List<Student> students = new ArrayList<>();
        Arrays.stream(studentsString.split(";")).forEach(studentType ->
                students.add(new Student(RealmType.getRealmByAbbreviation(studentType))));
        return students;
    }
}
