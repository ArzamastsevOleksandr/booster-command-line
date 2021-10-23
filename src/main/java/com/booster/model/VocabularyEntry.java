package com.booster.model;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class VocabularyEntry {

    long id;
    String name;
    Timestamp createdAt;
    int correctAnswersCount;

    // todo: feat 1 ve can belong to N v
    String vocabularyName;

    String definition;

    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> antonyms = Set.of();

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

}
