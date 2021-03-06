package cliclient.command.args;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
public enum VocabularyTrainingSessionMode {

    @Deprecated
    ANTONYMS("a"),
    SYNONYMS("s"),
    TRANSLATIONS("t"),
    UNRECOGNIZED("UNRECOGNIZED");

    // if any of the training session modes have shared values - crash the program early
    static {
        Map<String, Long> mode2Count = Arrays.stream(values())
                .map(v -> v.mode)
                .collect(groupingBy(Function.identity(), counting()));

        Predicate<Map.Entry<String, Long>> isSharedMode = e -> e.getValue() > 1;

        Map<String, Long> sharedModes = mode2Count.entrySet()
                .stream()
                .filter(isSharedMode)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!sharedModes.isEmpty()) {
            throw new AssertionError("Duplicate training session modes detected: " + sharedModes);
        }
    }

    @Getter
    private final String mode;

    public static VocabularyTrainingSessionMode fromString(String mode) {
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
                .map(VocabularyTrainingSessionMode::getMode)
                .collect(joining(","));
    }

    public static VocabularyTrainingSessionMode getDefaultMode() {
        return SYNONYMS;
    }

}
