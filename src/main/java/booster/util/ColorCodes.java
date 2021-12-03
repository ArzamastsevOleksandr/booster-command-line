package booster.util;

public interface ColorCodes {

    String ANSI_RESET = "\u001B[0m";
    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";

    // todo: add background colors

    static String cyan(Object o) {
        return ANSI_CYAN + o.toString() + ANSI_RESET;
    }

    static String green(Object o) {
        return ANSI_GREEN + o.toString() + ANSI_RESET;
    }

    static String red(Object o) {
        return ANSI_RED + o.toString() + ANSI_RESET;
    }

    static String yellow(Object o) {
        return ANSI_YELLOW + o.toString() + ANSI_RESET;
    }

    static String purple(Object o) {
        return ANSI_PURPLE + o.toString() + ANSI_RESET;
    }

    static String blue(Object o) {
        return ANSI_BLUE + o.toString() + ANSI_RESET;
    }

}
