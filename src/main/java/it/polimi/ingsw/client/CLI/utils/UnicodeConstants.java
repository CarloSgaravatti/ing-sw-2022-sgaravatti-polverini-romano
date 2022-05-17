package it.polimi.ingsw.client.CLI.utils;

public enum UnicodeConstants {
    BOTTOM_RIGHT("\u255D"),
    BOTTOM_LEFT("\u255A"),
    TOP_RIGHT("\u2557"),
    TOP_LEFT("\u2554"),
    HORIZONTAL("\u2550"),
    VERTICAL("\u2551"),

    WHITE_DOT(Colors.WHITE + "\u25CF" + Colors.RESET),
    BLUE_DOT(Colors.BLUE + "\u25CF" + Colors.RESET),
    YELLOW_DOT(Colors.YELLOW + "\u25CF" + Colors.RESET),
    RED_DOT(Colors.RED + "\u25CF" + Colors.RESET),
    GREEN_DOT(Colors.GREEN + "\u25CF" + Colors.RESET),
    PURPLE_DOT(Colors.PURPLE + "\u25CF" + Colors.RESET),

    BLACK_TOWER(Colors.BLACK + "\u265C" + Colors.RESET),
    WHITE_TOWER(Colors.WHITE + "\u265C" + Colors.RESET),
    GREY_TOWER(Colors.CYAN + "\u265C" + Colors.RESET); //There isn't grey, for the moment I put cyan

    private final String codeValue;

    UnicodeConstants(String codeValue) {
        this.codeValue = codeValue;
    }

    @Override
    public String toString() {
        return codeValue;
    }
}
