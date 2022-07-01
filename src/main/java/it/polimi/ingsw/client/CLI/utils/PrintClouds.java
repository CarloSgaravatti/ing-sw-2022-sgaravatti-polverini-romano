package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.model.enumerations.RealmType;

/**
 * PrintClouds is used for creating the skeleton of the cloud with all dynamics information inside
 */
public class PrintClouds {
    public static final int CLOUD_SIZE_X = 5;
    public static final int CLOUD_SIZE_Y = 10;

    /**
     * drawClouds create the cloud skeleton with dynamics parts inside
     *
     * @param cloudIndex index of the cloud
     * @param full boolean value that tell if the cloud is full or empty
     * @param students students' array
     * @return the cloud skeleton with dynamics parts inside
     */
    public static String[][] drawClouds(int cloudIndex, boolean full, RealmType ... students) {
        String[][] draw = new String[CLOUD_SIZE_X][CLOUD_SIZE_Y];
        draw[0][0] = Colors.BLUE + UnicodeConstants.TOP_LEFT.toString() + Colors.RESET;
        draw[0][9] = Colors.BLUE + UnicodeConstants.TOP_RIGHT.toString() + Colors.RESET;
        draw[4][0] = Colors.BLUE + UnicodeConstants.BOTTOM_LEFT.toString();
        draw[4][9] = Colors.BLUE + UnicodeConstants.BOTTOM_RIGHT.toString() + Colors.BLUE + Colors.RESET;

        for(int i = 0; i<5; i++){
            for(int j = 0; j<10; j++){
                if((i == 0 && j!=0 && j!=9) || (i == 4 && j!=0 && j!=9)){
                    draw[i][j] = UnicodeConstants.HORIZONTAL.toString();
                }
                if((j==0 && i!=0 && i!= 4) || (j== 9 && i!=0 && i!=4 )){
                    draw[i][j] = Colors.BLUE + UnicodeConstants.VERTICAL.toString() + Colors.RESET;
                }
                if(i!=0 && i!=4 && j!=0 && j!=9){
                    draw[i][j] =" ";
                }

            }

        }

        draw[0][4]= Colors.WHITE +""+ BackgroundColors.BLUE + "" + cloudIndex + BackgroundColors.RESET +""+ Colors.RESET;

        int f=0;
        int h=1;
        int l=3;
        if(full) {
            while (f < students.length) {
                if(students[f] == RealmType.RED_DRAGONS){
                  draw[h][l] = Colors.RED + UnicodeConstants.NO_COLOR_DOT.toString() + Colors.RESET;
                }
                if(students[f] == RealmType.YELLOW_GNOMES){
                    draw[h][l] = Colors.YELLOW + UnicodeConstants.NO_COLOR_DOT.toString() + Colors.RESET;

                }
                if(students[f] == RealmType.GREEN_FROGS){
                    draw[h][l] = Colors.GREEN + UnicodeConstants.NO_COLOR_DOT.toString() + Colors.RESET;

                }
                if(students[f] == RealmType.BLUE_UNICORNS){
                    draw[h][l] = Colors.BLUE + UnicodeConstants.NO_COLOR_DOT.toString() + Colors.RESET;

                }
                if(students[f] == RealmType.PINK_FAIRES){
                    draw[h][l] = Colors.PURPLE + UnicodeConstants.NO_COLOR_DOT.toString() + Colors.RESET;
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
