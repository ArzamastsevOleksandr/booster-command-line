package com.booster.command;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;

// todo: group commands based on functionality (HELP command will use it to print data expressively).
@RequiredArgsConstructor
public enum Command {

    HELP(Set.of("h")),

    LIST_LANGUAGES(Set.of("l")),

    LIST_WORDS(Set.of("w")),

    LIST_VOCABULARY_ENTRIES(Set.of("ve")),
    DELETE_VOCABULARY_ENTRY(Set.of("dve")),
    ADD_VOCABULARY_ENTRY(Set.of("ave")),

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
