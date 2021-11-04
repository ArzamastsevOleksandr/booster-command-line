package com.booster.parser;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
enum FlagType {

    NAME("n"),
    ID("id"),
    LANGUAGE_ID("lid"),
    SYNONYMS("s"),
    ANTONYMS("a"),
    DESCRIPTION("d"),
    MODE("m"),
    FILE("f"),
    CONTENT("c"),
    UNKNOWN("UNKNOWN");

    final String value;

    static FlagType fromString(String s) {
        return Arrays.stream(values())
                .filter(flagType -> flagType.value.equals(s))
                .findFirst()
                .orElse(UNKNOWN);
    }

    static boolean isKnown(FlagType flagType) {
        return flagType != UNKNOWN;
    }

}
