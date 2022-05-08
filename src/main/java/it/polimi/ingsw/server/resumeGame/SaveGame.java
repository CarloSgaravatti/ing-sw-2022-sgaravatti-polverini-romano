package it.polimi.ingsw.server.resumeGame;

import com.google.gson.Gson;
import it.polimi.ingsw.server.GameLobby;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SaveGame {
    private final GameLobby gameLobby;
    private final String fileName;
    private final Map<String, Object> variables = new HashMap<>();

    public SaveGame(int id, GameLobby gameLobby) throws IOException {
        this.gameLobby = gameLobby;
        fileName = "backupGames/Game_With_ID_"+id+".json";
    }
    public void createJson(){
        try {
            for(int i = 0 ; i < gameLobby.getGameController().getModel().getNumPlayers() ; i++){
                variables.put("players_"+i+"_nickname", gameLobby.getGameController().getModel().getPlayers().get(i).getNickName());
                variables.put("players_"+i+"_coins", gameLobby.getGameController().getModel().getPlayers().get(i).getNumCoins());
                variables.put("players_"+i+"_school", gameLobby.getGameController().getModel().getPlayers().get(i).getSchool());
                variables.put("players_"+i+"_wizard", gameLobby.getGameController().getModel().getPlayers().get(i).getWizardType());
                variables.put("players_"+i+"_assistants", gameLobby.getGameController().getModel().getPlayers().get(i).getAssistants());
                variables.put("players_"+i+"_turn_effect", gameLobby.getGameController().getModel().getPlayers().get(i).getTurnEffect());

            }
            variables.put("bag", gameLobby.getGameController().getModel().getBag());
            if(gameLobby.getGameController().isExpertGame()) {
                variables.put("characterCards",gameLobby.getGameController().getModel().getCharacterCards()); // controllare se va bene
            }
            for(int j = 0; j < gameLobby.getGameController().getModel().getNumPlayers(); j++) {
                variables.put("clouds_"+j, gameLobby.getGameController().getModel().getClouds()[j]);
            }
            variables.put("coins",gameLobby.getGameController().getModel().getCoinGeneralSupply());
            variables.put("Islands", gameLobby.getGameController().getModel().getIslands()); // controllare se va bene
            variables.put("rules", gameLobby.getGameController().isExpertGame());
            variables.put("current_phase", gameLobby.getGameController().getTurnController().getCurrentPhase());
            variables.put("active_player_nickname", gameLobby.getGameController().getTurnController().getActivePlayer().getNickName());
            if(gameLobby.getGameController().getTurnController().isOrderCalculated()){
                variables.put("player_order", gameLobby.getGameController().getTurnController().getPlayerOrder());
            }
            variables.put("is_last_round", gameLobby.getGameController().getModel().isLastRound());



            Writer writer = new FileWriter(fileName);
            new Gson().toJson(variables, writer);
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public GameLobby getGameLobby() {
        return gameLobby;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}
