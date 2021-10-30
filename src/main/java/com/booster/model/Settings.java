package com.booster.model;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class Settings {

    Long languageBeingLearnedId;
    Long vocabularyId;

    public Optional<Long> getVocabularyId() {
        return Optional.ofNullable(vocabularyId);
    }

    public Optional<Long> getLanguageBeingLearnedId() {
        return Optional.ofNullable(languageBeingLearnedId);
    }

}
