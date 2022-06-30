package it.polimi.ingsw.client.CLI.utils;

/**
 * Enumeration Colors provide all the constants of the unicode standard that are used
 * for changing color to text character.
 * Each constant have the corresponding unicode value associated.
 */
public enum Colors {
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),

    //Advanced colors
    BRIGHT_BLACK("\u001B[30;1m"),
    BRIGHT_RED("\u001B[31;1m"),
    BRIGHT_GREEN("\u001B[32;1m"),
    BRIGHT_YELLOW("\u001B[33;1m"),
    BRIGHT_BLUE("\u001B[34;1m"),
    BRIGHT_PURPLE("\u001B[35;1m"),
    BRIGHT_CYAN("\u001b[36;1m"),
    BRIGHT_WHITE("\u001b[37;1m");

    public static final String RESET = "\u001B[0m";

    private final String escape;

    /**
     * Constructs a new Colors with the specified code value
     *
     * @param escape the unicode string of the Colors
     */
    Colors(String escape) {
        this.escape = escape;
    }

    @Override
    public String toString() {
        return escape;
    }
}
