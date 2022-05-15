package it.polimi.ingsw.client.CLI.utils;

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

    Colors(String escape) {
        this.escape = escape;
    }

    @Override
    public String toString() {
        return escape;
    }
}
