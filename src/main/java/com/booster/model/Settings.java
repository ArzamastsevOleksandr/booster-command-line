package com.booster.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Settings {

    Long languageBeingLearnedId;
    Long vocabularyId;

}
