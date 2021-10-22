package com.booster.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Data
@Builder
public class VocabularyEntry {

    private long id;
    private String name;
    private Timestamp createdAt;
    private int correctAnswersCount;

    // todo: feat 1 ve can belong to N v
    private String vocabularyName;

    private String definition;

    @Builder.Default
    private Set<String> synonyms = Set.of();
    @Builder.Default
    private Set<String> antonyms = Set.of();

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

}
