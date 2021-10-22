package com.booster.command.arguments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public enum TrainingSessionMode {

    FULL("f"),
    ANTONYMS("a"),
    SYNONYMS("s"),
    UNRECOGNIZED("UNRECOGNIZED");

    @Getter
    private final String mode;

    public static TrainingSessionMode fromString(String mode) {
        return Arrays.stream(values())
                .filter(trainingSessionMode -> trainingSessionMode.mode.equals(mode))
                .findFirst()
                .orElse(UNRECOGNIZED);
    }

    public static boolean isUnrecognized(String mode) {
        return fromString(mode) == UNRECOGNIZED;
    }

    public static String modesToString() {
        return Arrays.stream(values())
                .filter(m -> m != UNRECOGNIZED)
                .map(TrainingSessionMode::getMode)
                .collect(joining(","));
    }

}
