package it.polimi.ingsw.server.resumeGame;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.server.GameLobby;
import org.apache.maven.settings.Server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SaveGame {
    private final GameLobby gameLobby;
    private final String fileName;
    private final Map<String, Object> variables = new HashMap<>();
    private final File file;
    private boolean created;

    public SaveGame(int id, GameLobby gameLobby) throws IOException {
        this.gameLobby = gameLobby;
        File jarPath = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath());
         String string = jarPath.getParentFile().getAbsolutePath();
        fileName = string + "/backupGames/Game_With_ID_"+id+".json";
        new File(string+ "/backupGames").mkdir();
        this.file = new File(fileName);
        created = file.createNewFile();
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
}
