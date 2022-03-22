package cliclient.command;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
public enum FlagType {

    NAME("n"),
    LANGUAGE_NAME("ln"),
    ID("id"),
    VOCABULARY_ENTRY_ID("vid"),
    NOTE_ID("nid"),
    TAG("t"),
    SYNONYMS("syn"),
    ANTONYMS("ant"),
    DEFINITION("def"),
    MODE_VOCABULARY("mv"),
    FILE("f"),
    CONTENT("con"),
    CORRECT_ANSWERS_COUNT("cac"),
    ADD_ANTONYMS("aant"),
    ADD_SYNONYMS("asyn"),
    REMOVE_ANTONYMS("rant"),
    REMOVE_SYNONYMS("rsyn"),
    PAGINATION("pg"),
    VOCABULARY_PAGINATION("vpg"),
    NOTES_PAGINATION("npg"),
    TAGS_PAGINATION("tpg"),
    LANGUAGES_PAGINATION("lpg"),
    SUBSTRING("ss"),
    CONTEXTS("ctx"),
    ENTRIES_PER_VOCABULARY_TRAINING_SESSION("epvts"),
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

    public final String value;

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
