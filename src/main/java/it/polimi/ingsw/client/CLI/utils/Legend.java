package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.model.enumerations.TowerType;

public class Legend {
    public static void printLegend(){
        String[][] legend = IslandPrinter.drawIsland(1,1, TowerType.WHITE,1,2,3,4,5);
        System.out.println("\t\t"+" Island Index");
        System.out.println("\t\t"+"       |");
        System.out.println("\t\t"+"       |");
        System.out.println("\t\t"+"       v");
        for(int i = 0; i < 7; i++){
            System.out.print("\t\t");
            for(int j = 0 ; j < 15 ; j++){
                if(i==6 && j== 2){
                    System.out.print(Colors.RESET+"^"+Colors.GREEN);
                }
                else {
                    System.out.print(legend[i][j]);
                }
            }
            if(i==3){
                System.out.print(Colors.RESET+" <--- Number of Students");
            }
            if(i==4){
                System.out.print(Colors.RESET+"       Colored by Type");
            }
            System.out.println();
        }
        System.out.println("\t\t"+"  |");
        System.out.println("\t\t"+"  |");
        System.out.println("Number of Tower");
        System.out.println("Colored by Type");
    }
}
