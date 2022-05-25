package it.polimi.ingsw.client.CLI.utils;

import java.util.List;

public class Lobby {
    private final List<String[][]> lobby;
    private int sID=0;

    public Lobby(){
        this.lobby = createSkeleton();
    }

    public int startIndex(int id, String command){
        switch(command){
            case "<5" -> sID -= 5;
            case "5>" -> sID += 5;
        }

        return sID;
    }

    public List<String[][]> createSkeleton(){
        int count=0;
        String newGame = "NewGame";
        String[] newGameString = new String[7];
        String newGame1 = "Refresh";
        String[] refreshString = new String[7];

        String[][] setup0 = new String[5][49];
        String[][] setup1 = new String[4][49];
        String[][] setup2 = new String[4][49];
        String[][] setup3 = new String[5][49];

        String[][] upperCommandBox = new String[2][49];
        String[][] upperCommandBox1 = new String[3][49];
        String[][] lowerCommandBox = new String[3][49];

        for(int i = 0 ; i < 7; i++){
            newGameString[i]= String.valueOf(newGame.charAt(i));
            refreshString[i]= String.valueOf(newGame1.charAt(i));
        }

        //upperCommandBox for when we have zero game in lobby
        upperCommandBox1[0][0] = UnicodeConstants.TOP_LEFT.toString();
        upperCommandBox1[0][8] = UnicodeConstants.T_DOWN.toString();
        upperCommandBox1[0][16] = UnicodeConstants.TOP_RIGHT.toString();
        upperCommandBox1[2][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        upperCommandBox1[2][16] = UnicodeConstants.BOTTOM_RIGHT.toString();
        upperCommandBox1[2][8] = UnicodeConstants.T_UP.toString();
        for(int i = 0 ; i < 2 ; i++){
            int s = 0;
            int f = 0;
            for(int j = 0 ; j < 49; j++){
                if((i == 0 || i==2) && upperCommandBox1[i][j] == null){
                    upperCommandBox1[i][j] = UnicodeConstants.HORIZONTAL.toString();
                }
                if(i==1 && (j==0 || j==8 || j==16)){
                    upperCommandBox1[i][j] = UnicodeConstants.VERTICAL.toString();
                }
                if(i==1 && (j>0 && j<8)){
                    upperCommandBox1[i][j] = newGameString[s];
                    s++;
                }
                if(i==1 && (j>8 && j<16)){
                    upperCommandBox1[i][j] = refreshString[f];
                    f++;
                }
            }
        }
        for(int i=0; i < 2;i++){
            for(int j=0; j<49;j++){
                if(upperCommandBox1[i][j]==null){
                    upperCommandBox1[i][j] = " ";
                }
            }
        }
        lobby.add(upperCommandBox1);

        //upperCommandBox for when we have at least one game in lobby
        upperCommandBox[0][0] = UnicodeConstants.TOP_LEFT.toString();
        upperCommandBox[0][8] = UnicodeConstants.T_DOWN.toString();
        upperCommandBox[0][16] = UnicodeConstants.TOP_RIGHT.toString();
        for(int i = 0 ; i < 2 ; i++){
            int s = 0;
            int f = 0;
            for(int j = 0 ; j < 49; j++){
                if(i == 0 && upperCommandBox[i][j] == null){
                    upperCommandBox[i][j] = UnicodeConstants.HORIZONTAL.toString();
                }
                if(i==1 && (j==0 || j==8 || j==16)){
                    upperCommandBox[i][j] = UnicodeConstants.VERTICAL.toString();
                }
                if(i==1 && (j>0 && j<8)){
                    upperCommandBox[i][j] = newGameString[s];
                    s++;
                }
                if(i==1 && (j>8 && j<16)){
                    upperCommandBox[i][j] = refreshString[f];
                    f++;
                }
            }
        }
        for(int i=0; i < 2;i++){
            for(int j=0; j<49;j++){
                if(upperCommandBox[i][j]==null){
                    upperCommandBox[i][j] = " ";
                }
            }
        }
        lobby.add(upperCommandBox);

        //setup box if we have just one game
        setup0[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup0[0][8] = UnicodeConstants.CROSS.toString();
        setup0[0][16] = UnicodeConstants.T_UP.toString();
        setup0[0][23] = UnicodeConstants.T_DOWN.toString();
        setup0[0][33] = UnicodeConstants.T_DOWN.toString();
        setup0[0][48] = UnicodeConstants.TOP_RIGHT.toString();
        setup0[4][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        setup0[4][5] = UnicodeConstants.CROSS.toString();
        setup0[4][8] = UnicodeConstants.T_UP.toString();
        setup0[4][23] = UnicodeConstants.T_UP.toString();
        setup0[4][33] = UnicodeConstants.T_UP.toString();
        setup0[4][43] = UnicodeConstants.CROSS.toString();
        setup0[4][48] = UnicodeConstants.BOTTOM_RIGHT.toString();
        for(int i = 0; i< 48; i++){
            if(setup0[0][i]==null){
                setup0[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup0[4][i]==null){
                setup0[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j< 49; j++){
                if(setup0[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup0[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup0[i][j]= " ";
                    }
                }
            }
        }
        lobby.add(setup0);

        //setup first box if we have more than one game
        setup1[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup1[0][8] = UnicodeConstants.CROSS.toString();
        setup1[0][16] = UnicodeConstants.T_UP.toString();
        setup1[0][23] = UnicodeConstants.T_DOWN.toString();
        setup1[0][33] = UnicodeConstants.T_DOWN.toString();
        setup1[0][48] = UnicodeConstants.TOP_RIGHT.toString();
        for(int i = 0; i< 48; i++){
            if(setup1[0][i]==null){
                setup1[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 4; i++){
            for(int j = 0; j< 49; j++){
                if(setup1[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup1[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup1[i][j]= " ";
                    }
                }
            }
        }
        lobby.add(setup1);

        //setup box for boxes in the middle
        setup2[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup2[0][8] = UnicodeConstants.CROSS.toString();
        setup2[0][23] = UnicodeConstants.CROSS.toString();
        setup2[0][33] = UnicodeConstants.CROSS.toString();
        setup2[0][48] = UnicodeConstants.TOP_RIGHT.toString();
        for(int i = 0; i< 48; i++){
            if(setup2[0][i]==null){
                setup2[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 4; i++){
            for(int j = 0; j< 49; j++){
                if(setup2[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup2[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup2[i][j]= " ";
                    }
                }
            }
        }
        lobby.add(setup2);

        //setup for last box
        setup3[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup3[0][8] = UnicodeConstants.CROSS.toString();
        setup3[0][9] = UnicodeConstants.T_UP.toString();
        setup3[0][17] = UnicodeConstants.T_UP.toString();
        setup3[0][23] = UnicodeConstants.CROSS.toString();
        setup3[0][33] = UnicodeConstants.CROSS.toString();
        setup3[0][48] = UnicodeConstants.TOP_RIGHT.toString();
        setup3[4][0] = UnicodeConstants.T_RIGHT.toString();
        setup3[4][5] = UnicodeConstants.T_DOWN.toString();
        setup3[4][8] = UnicodeConstants.T_UP.toString();
        setup3[4][23] = UnicodeConstants.T_UP.toString();
        setup3[4][33] = UnicodeConstants.T_UP.toString();
        setup3[4][43] = UnicodeConstants.T_DOWN.toString();
        setup3[4][48] = UnicodeConstants.T_LEFT.toString();
        for(int i = 0; i< 48; i++){
            if(setup3[0][i]==null){
                setup3[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup3[4][i]==null){
                setup3[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j< 49; j++){
                if(setup3[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup3[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup3[i][j]= " ";
                    }
                }
            }
        }
        lobby.add(setup3);

        //lowerCommandBox appear just when we have at least one game
        lowerCommandBox[1][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        lowerCommandBox[1][48] = UnicodeConstants.BOTTOM_RIGHT.toString();
        lowerCommandBox[1][5] = UnicodeConstants.BOTTOM_RIGHT.toString();
        lowerCommandBox[1][43] = UnicodeConstants.BOTTOM_LEFT.toString();
        lowerCommandBox[0][2] = "<";
        lowerCommandBox[0][3] = "5";
        lowerCommandBox[0][45] = "5";
        lowerCommandBox[0][46] = ">";
        for(int i = 0; i < 2; i++){
            for(int j = 0 ; j < 49 ; j++){
                if(i==0){
                    if(j==0 || j==5 || j==43 || j==48){
                        lowerCommandBox[i][j] = UnicodeConstants.VERTICAL.toString();
                    }
                }
                else{
                    if((j>0 && j<5) || (j>43 && j<48)){
                        lowerCommandBox[i][j] = UnicodeConstants.HORIZONTAL.toString();
                    }
                }
            }
        }
        lobby.add(lowerCommandBox);

        return lobby;
    }
}
