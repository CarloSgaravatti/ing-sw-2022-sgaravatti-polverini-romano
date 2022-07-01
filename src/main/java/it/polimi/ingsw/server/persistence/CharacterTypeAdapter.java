package it.polimi.ingsw.server.persistence;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.CharacterCreator;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.characters.Character1;
import it.polimi.ingsw.model.characters.Character11;
import it.polimi.ingsw.model.characters.Character5;
import it.polimi.ingsw.model.characters.Character7;
import it.polimi.ingsw.model.effects.StudentContainer;
import it.polimi.ingsw.model.enumerations.RealmType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * CharacterTypeAdapter is a TypeAdapter of character cards implementation, used to write character cards into json files
 * and to read them from json files.
 * @see CharacterCard
 */
public class CharacterTypeAdapter extends TypeAdapter<CharacterCard> {
    /**
     * Writes the specified character card in a json that has the specified json writer
     *
     * @param jsonWriter the writer of the json file
     * @param characterCard the character card
     * @throws IOException if it wasn't possible to write the object in the file due to a json writer error
     * @see TypeAdapter#write(JsonWriter, Object)
     */
    @Override
    public void write(JsonWriter jsonWriter, CharacterCard characterCard) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id").value(characterCard.getId());
        jsonWriter.name("coinsPresents").value(characterCard.isCoinPresent());
        if (characterCard.getId() == 5) {
            jsonWriter.name("noEntryTiles").value(((Character5) characterCard).getNoEntryTiles());
        } else if (characterCard.getId() == 1 || characterCard.getId() == 7 || characterCard.getId() == 11) {
            switch (characterCard.getId()) {
                case 1 -> jsonWriter.name("students").value(getStudentsString(((Character1) characterCard).getStudents()));
                case 7 -> jsonWriter.name("students").value(getStudentsString(((Character7) characterCard).getStudents()));
                case 11 -> jsonWriter.name("students").value(getStudentsString(((Character11) characterCard).getStudents()));
            }
        }
        jsonWriter.endObject();
    }

    /**
     * Get a string representation of the specified list of students. The string contains all students RealmType's
     * abbreviations separated with ';' and will be used to save students that are contained in characters to the
     * json file.
     *
     * @param students the list of students
     * @return the string representation of the students
     */
    private String getStudentsString(List<Student> students) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < students.size() - 1; i++) {
            stringBuilder.append(students.get(i).getStudentType().getAbbreviation()).append(";");
        }
        if (students.size() != 0) stringBuilder.append(students.get(students.size() - 1).getStudentType().getAbbreviation());
        return stringBuilder.toString();
    }

    /**
     * Returns a character card that is created from the values that are read by the specified json reader
     *
     * @param jsonReader the reader of the json file
     * @return the character cards which value is encoded in the json file
     * @throws IOException if it wasn't possible to write the object in the file due to a json reader error
     * @see TypeAdapter#read(JsonReader)
     */
    @Override
    public CharacterCard read(JsonReader jsonReader) throws IOException {
        int id = 0;
        boolean coinsPresents = false;
        int numNoEntryTiles = 0;
        String studentsString = "";
        CharacterCard characterCard;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "id" -> id = jsonReader.nextInt();
                case "coinsPresents" -> coinsPresents = jsonReader.nextBoolean();
                case "noEntryTiles" -> numNoEntryTiles = jsonReader.nextInt();
                case "students" -> studentsString = jsonReader.nextString();
            }
        }
        jsonReader.endObject();
        if (List.of(1, 7, 11).contains(id)) {
            Student[] students;
            if (studentsString.isBlank() || studentsString.isEmpty()) students = new Student[0];
            else students = Arrays.stream(studentsString.split(";"))
                    .map(abbreviation -> new Student(RealmType.getRealmByAbbreviation(abbreviation))).toList().toArray(new Student[0]);
            characterCard = switch (id) {
                case 1 -> new Character1(new StudentContainer(4, students));
                case 7 -> new Character7(new StudentContainer(6, students));
                default -> new Character11(new StudentContainer(4, students));
            };
        } else {
            characterCard = new CharacterCreator(null).getCharacter(id);
            if (id == 5) ((Character5) characterCard).setNoEntryTiles(numNoEntryTiles);
        }
        if (coinsPresents) characterCard.putCoin();
        return characterCard;
    }
}
