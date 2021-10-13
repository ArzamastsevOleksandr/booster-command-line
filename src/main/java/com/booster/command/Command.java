package com.booster.command;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;

@RequiredArgsConstructor
public enum Command {

    HELP(Set.of("h")),
    LIST_LANGUAGES(Set.of("l l")),
    LIST_LANGUAGES_BEING_LEARNED(Set.of("l lbl")),
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

}
