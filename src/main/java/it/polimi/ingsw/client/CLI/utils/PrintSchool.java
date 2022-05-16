package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.model.enumerations.TowerType;

public class PrintSchool {
    public static String[][] drawSchool(boolean[] isProfessorPresent, int[] entranceStudents,int numToweras ,TowerType type, int... diningRoomStudents) {
        String[][] school = new String[7][35];
        school[0][0] = "╔";
        school[6][0] = "╚";
        school[0][34] = "╗";
        school[6][34] = "╝";

        for (int i = 1; i < 34; i++) {
            school[0][i] = "═";
        }

        for (int i = 1; i < 34; i++) {
            school[6][i] = "═";
        }

        int f = 0;
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 35; j++) {
                if(j==0 || j==4 || j==24 || j==29 || j==34){
                    school[i][j] = "║";
                }
                if(i==1 && j== 3){
                    school[i][j] = Colors.RED + "●"+Colors.RESET;
                }
                if(i==2 && j== 3){
                    school[i][j] = Colors.YELLOW+"●"+Colors.RESET;
                }
                if(i==3 && j== 3){
                    school[i][j] = Colors.GREEN+"●"+Colors.RESET;
                }
                if(i==4 && j== 3){
                    school[i][j] = Colors.BLUE+"●"+Colors.RESET;
                }
                if(i==5 && j== 3){
                    school[i][j] = Colors.PURPLE+"●"+Colors.RESET;
                }
                if(i==1 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";
                }
                if(i==2 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";

                }
                if(i==3 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";

                }
                if(i==4 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    f++;
                    school[i][j+1] = "x";

                }
                if(i==5 && j== 1){
                    school[i][j] =""+entranceStudents[f];
                    school[i][j+1] = "x";

                }
            }
        }

        int cont = 0;
        f = 0;
        for(int i = 1; i < 6 ; i++){
            for(int j = 0 ; j < 30; j++){
                if(i == 1 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.RED + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 2 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.YELLOW + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 3 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.GREEN + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 4 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.BLUE + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
                if(i == 5 && j>4 && j%2!=0 && j<24){
                    if(cont < diningRoomStudents[f]) {
                        school[i][j] = Colors.PURPLE + "●" + Colors.RESET;
                        cont++;
                    }
                    else{
                        school[i][j] = "●";
                    }
                }
            }
            cont=0;
            f++;
        }

        f = 0;
        for(int i = 1; i < 6; i++){
            if(i == 1) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.RED + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 2) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.YELLOW + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 3) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.GREEN + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 4) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.BLUE + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
            if(i == 5) {
                if (isProfessorPresent[f]) {
                    school[i][26] = Colors.PURPLE + "●" + Colors.RESET;
                } else {
                    school[i][26] = "●";
                }
                f++;
            }
        }

        int l=1;
        int s=31;
        cont=numToweras;
        while (cont != 0){
            if(type == TowerType.BLACK){
                school[l][s] = Colors.BLACK + "♜" + Colors.RESET;
            }
            if(type == TowerType.WHITE){
                school[l][s] = Colors.BLACK + "♜" + Colors.RESET;
            }
            if(type == TowerType.GREY){
                school[l][s] = Colors.BLACK + "♜" + Colors.RESET;
            }
            if(s==32){
                s=31;
                l++;
            }
            else{
                s++;
            }
            cont--;
        }

        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 35; j++){
                if(school[i][j]==null){
                    school[i][j] = " ";
                }
            }
        }
        return school;
    }
}
