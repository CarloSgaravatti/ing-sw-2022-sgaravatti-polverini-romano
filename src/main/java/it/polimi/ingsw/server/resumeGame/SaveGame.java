package it.polimi.ingsw.server.resumeGame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.characters.Character1;
import it.polimi.ingsw.model.effects.InfluenceStrategy;
import it.polimi.ingsw.server.GameLobby;
import it.polimi.ingsw.utils.JsonUtils;
import org.apache.maven.settings.Server;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveGame {
    private final GameLobby gameLobby;
    private final String fileName;
    private final Map<String, Object> variables = new HashMap<>();
    private final File file;
    private boolean created;
    private static final String jarPathString;
    private static final GsonBuilder gsonBuilder;

    public SaveGame(int id, GameLobby gameLobby) throws IOException, URISyntaxException {
        this.gameLobby = gameLobby;
        File jarPath = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String string = jarPath.getParentFile().getAbsolutePath();
        fileName = string + "/backupGames/Game_With_ID_"+id+".json";
        new File(string + "/backupGames").mkdir();
        this.file = new File(fileName);
        created = file.createNewFile();
        System.out.println("Saved file on path: " + fileName);
    }

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

    public void createJson(){
        try {
            variables.put("Game", gameLobby.getGameController().getModel());
            variables.put("GameController", gameLobby.getGameController());

            PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8);
            writer.print(new Gson().toJson(gameLobby.getGameController()));
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public GameLobby getGameLobby() {
        return gameLobby;
    }

    public void deleteFile(){
        try {
            File file = new File(fileName);
            if (!file.delete())
                throw new Exception();

            System.out.println("Deleted file game:"+ fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public static void saveGame(PersistenceGameInfo gameInfo) throws URISyntaxException, IOException {
        File jarPath = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String string = jarPath.getParentFile().getAbsolutePath();
        String fileName = jarPathString + "/backupGames/Game_With_ID_" + gameInfo.getGameId() + ".json";
        File file = new File(fileName);
        boolean created = file.createNewFile();
        System.out.println("Saved file on path: " + fileName);
        try {
            PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8);
            Gson gson = gsonBuilder.create();
            ArrayTypeAdapter<Island> islandArrayTypeAdapter = new ArrayTypeAdapter<>(gson, gsonBuilder.create().getAdapter(Island.class), Island.class);
            writer.print(gsonBuilder.create().toJson(gameInfo));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PersistenceGameInfo getPersistenceData(int gameId) throws URISyntaxException, FileNotFoundException {
        File jarPath = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String stringPath = jarPath.getParentFile().getAbsolutePath();
        /*InputStream stream = JsonUtils.class.getResourceAsStream(jarPathString + "/backupGames/Game_With_ID_" + gameId + ".json");
        if (stream == null) return null; //TODO: exception
        InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);*/
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
            System.out.println("Deleted file of game "+ gameId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> findSavedGamesIds() {
        File gamesFolder = new File(jarPathString + "/backupGames");
        File[] listOfFiles = gamesFolder.listFiles();
        if (listOfFiles == null) return new ArrayList<>();
        List<Integer> gamesId = new ArrayList<>(listOfFiles.length);
        for (File listOfFile : listOfFiles) {
            String fileName = listOfFile.getName();
            String id = fileName.substring("Game_With_ID_".length(), fileName.length() - 1 - "json".length());
            gamesId.add(Integer.parseInt(id));
        }
        return gamesId;
    }
}
