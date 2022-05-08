package it.polimi.ingsw.client.CLI.utils;

public enum BackgroundColors {
    BLACK("\u001B[40m"),
    RED("\u001B[41m"),
    GREEN("\u001B[42m"),
    YELLOW("\u001B[43m"),
    BLUE("\u001B[44m"),
    PURPLE("\u001B[45m"),
    CYAN("\u001b[46m"),
    WHITE("\u001b[47m"),

    //Advanced colors
    BRIGHT_BLACK("\u001B[40;1m"),
    BRIGHT_RED("\u001B[41;1m"),
    BRIGHT_GREEN("\u001B[42;1m"),
    BRIGHT_YELLOW("\u001B[43;1m"),
    BRIGHT_BLUE("\u001B[44;1m"),
    BRIGHT_PURPLE("\u001B[45;1m"),
    BRIGHT_CYAN("\u001b[46;1m"),
    BRIGHT_WHITE("\u001b[47;1m");

    static final String RESET = "\u001B[0m";

    private final String escape;

    BackgroundColors(String escape) {
        this.escape = escape;
    }

    @Override
    public String toString() {
        return escape;
    }
}
