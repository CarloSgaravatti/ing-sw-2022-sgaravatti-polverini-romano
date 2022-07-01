package it.polimi.ingsw.server.persistence;

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

/**
 * Class SaveGame contains all static methods used to save games states from all active games into files, to get games
 * data from files and to delete games data files when a game is finished or deleted. All files are created in a
 * directory named /backupGames that will be created (if not already present) in the jar local context. A file that
 * contains all participants names associated with a gameId (of a game that is currently active or that is saved, but
 * it has not been already restored) will also be added to that directory. All files created are .json files.
 * @see PersistenceGameInfo
 */
public class SaveGame {
    private static final String jarPathString;
    private static final GsonBuilder gsonBuilder;

    static {
        File jarPath;
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

    /**
     * Saves the game that have the specified persistence info in a json file. The file will be named like
     * Game_With_ID_X.json, where X is the gameId.
     *
     * @param gameInfo persistent game info
     * @throws IOException if there was an error in the file creation
     */
    public static void saveGame(PersistenceGameInfo gameInfo) throws IOException {
        String fileName = jarPathString + "/backupGames/Game_With_ID_" + gameInfo.getGameId() + ".json";
        File file = new File(fileName);
        boolean created = file.createNewFile();
        try {
            PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8);
            writer.print(gsonBuilder.create().toJson(gameInfo));
            writer.close();
        } catch (Exception e) {
            System.out.println("Wasn't able to save the game " + gameInfo.getGameId() + "on disk");
        }
    }

    /**
     * Returns the persistence data of the game that have the specified id. The data is obtained from a json file that is
     * named as Game_With_ID_X.json, where X is the gameId.
     *
     * @param gameId the id of the game
     * @return the persistence data of the game that have the specified id
     * @throws FileNotFoundException if it doesn't exist the file associated to the specified id
     * @throws IOException if it was not possible to close the file input stream
     */
    public static PersistenceGameInfo getPersistenceData(int gameId) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(jarPathString + "/backupGames/Game_With_ID_" + gameId + ".json");
        InputStreamReader streamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        PersistenceGameInfo gameInfo = gsonBuilder.create().fromJson(streamReader, PersistenceGameInfo.class);
        streamReader.close();
        return gameInfo;
    }

    /**
     * Delete the file that is associated to the specified game id
     *
     * @param gameId the id of the game to delete
     */
    public static void deletePersistenceData(int gameId) {
        File fileToDelete = new File(jarPathString + "/backupGames/Game_With_ID_" + gameId + ".json");
        try {
            if (!fileToDelete.delete()) throw new Exception();
            System.out.println("Deleted file of game " + gameId);
        } catch (Exception e) {
            System.out.println("Could not delete file of name " + fileToDelete.getName());
        }
    }

    /**
     * Save on a file named participants.json the specified game participants
     *
     * @param gamesParticipants the participants of all games that are active or that are saved but not already restored
     * @throws IOException if it wasn't possible to write the object in the file due to a json writer error
     */
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
        fileOutputStream.close();
    }

    /**
     * Returns a map of all the games that are saved in files, associated with the names of the participants
     *
     * @return a map of all the games that are saved in files, associated with the names of the participants
     * @throws IOException if it wasn't possible to write the object in the file due to a json reader error
     */
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
        fileInputStream.close();
        return participants;
    }
}
