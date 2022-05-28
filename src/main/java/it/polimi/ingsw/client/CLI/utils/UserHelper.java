package it.polimi.ingsw.client.CLI.utils;

import it.polimi.ingsw.client.modelView.ModelView;
import it.polimi.ingsw.utils.JsonUtils;
import it.polimi.ingsw.utils.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class UserHelper {
    private static String PLAY_ASSISTANT_HELP = "You have to insert the assistant id of the assistant you want to play.\n" +
            "For example: PlayAssistant 2 end";
    private static String MOVE_STUDENTS_HELP = "You have to move 3 students, you can move them to the dining (type 'ToDiningRoom')" +
            " or to an island (type 'ToIsland' and insert also an island id). Each student is identified by his" +
            " abbreviation (Y for YELLOW_GNOMES, B for BLUE_UNICORNS, G for GREEN_FROGS, R for RED_DRAGONS" +
            " and P for PINK_FAIRES.\n" +
            "For example: Move students Y ToDiningRoom B ToIsland 3 R ToDiningRoom end";
    private static String MOVE_MOTHER_NATURE_HELP = "You have to insert the number of movements that mother nature will do.\n" +
            "For example: MoveMotherNature 3 end";
    private static String PICK_FROM_CLOUD_HELP = "You have to select a cloud to take students from it to your entrance.\n" +
            "For example: PickFromCloud 1 end";
    private ModelView modelView;
    private List<String> commandsToBeHelped;
    private final Scanner sc;

    public UserHelper(ModelView modelView, Scanner sc) {
        this.modelView = modelView;
        commandsToBeHelped = JsonUtils.getRulesByDifficulty(modelView.isExpert());
        this.sc = sc;
    }

    public void onHelpRequest() {
        String command;
        do {
            System.out.println("Insert the command on which you need help, if you want to return to the game insert 'q': ");
            System.out.print("> ");
            command = sc.next();
            if (!commandsToBeHelped.contains(command) && !command.equals("q")) {
                System.out.println(Colors.RED + "Not a valid command" + Colors.RESET);
            } else if (!command.equals("q")) {
                switch (command) {
                    case "PlayAssistant" -> System.out.println(PLAY_ASSISTANT_HELP);
                    case "MoveStudents" -> System.out.println(MOVE_STUDENTS_HELP);
                    case "MoveMotherNature" -> System.out.println(MOVE_MOTHER_NATURE_HELP);
                    case "PickFromCloud" -> System.out.println(PICK_FROM_CLOUD_HELP);
                    case "PlayCharacter" -> {
                        System.out.println("Insert the character id");
                        System.out.print("> ");
                        int id;
                        try {
                            id = Integer.parseInt(sc.next());
                            if (modelView.getField().getExpertField().getCharacters().containsKey(id)) {
                                printCharacterInfo(id);
                            } else {
                                System.out.println(Colors.RED + "Not a valid character id" + Colors.RESET);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(Colors.RED + "Not a number" + Colors.RESET);
                        }
                    }
                }
            }
        } while (!command.equals("q"));
    }

    private void printCharacterInfo(int characterId) {
        Triplet<String, String, String> info = JsonUtils.getCharacterDescription(characterId);
        System.out.println("Character description: " + info.getFirst());
        System.out.println(info.getSecond());
        System.out.println("For example: " + info.getThird());
    }
}
