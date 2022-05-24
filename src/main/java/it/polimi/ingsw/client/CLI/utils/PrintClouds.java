package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.model.enumerations.RealmType;

public class PrintClouds {
    public static final int CLOUD_SIZE_X = 5;
    public static final int CLOUD_SIZE_Y = 10;

    public static String[][] drawClouds(int cloudIndex, boolean full, RealmType ... students) {
        String[][] draw = new String[CLOUD_SIZE_X][CLOUD_SIZE_Y];
        draw[0][0] = Colors.BLUE + "╔" + Colors.RESET;
        draw[0][9] = Colors.BLUE + "╗" + Colors.RESET;
        draw[4][0] = Colors.BLUE + "╚";
        draw[4][9] = Colors.BLUE + "╝" + Colors.BLUE + Colors.RESET;

        for(int i = 0; i<5; i++){
            for(int j = 0; j<10; j++){
                if((i == 0 && j!=0 && j!=9) || (i == 4 && j!=0 && j!=9)){
                    draw[i][j] = "═";
                }
                if((j==0 && i!=0 && i!= 4) || (j== 9 && i!=0 && i!=4 )){
                    draw[i][j] = Colors.BLUE + "║" + Colors.RESET;
                }
                if(i!=0 && i!=4 && j!=0 && j!=9){
                    draw[i][j] =" ";
                }

            }

        }

        draw[0][4]= Colors.BLACK +""+ BackgroundColors.BLUE + "" + cloudIndex + BackgroundColors.RESET +""+ Colors.RESET;

        int f=0;
        int h=1;
        int l=3;
        if(full) {
            while (f < students.length) {
                if(students[f] == RealmType.RED_DRAGONS){
                  draw[h][l] = Colors.RED + "●" + Colors.RESET;
                }
                if(students[f] == RealmType.YELLOW_GNOMES){
                    draw[h][l] = Colors.YELLOW + "●" + Colors.RESET;

                }
                if(students[f] == RealmType.GREEN_FROGS){
                    draw[h][l] = Colors.GREEN + "●" + Colors.RESET;

                }
                if(students[f] == RealmType.BLUE_UNICORNS){
                    draw[h][l] = Colors.BLUE + "●" + Colors.RESET;

                }
                if(students[f] == RealmType.PINK_FAIRES){
                    draw[h][l] = Colors.PURPLE + "●" + Colors.RESET;
                }

                if(l == 6){
                    l=3;
                    h=3;
                }
                else if(l==3){
                    l=l*2;
                }

                f++;
            }
        }
        return draw;
    }
}
