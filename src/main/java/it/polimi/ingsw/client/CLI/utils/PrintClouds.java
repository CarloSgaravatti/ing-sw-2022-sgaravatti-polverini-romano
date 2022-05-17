package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.controller.InitController;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.enumerations.RealmType;

public class PrintClouds {



    public static String[][] drawClouds(int cloudIndex, boolean full, Student... students) {
        String[][] draw = new String[5][10];
        draw[0][0] = ""+ Colors.BLUE + "╔" + Colors.RESET;
        draw[0][9] = ""+ Colors.BLUE + "╗";
        draw[4][0] = ""+ Colors.BLUE + "╚";
        draw[4][9] = ""+ Colors.BLUE + "╝"+Colors.BLUE;

        for(int i = 0; i<5; i++){
            for(int j = 0; j<10; j++){
                if((i == 0 && j!=0 && j!=9) || (i == 4 && j!=0 && j!=9)){
                    draw[i][j] ="═";
                }
                if((j==0 && i!=0 && i!= 4) || (j== 9 && i!=0 && i!=4 )){
                    draw[i][j] ="║";
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
                if(students[f].getStudentType() == RealmType.RED_DRAGONS){
                  draw[h][l] = Colors.RED + "●" + Colors.BLUE;
                }
                if(students[f].getStudentType() == RealmType.YELLOW_GNOMES){
                    draw[h][l] = Colors.YELLOW + "●" + Colors.BLUE;

                }
                if(students[f].getStudentType() == RealmType.GREEN_FROGS){
                    draw[h][l] = Colors.GREEN + "●" + Colors.BLUE;

                }
                if(students[f].getStudentType() == RealmType.BLUE_UNICORNS){
                    draw[h][l] = Colors.BLUE + "●" + Colors.BLUE;

                }
                if(students[f].getStudentType() == RealmType.PINK_FAIRES){
                    draw[h][l] = Colors.PURPLE + "●" + Colors.BLUE;
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
