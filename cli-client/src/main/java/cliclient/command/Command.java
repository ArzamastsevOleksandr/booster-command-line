package cliclient.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
public enum Command {

    HELP("h"),

    LIST_AVAILABLE_LANGUAGES("al"),
    // todo: my langs

    LIST_VOCABULARY_ENTRIES("ve"),
    DELETE_VOCABULARY_ENTRY("dve"),
    ADD_VOCABULARY_ENTRY("ave"),
    UPDATE_VOCABULARY_ENTRY("uve"),

    START_VOCABULARY_TRAINING_SESSION("svts"),

    DOWNLOAD("dwn"),
    UPLOAD("upl"),

    SHOW_SETTINGS("ss"),
    ADD_SETTINGS("as"),
    DELETE_SETTINGS("ds"),

    LIST_NOTES("n"),
    ADD_NOTE("an"),
    DELETE_NOTE("dn"),

    ADD_TAG("at"),
    LIST_TAGS("t"),
    // todo: DELETE_TAG
    USE_TAG("ut"),

    LIST_FLAG_TYPES("ft"),

    EXIT("e"),
    NO_INPUT("NO_INPUT"),

    UNRECOGNIZED("UNRECOGNIZED");

    // if any of the commands have shared equivalents - crash the program early
    static {
        Map<String, Long> equivalent2Count = Arrays.stream(values())
                .map(Command::getName)
                .collect(groupingBy(Function.identity(), counting()));

        Predicate<Map.Entry<String, Long>> isSharedEquivalent = e -> e.getValue() > 1;

        Map<String, Long> sharedEquivalents = equivalent2Count.entrySet()
                .stream()
                .filter(isSharedEquivalent)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!sharedEquivalents.isEmpty()) {
            throw new AssertionError("Duplicate command names detected: " + sharedEquivalents);
        }
    }

    private static final Set<Command> NON_RECOGNIZABLE_COMMANDS = Set.of(UNRECOGNIZED, NO_INPUT);

    @Getter
    private final String name;

    public static Command fromString(String str) {
        return Arrays.stream(values())
                .filter(command -> command.name.contains(str))
                .findFirst()
                .orElse(UNRECOGNIZED);
    }

    public static boolean isRecognizable(Command command) {
        return !NON_RECOGNIZABLE_COMMANDS.contains(command);
    }

}
