package com.booster.parser;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

// todo: test
// todo: custom service to allow clean unit tests?
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
    CORRECT_ANSWERS_COUNT("cac"),
    ADD_ANTONYMS("aa"),
    ADD_SYNONYMS("as"),
    REMOVE_ANTONYMS("ra"),
    REMOVE_SYNONYMS("rs"),
    PAGINATION("p"),
    SUBSTRING("ss"),
    CONTEXTS("ctx"),
    UNKNOWN("UNKNOWN");

    // if any of the flag types have shared values - crash the program early
    static {
        Map<String, Long> value2Count = Arrays.stream(values())
                .map(v -> v.value)
                .collect(groupingBy(Function.identity(), counting()));

        Predicate<Map.Entry<String, Long>> isSharedValue = e -> e.getValue() > 1;

        Map<String, Long> sharedValues = value2Count.entrySet()
                .stream()
                .filter(isSharedValue)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!sharedValues.isEmpty()) {
            throw new AssertionError("Duplicate flag types detected: " + sharedValues);
        }
    }

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
