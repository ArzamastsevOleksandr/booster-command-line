package vocabularyservice.language;

import api.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Language {
    ENGLISH("ENG"),
    GERMAN("GER");

    private final String name;

    public static Language fromString(String name) {
        return Arrays.stream(values())
                .filter(language -> language.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No language found by name: " + name));
    }

}
