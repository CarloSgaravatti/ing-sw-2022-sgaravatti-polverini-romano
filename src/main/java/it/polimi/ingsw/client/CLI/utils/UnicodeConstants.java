package it.polimi.ingsw.client.CLI.utils;

/**
 * Enumeration UnicodeConstants provide all the constants of the unicode standard that are used
 * for printing the CLI board. Each constant have the corresponding unicode value associated and can
 * also be associated white an ANSI color code (the unicode value will be concatenated to an ANSI code)
 */
public enum UnicodeConstants {

    CROSS("\u256c"),
    T_RIGHT("\u2560"),
    T_LEFT("\u2563"),
    T_UP("\u2569"),
    T_DOWN("\u2566"),
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
    NO_COLOR_DOT("\u25CF"),

    BLACK_TOWER(Colors.BLACK + "\u265C" + Colors.RESET),
    WHITE_TOWER(Colors.WHITE + "\u265C" + Colors.RESET),
    GREY_TOWER(Colors.CYAN + "\u265C" + Colors.RESET), //There isn't grey, for the moment I put cyan
    NO_COLOR_TOWER("\u265C");

    private final String codeValue;

    UnicodeConstants(String codeValue) {
        this.codeValue = codeValue;
    }

    @Override
    public String toString() {
        return codeValue;
    }
}
