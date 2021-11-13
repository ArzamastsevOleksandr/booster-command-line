package com.booster.dao.params;

import lombok.Value;

import java.util.Objects;
import java.util.Optional;

// todo: KISS
@Value
public class AddSettingsDaoParams {

    Long languageId;

    private AddSettingsDaoParams(Long languageId) {
        this.languageId = languageId;
    }

    public Optional<Long> getLanguageId() {
        return Optional.ofNullable(languageId);
    }

    public static AddSettingsDaoParams empty() {
        return new AddSettingsDaoParams(null);
    }

    public static AddSettingsDaoParams of(Long languageId) {
        return new AddSettingsDaoParams(Objects.requireNonNull(languageId, "language id can not be null"));
    }

}
