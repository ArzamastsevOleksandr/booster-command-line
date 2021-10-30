package com.booster.model;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class Settings {

    Long languageId;

    public Optional<Long> getLanguageId() {
        return Optional.ofNullable(languageId);
    }

}
