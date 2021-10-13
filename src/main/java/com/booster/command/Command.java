package com.booster.command;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;

@RequiredArgsConstructor
public enum Command {

    HELP(Set.of("h")),
    LIST_LANGUAGES(Set.of("l l")),
    EXIT(Set.of("e")),
    UNRECOGNIZED(Set.of("UNRECOGNIZED"));

    private final Set<String> values;

    public static Command fromString(String str) {
        return Arrays.stream(values())
                .filter(command -> command.values.contains(str))
                .findFirst()
                .orElse(UNRECOGNIZED);
    }

    public static boolean isExit(Command command) {
        return command == EXIT;
    }

    public static boolean isNotExit(Command command) {
        return !isExit(command);
    }

}
