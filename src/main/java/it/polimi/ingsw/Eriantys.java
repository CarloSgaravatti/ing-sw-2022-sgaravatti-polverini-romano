package it.polimi.ingsw;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.client.CLI.CLIStub;
import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.server.Server;
import javafx.application.Application;

import java.util.Scanner;

public class Eriantys {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //Prints ...
        int mod = sc.nextInt();
        switch (mod) {
            case 0 -> Server.main(null);
            case 1 -> CLI.main(null);
            case 2 -> {
                System.setProperty("prism.allowhidpi", "false");
                Application.launch(GUI.class);
            }
            //For testing
            case 3 -> CLIStub.main(null);
            default -> System.err.println("Error");
        }
    }
}
