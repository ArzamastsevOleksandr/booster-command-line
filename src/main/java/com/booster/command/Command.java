package com.booster.command;

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

    LIST_WORDS(Set.of("w")),

    LIST_VOCABULARY_ENTRIES(Set.of("ve")),
    DELETE_VOCABULARY_ENTRY(Set.of("dve")),
    ADD_VOCABULARY_ENTRY(Set.of("ave")),
    UPDATE_VOCABULARY_ENTRY(Set.of("uve")),

    START_TRAINING_SESSION(Set.of("t")),

    EXPORT(Set.of("exp")),
    IMPORT(Set.of("imp")),

    SHOW_SETTINGS(Set.of("ss")),
    ADD_SETTINGS(Set.of("as")),
    DELETE_SETTINGS(Set.of("ds")),

    MARK_VOCABULARY_ENTRY_DIFFICULT(Set.of("md")),
    MARK_VOCABULARY_ENTRY_NOT_DIFFICULT(Set.of("mnd")),

    EXIT(Set.of("e")),

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

    private final Set<String> equivalents;

    public static Command fromString(String str) {
        return Arrays.stream(values())
                .filter(command -> command.equivalents.contains(str))
                .findFirst()
                .orElse(UNRECOGNIZED);
    }

    public static boolean isExit(Command command) {
        return command == EXIT;
    }

    public static boolean isNotExit(Command command) {
        return !isExit(command);
    }

    public static boolean isRecognizable(Command command) {
        return command != UNRECOGNIZED;
    }

    public Set<String> getEquivalents() {
        return equivalents;
    }

    public String extendedToString() {
        return this + " (" + String.join(",", equivalents) + ")";
    }

}
