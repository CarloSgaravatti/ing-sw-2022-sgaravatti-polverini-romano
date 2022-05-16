package it.polimi.ingsw.client.CLI.utils;


import it.polimi.ingsw.model.enumerations.TowerType;

public class PrintIslands {
    //TODO: some notes that might improve code readability and cli performance (not only for PrintIslands)
    //  - this class will not have static methods
    //  - class will have a constructor that loads an island skeleton in a final String[][] (without dynamic parts)
    //  - method drawIsland (not static) will change the skeleton only in the dynamic parts

    public static String[][] drawIsland(int islandIndex, int numTowers,  TowerType type , int ...islandStudent){
        String[][] draw = new String[7][15];
        draw[0][4] = ""+  Colors.GREEN + "╔" + Colors.RED;
        draw[0][11] = Colors.GREEN + "╗";
        draw[1][2] = ""+  Colors.GREEN+ "╔";
        draw[3][0] = ""+  Colors.GREEN+ "╔";
        draw[4][4] = "╔";
        draw[5][13] = "╔";
        draw[4][5] = "╗";
        draw[1][12] = "╗";
        draw[2][14] = "╗";
        draw[1][4] = "╝";
        draw[3][2] = "╝";
        draw[5][4] = "╝";
        draw[5][14] = "╝";
        draw[6][13] = "╝";
        draw[5][0] = ""+  Colors.GREEN+ "╚";
        draw[6][5] = ""+  Colors.GREEN+ "╚";
        draw[1][11] = "╚";
        draw[2][12] = "╚";
        int f = 0;


        for(int i = 0; i < 7; i++ ){
            for(int j = 0 ; j < 15 ; j++){
                if(i==0 && j>=5 && j<=10){
                    draw[i][j] = "═";
                }
                if(i==1 && j==3){
                    draw[i][j] = "═";
                }
                if(i==2){
                    if(j==2){
                        draw[i][j] = ""+  Colors.GREEN+ "║";
                    }
                    if(j==4){
                        draw[i][j] = "" + Colors.RED + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==8){
                        draw[i][j] = "" + Colors.BLUE + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==13){
                        draw[i][j] = "═";
                    }
                }
                if(i==3){
                    if(j==1){
                        draw[i][j] = "═";
                    }
                    if(j==6){
                        draw[i][j] = "" + Colors.YELLOW + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==14){
                        draw[i][j] = "║";
                    }
                }
                if(i==4){
                    if(j==0) {
                        draw[i][j] =""+  Colors.GREEN+ "║";
                    }
                    if(j==10){
                        draw[i][j] = "" + Colors.PURPLE + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==14){
                        draw[i][j] = "║";
                    }
                }
                if(i==5){
                    if(j>=1 && j<=3){
                        draw[i][j] = "═";
                    }
                    if(j==5){
                        draw[i][j] = "║";
                    }
                    if(j==6){
                        draw[i][j] = " ";
                    }
                    if(j==8){
                        draw[i][j] = ""  + Colors.GREEN + islandStudent[f] + Colors.RESET + Colors.GREEN;
                        f++;
                    }
                    if(j==9){
                        draw[i][j] = " ";
                    }
                }
                if(i==6 && j>=6 && j<=12){
                    draw[i][j] = "═";
                }
            }
        }


        for(int i = 0; i < 7; i++ ) {
            for (int j = 0; j < 15; j++) {
                if(draw[i][j]==null){
                    draw[i][j] = " ";
                }
            }
        }

        if(numTowers>=1){
            if(type == TowerType.WHITE) {
                draw[4][1] = Colors.RESET + "" + numTowers;
                draw[4][2] = "♜" + Colors.GREEN;
            }else if(type == TowerType.BLACK){
                draw[4][1] = Colors.BLACK + "" + numTowers;
                draw[4][2] = "♜" + Colors.GREEN;
            }else{
                draw[4][1] = Colors.WHITE + "" + numTowers;
                draw[4][2] = "♜" + Colors.GREEN;
            }
        }

        draw[0][7] =Colors.BLACK +""+ BackgroundColors.YELLOW + "" + islandIndex + BackgroundColors.RESET +""+ Colors.RED;
        return draw;
    }
}
