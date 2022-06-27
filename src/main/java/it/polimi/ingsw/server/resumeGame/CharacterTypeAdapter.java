package it.polimi.ingsw.server.resumeGame;

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
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterTypeAdapter extends TypeAdapter<CharacterCard> {
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

    private String getStudentsString(List<Student> students) {
        return StringUtils.join(students.stream()
                .map(student -> student.getStudentType().getAbbreviation()).toList().toArray(new String[0]), ";");
    }

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
