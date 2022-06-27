package it.polimi.ingsw.server.resumeGame;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.effects.InfluenceStrategy;
import org.apache.maven.settings.Server;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SaveGame {
    private static final String jarPathString;
    private static final GsonBuilder gsonBuilder;

    static {
        File jarPath = null;
        try {
            jarPath = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        jarPathString = jarPath.getParentFile().getAbsolutePath();
        new File(jarPathString + "/backupGames").mkdir();
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(InfluenceStrategy.class, new InfluenceStrategyAdapter());
        gsonBuilder.registerTypeAdapter(Island.class, new IslandTypeAdapter());
        gsonBuilder.registerTypeAdapter(CharacterCard.class, new CharacterTypeAdapter());
    }

    public static void saveGame(PersistenceGameInfo gameInfo) throws URISyntaxException, IOException {
        String fileName = jarPathString + "/backupGames/Game_With_ID_" + gameInfo.getGameId() + ".json";
        File file = new File(fileName);
        boolean created = file.createNewFile();
        //System.out.println("Saved file on path: " + fileName);
        try {
            PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8);
            writer.print(gsonBuilder.create().toJson(gameInfo));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PersistenceGameInfo getPersistenceData(int gameId) throws URISyntaxException, FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(jarPathString + "/backupGames/Game_With_ID_" + gameId + ".json");
        InputStreamReader streamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        return gsonBuilder.create().fromJson(streamReader, PersistenceGameInfo.class);
    }

    public static void deletePersistenceData(int gameId) throws URISyntaxException {
        File jarPath = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String stringPath = jarPath.getParentFile().getAbsolutePath();
        File fileToDelete = new File(jarPathString + "/backupGames/Game_With_ID_" + gameId + ".json");
        try {
            if (!fileToDelete.delete()) throw new Exception();
            System.out.println("Deleted file of game " + gameId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveGameParticipants(Map<Integer, String[]> gamesParticipants) throws IOException {
        String fileName = jarPathString + "/backupGames/participants.json";
        File file = new File(fileName);
        boolean created = file.createNewFile();
        System.out.println("Saved participants on path: " + fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
        writer.setIndent("    ");
        writer.beginArray();
        for (Integer gameId: gamesParticipants.keySet()) {
            writer.beginObject();
            writer.name("id").value(gameId);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < gamesParticipants.get(gameId).length - 1; i++) {
                stringBuilder.append(gamesParticipants.get(gameId)[i]).append(" ");
            }
            stringBuilder.append(gamesParticipants.get(gameId)[gamesParticipants.get(gameId).length - 1]);
            writer.name("participants").value(stringBuilder.toString());
            writer.endObject();
        }
        writer.endArray();
        writer.close();
    }

    public static Map<Integer, String[]> getParticipants() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(jarPathString + "/backupGames/participants.json");
        JsonReader jsonReader = new JsonReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
        Map<Integer, String[]> participants = new HashMap<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            int gameId = 0;
            String participantsString = "";
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String nextName = jsonReader.nextName();
                if (nextName.equals("id")) gameId = jsonReader.nextInt();
                else if (nextName.equals("participants")) participantsString = jsonReader.nextString();
                else jsonReader.skipValue();
            }
            participants.put(gameId, participantsString.split(" "));
            jsonReader.endObject();
        }
        jsonReader.endArray();
        jsonReader.close();
        return participants;
    }
}
