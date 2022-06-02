package it.polimi.ingsw.client.CLI.utils;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final List<String[][]> lobby;

    public Lobby(){
        this.lobby = createSkeleton();
    }

    //TODO: better reuse of code
    public List<String[][]> createSkeleton(){
        int count = 0;
        List<String[][]> skeleton = new ArrayList<>();
        String newGame = "NewGame";
        String[] newGameString = new String[7];
        String newGame1 = "Refresh";
        String[] refreshString = new String[7];

        String[][] setup0 = new String[5][49]; //if only one game
        String[][] setup1 = new String[4][49]; //starting if more than one game
        String[][] setup2 = new String[4][49]; //middle box in middles pages
        String[][] setup3 = new String[5][49]; //fine di una pagina mediana
        //TODO: new boxes
        String[][] setup4 = new String[5][49]; //box finale se ho 5 game(e poi basta) oppure meno di 5 game da stampare nella prima pagina
        String[][] setup5 = new String[5][49]; //fine prima pagina
        String[][] setup6 = new String[5][49]; //first box in last page if we have just one game
        String[][] setup7 = new String[5][49]; //fine della prima pagina
        String[][] setup8 = new String[5][49]; //fine dell'ultima pagina

        String[][] upperCommandBox = new String[2][49]; //se ci sono giochi
        String[][] upperCommandBox1 = new String[3][49]; //se non ci sono giochi
        String[][] lowerCommandBox = new String[3][49]; //se ci sono giochi
        //TODO: new boxew
        String[][] lowerCommandBoxLeft = new String[3][49]; // solo <5
        String[][] lowerCommandBoxRight = new String[3][49]; // solo >5

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
        for(int i = 0 ; i < 3 ; i++){
            int s = 0;
            int f = 0;
            for(int j = 0 ; j < 49; j++){
                if((i == 0 || i==2) && upperCommandBox1[i][j] == null && j<16){
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
        for(int i=0; i < 3;i++){
            for(int j=0; j<49;j++){
                if(upperCommandBox1[i][j]==null){
                    upperCommandBox1[i][j] = " ";
                }
            }
        }
        skeleton.add(upperCommandBox1);

        //upperCommandBox for when we have at least one game in lobby
        upperCommandBox[0][0] = UnicodeConstants.TOP_LEFT.toString();
        upperCommandBox[0][8] = UnicodeConstants.T_DOWN.toString();
        upperCommandBox[0][16] = UnicodeConstants.TOP_RIGHT.toString();
        for(int i = 0 ; i < 2 ; i++){
            int s = 0;
            int f = 0;
            for(int j = 0 ; j < 49; j++){
                if(i == 0 && upperCommandBox[i][j] == null && j<16){
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
        skeleton.add(upperCommandBox);

        //setup box if we have just one game
        setup0[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup0[0][8] = UnicodeConstants.CROSS.toString();
        setup0[0][16] = UnicodeConstants.T_UP.toString();
        setup0[0][23] = UnicodeConstants.T_DOWN.toString();
        setup0[0][33] = UnicodeConstants.T_DOWN.toString();
        setup0[0][48] = UnicodeConstants.TOP_RIGHT.toString();
        setup0[4][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        //setup0[4][5] = UnicodeConstants.CROSS.toString();
        setup0[4][8] = UnicodeConstants.T_UP.toString();
        setup0[4][23] = UnicodeConstants.T_UP.toString();
        setup0[4][33] = UnicodeConstants.T_UP.toString();
        //setup0[4][43] = UnicodeConstants.CROSS.toString();
        setup0[4][48] = UnicodeConstants.BOTTOM_RIGHT.toString();
        for(int i = 0; i< 48; i++){
            if(setup0[0][i]==null){
                setup0[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup0[4][i]==null){
                setup0[4][i]= UnicodeConstants.HORIZONTAL.toString();
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
        skeleton.add(setup0);

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
        skeleton.add(setup1);

        //setup box for boxes in the middle
        setup2[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup2[0][8] = UnicodeConstants.CROSS.toString();
        setup2[0][23] = UnicodeConstants.CROSS.toString();
        setup2[0][33] = UnicodeConstants.CROSS.toString();
        setup2[0][48] = UnicodeConstants.T_LEFT.toString();
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
        skeleton.add(setup2);

        //setup for last box in middles pages
        setup3[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup3[0][8] = UnicodeConstants.CROSS.toString();
        setup3[0][23] = UnicodeConstants.CROSS.toString();
        setup3[0][33] = UnicodeConstants.CROSS.toString();
        setup3[0][48] = UnicodeConstants.T_LEFT.toString();
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
                setup3[4][i]= UnicodeConstants.HORIZONTAL.toString();
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
        skeleton.add(setup3);

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
        for(int i = 0 ; i< 3;i++){
            for( int j =0;j<49;j++){
                if(lowerCommandBox[i][j]==null){
                    lowerCommandBox[i][j]= " ";
                }
            }
        }
        skeleton.add(lowerCommandBox);

        //setup for last box if we have less than 5 games or exactly five games printed in first page
        setup4[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup4[0][8] = UnicodeConstants.CROSS.toString();
        setup4[0][23] = UnicodeConstants.CROSS.toString();
        setup4[0][33] = UnicodeConstants.CROSS.toString();
        setup4[0][48] = UnicodeConstants.T_LEFT.toString();
        setup4[4][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        setup4[4][8] = UnicodeConstants.T_UP.toString();
        setup4[4][23] = UnicodeConstants.T_UP.toString();
        setup4[4][33] = UnicodeConstants.T_UP.toString();
        setup4[4][48] = UnicodeConstants.BOTTOM_RIGHT.toString();
        for(int i = 0; i< 48; i++){
            if(setup4[0][i]==null){
                setup4[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup4[4][i]==null){
                setup4[4][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j< 49; j++){
                if(setup4[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup4[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup4[i][j]= " ";
                    }
                }
            }
        }
        skeleton.add(setup4);

        //setup for last box in the first page
        setup5[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup5[0][8] = UnicodeConstants.CROSS.toString();
        setup5[0][23] = UnicodeConstants.CROSS.toString();
        setup5[0][33] = UnicodeConstants.CROSS.toString();
        setup5[0][48] = UnicodeConstants.T_LEFT.toString();
        setup5[4][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        setup5[4][8] = UnicodeConstants.T_UP.toString();
        setup5[4][23] = UnicodeConstants.T_UP.toString();
        setup5[4][33] = UnicodeConstants.T_UP.toString();
        setup5[4][43] = UnicodeConstants.T_DOWN.toString();
        setup5[4][48] = UnicodeConstants.T_LEFT.toString();
        for(int i = 0; i< 48; i++){
            if(setup5[0][i]==null){
                setup5[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup5[4][i]==null){
                setup5[4][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j< 49; j++){
                if(setup5[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup5[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup5[i][j]= " ";
                    }
                }
            }
        }
        skeleton.add(setup5);

        //setup box if we have just one game in the last page
        setup6[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup6[0][8] = UnicodeConstants.CROSS.toString();
        setup6[0][16] = UnicodeConstants.T_UP.toString();
        setup6[0][23] = UnicodeConstants.T_DOWN.toString();
        setup6[0][33] = UnicodeConstants.T_DOWN.toString();
        setup6[0][48] = UnicodeConstants.TOP_RIGHT.toString();
        setup6[4][0] = UnicodeConstants.T_RIGHT.toString();
        setup6[4][5] = UnicodeConstants.T_DOWN.toString();
        setup6[4][8] = UnicodeConstants.T_UP.toString();
        setup6[4][23] = UnicodeConstants.T_UP.toString();
        setup6[4][33] = UnicodeConstants.T_UP.toString();
        setup6[4][48] = UnicodeConstants.BOTTOM_RIGHT.toString();
        for(int i = 0; i< 48; i++){
            if(setup6[0][i]==null){
                setup6[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup6[4][i]==null){
                setup6[4][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j< 49; j++){
                if(setup6[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup6[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup6[i][j]= " ";
                    }
                }
            }
        }
        skeleton.add(setup6);

        //last box in first page
        setup7[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup7[0][8] = UnicodeConstants.CROSS.toString();
        setup7[0][23] = UnicodeConstants.CROSS.toString();
        setup7[0][33] = UnicodeConstants.CROSS.toString();
        setup7[0][48] = UnicodeConstants.T_LEFT.toString();
        setup7[4][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        setup7[4][8] = UnicodeConstants.T_UP.toString();
        setup7[4][23] = UnicodeConstants.T_UP.toString();
        setup7[4][33] = UnicodeConstants.T_UP.toString();
        setup7[4][43] = UnicodeConstants.T_DOWN.toString();
        setup7[4][48] = UnicodeConstants.T_LEFT.toString();
        for(int i = 0; i< 48; i++){
            if(setup7[0][i]==null){
                setup7[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup7[4][i]==null){
                setup7[4][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j< 49; j++){
                if(setup7[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup7[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup7[i][j]= " ";
                    }
                }
            }
        }
        skeleton.add(setup7);

        //last box in last page
        setup8[0][0] = UnicodeConstants.T_RIGHT.toString();
        setup8[0][8] = UnicodeConstants.CROSS.toString();
        setup8[0][23] = UnicodeConstants.CROSS.toString();
        setup8[0][33] = UnicodeConstants.CROSS.toString();
        setup8[0][48] = UnicodeConstants.T_LEFT.toString();
        setup8[4][0] = UnicodeConstants.T_RIGHT.toString();
        setup8[4][5] = UnicodeConstants.T_DOWN.toString();
        setup8[4][8] = UnicodeConstants.T_UP.toString();
        setup8[4][23] = UnicodeConstants.T_UP.toString();
        setup8[4][33] = UnicodeConstants.T_UP.toString();
        setup8[4][48] = UnicodeConstants.BOTTOM_RIGHT.toString();
        for(int i = 0; i< 48; i++){
            if(setup8[0][i]==null){
                setup8[0][i]= UnicodeConstants.HORIZONTAL.toString();
            }
            if(setup8[4][i]==null){
                setup8[4][i]= UnicodeConstants.HORIZONTAL.toString();
            }
        }
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j< 49; j++){
                if(setup8[i][j]==null){
                    if(j==0 || j==48 || j==8 || j==23 || j==33){
                        setup8[i][j]=UnicodeConstants.VERTICAL.toString();
                    }
                    else{
                        setup8[i][j]= " ";
                    }
                }
            }
        }
        skeleton.add(setup8);

        //lowerCommandBox appear when we are in the last page
        lowerCommandBoxLeft[1][0] = UnicodeConstants.BOTTOM_LEFT.toString();
        lowerCommandBoxLeft[1][5] = UnicodeConstants.BOTTOM_RIGHT.toString();
        lowerCommandBoxLeft[0][2] = "<";
        lowerCommandBoxLeft[0][3] = "5";
        for(int i = 0; i < 2; i++){
            for(int j = 0 ; j < 49 ; j++){
                if(i==0){
                    if(j==0 || j==5){
                        lowerCommandBoxLeft[i][j] = UnicodeConstants.VERTICAL.toString();
                    }
                }
                else{
                    if((j>0 && j<5)){
                        lowerCommandBoxLeft[i][j] = UnicodeConstants.HORIZONTAL.toString();
                    }
                }
            }
        }
        for(int i = 0 ; i< 3;i++){
            for( int j =0;j<49;j++){
                if(lowerCommandBoxLeft[i][j]==null){
                    lowerCommandBoxLeft[i][j]= " ";
                }
            }
        }
        skeleton.add(lowerCommandBoxLeft);

        //lowerCommandBox appear when we are in the first page
        lowerCommandBoxRight[1][48] = UnicodeConstants.BOTTOM_RIGHT.toString();
        lowerCommandBoxRight[1][43] = UnicodeConstants.BOTTOM_LEFT.toString();
        lowerCommandBoxRight[0][45] = "5";
        lowerCommandBoxRight[0][46] = ">";
        for(int i = 0; i < 2; i++){
            for(int j = 0 ; j < 49 ; j++){
                if(i==0){
                    if(j==43 || j==48){
                        lowerCommandBoxRight[i][j] = UnicodeConstants.VERTICAL.toString();
                    }
                }
                else{
                    if((j>43 && j<48)){
                        lowerCommandBoxRight[i][j] = UnicodeConstants.HORIZONTAL.toString();
                    }
                }
            }
        }
        for(int i = 0 ; i< 3;i++){
            for( int j =0;j<49;j++){
                if(lowerCommandBoxRight[i][j]==null){
                    lowerCommandBoxRight[i][j]= " ";
                }
            }
        }
        skeleton.add(lowerCommandBoxRight);

        return skeleton;
    }

    public List<String[][]> getSkeleton(){
        return lobby;
    }

    public String[][] getUpperCommandBox() {
        return cloneMatrix(lobby.get(1));
    }

    public String[][] getUpperCommandBox1() {
        return cloneMatrix(lobby.get(0));
    }

    public String[][] getSetup0() {
        return cloneMatrix(lobby.get(2));
    }

    public String[][] getSetup1() {
        return cloneMatrix(lobby.get(3));
    }

    public String[][] getSetup2() {
        return cloneMatrix(lobby.get(4));
    }

    public String[][] getSetup3() {
        return cloneMatrix(lobby.get(5));
    }

    public String[][] getLowerCommandBox() {
        return cloneMatrix(lobby.get(6));
    }

    public String[][] getSetup4() {
        return cloneMatrix(lobby.get(7));
    }

    public String[][] getSetup5() {
        return cloneMatrix(lobby.get(8));
    }

    public String[][] getSetup6() {
        return cloneMatrix(lobby.get(9));
    }

    public String[][] getSetup7() {
        return cloneMatrix(lobby.get(10));
    }

    public String[][] getSetup8() {
        return cloneMatrix(lobby.get(11));
    }

    public String[][] getLowerCommandBoxLeft() {
        return cloneMatrix(lobby.get(12));
    }

    public String[][] getLowerCommandBoxRight() {
        return cloneMatrix(lobby.get(13));
    }

    public String[][] cloneMatrix(String[][] matrix) {
        String[][] clonedMatrix = new String[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            clonedMatrix[i] = matrix[i].clone();
        }
        return clonedMatrix;
    }
}
