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
    long wordId;
    Timestamp createdAt;
    int correctAnswersCount;

//    todo: custom ds
    Long languageId;
    String languageName;

    String definition;

    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> antonyms = Set.of();
    @Builder.Default
    Set<String> contexts = Set.of();

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

}
