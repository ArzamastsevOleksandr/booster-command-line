package cliclient.command;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

// todo: group commands based on functionality (HELP command will use it to print data expressively).
@RequiredArgsConstructor
public enum Command {

    HELP(Set.of("h")),

    LIST_LANGUAGES(Set.of("l")),
    ADD_LANGUAGE(Set.of("al")),
    DELETE_LANGUAGE(Set.of("dl")),

    LIST_VOCABULARY_ENTRIES(Set.of("ve")),
    DELETE_VOCABULARY_ENTRY(Set.of("dve")),
    ADD_VOCABULARY_ENTRY(Set.of("ave")),
    UPDATE_VOCABULARY_ENTRY(Set.of("uve")),

    START_VOCABULARY_TRAINING_SESSION(Set.of("stv")),

    DOWNLOAD(Set.of("dwn")),
    UPLOAD(Set.of("upl")),

    SHOW_SETTINGS(Set.of("ss")),
    ADD_SETTINGS(Set.of("as")),
    DELETE_SETTINGS(Set.of("ds")),

    LIST_NOTES(Set.of("n")),
    ADD_NOTE(Set.of("an")),
    DELETE_NOTE(Set.of("dn")),

    ADD_TAG(Set.of("at")),
    LIST_TAGS(Set.of("t")),
    // todo: DELETE_TAG
    USE_TAG(Set.of("ut")),

    MARK_VOCABULARY_ENTRY_DIFFICULT(Set.of("md")),
    MARK_VOCABULARY_ENTRY_NOT_DIFFICULT(Set.of("mnd")),

    LIST_FLAG_TYPES(Set.of("ft")),

    EXIT(Set.of("e")),
    NO_INPUT(Set.of()),

    UNRECOGNIZED(Set.of("UNRECOGNIZED"));

    // if any of the commands have shared equivalents - crash the program early
    static {
        Map<String, Long> equivalent2Count = Arrays.stream(values())
                .map(Command::getEquivalents)
                .flatMap(Set::stream)
                .collect(groupingBy(Function.identity(), counting()));

        Predicate<Map.Entry<String, Long>> isSharedEquivalent = e -> e.getValue() > 1;

        Map<String, Long> sharedEquivalents = equivalent2Count.entrySet()
                .stream()
                .filter(isSharedEquivalent)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!sharedEquivalents.isEmpty()) {
            throw new AssertionError("Duplicate commands detected: " + sharedEquivalents);
        }
    }

    static final Set<Command> NON_RECOGNIZABLE_COMMANDS = Set.of(UNRECOGNIZED, NO_INPUT);

    private final Set<String> equivalents;

    public static Command fromString(String str) {
        return Arrays.stream(values())
                .filter(command -> command.equivalents.contains(str))
                .findFirst()
                .orElse(UNRECOGNIZED);
    }

    public static boolean isRecognizable(Command command) {
        return !NON_RECOGNIZABLE_COMMANDS.contains(command);
    }

    public Set<String> getEquivalents() {
        return equivalents;
    }

}
