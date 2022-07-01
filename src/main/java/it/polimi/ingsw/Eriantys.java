package it.polimi.ingsw;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.server.Server;
import javafx.application.Application;

import java.util.Scanner;

public class Eriantys {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Insert 0 to start the server, 1 to start the client in CLI mode and 2 to start the " +
                "client in the GUI mode");
        System.out.print("> ");
        int mod = 0;
        try {
            mod = Integer.parseInt(sc.next());
        } catch (NumberFormatException e) {
            System.err.println("You have to insert a number!");
            System.exit(1);
        }
        switch (mod) {
            case 0 -> Server.main(null);
            case 1 -> CLI.main(null);
            case 2 -> {
                System.setProperty("prism.allowhidpi", "false");
                Application.launch(GUI.class);
            }
            default -> System.err.println("You have to insert 0, 1 or 2!");
        }
    }
}
