package com.booster.parser;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum FlagType {

    NAME("n"),
    ID("id"),
    LANGUAGE_ID("lid"),
    SYNONYMS("s"),
    ANTONYMS("a"),
    DESCRIPTION("d"),
    MODE("m"),
    FILE("f"),
    UNKNOWN("UNKNOWN");

    private final String value;

    public static FlagType fromString(String s) {
        return Arrays.stream(values())
                .filter(flagType -> flagType.value.equals(s))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public static boolean isKnown(FlagType flagType) {
        return flagType != UNKNOWN;
    }

}
